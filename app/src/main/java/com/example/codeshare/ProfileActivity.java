package com.example.codeshare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.codeshare.adapter.ProfileViewPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_cover)
    ImageView profileCover;
    @BindView(R.id.profile_image)
    CircleImageView profileImage;
    @BindView(R.id.profile_option_btn)
    Button profileOptionBtn;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.ViewPager_profile)
    ViewPager ViewPagerProfile;

    ProfileViewPagerAdapter profileViewPagerAdapter;

    // firebase-
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    StorageReference userProfileImageRef;
    DatabaseReference RootRef;

    TextView profile_name;
    String currentUserID;
    final static int Gallery_pick = 1000;
    String uid = "0";
    int current_state = 0;
    String profileUrl = "", coverUrl = "";
    ProgressDialog loadingBar;

    private SharedPreferences sharedPreferences;
    private String myprofileimage;
    private String myUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        profileViewPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager(), 1);


        ViewPagerProfile.setAdapter(profileViewPagerAdapter);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserID);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile Images");


        //  userRef = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserID);


        //init
        profile_name = (TextView) findViewById(R.id.profile_name);
        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        loadingBar = new ProgressDialog(this);


        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(ProfileActivity.this);

                //Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                //galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                //galleryIntent.setType("image/*");

              //  startActivityForResult(galleryIntent, Gallery_pick);
            }
        });

        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                  //  if (dataSnapshot.child("profileImage").getValue() != null) {

                        myprofileimage = dataSnapshot.child("image").getValue().toString();
                        myUsername = dataSnapshot.child("username").getValue().toString();
                        sharedPreferences = getPreferences(MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("firebaseProfileimage", myprofileimage);
                        editor.putString("firebaseUsername", myUsername);
                        editor.commit();
                        //String image = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();
                       // Picasso.get().load(image).placeholder(R.drawable.default_image_placeholder).into(profileImage);
                   // }
                    if(myprofileimage != null){
                        Glide.with(ProfileActivity.this).load(myprofileimage).into(profileImage);

                    }
                    else {
                        Glide.with(ProfileActivity.this).load(R.drawable.icon_profile).into(profileImage);
                    }

                }

                profile_name.setText(myUsername);
                //  uid = getIntent().getStringExtra("uid");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Log.d("Click", "Outside onactivity Result");
        if (requestCode == Gallery_pick && resultCode == RESULT_OK && data != null) {
            Uri imageUrl = data.getData();

            profileImage.setImageURI(imageUrl);
            Log.d("Click", "Crop image");
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            Log.d("Click", "Picke image");
            if (resultCode == RESULT_OK) {

                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we updating your profile image...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                Uri resultUri = result.getUri();

                profileImage.setImageURI(resultUri);

                StorageReference filepath = userProfileImageRef.child(currentUserID + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(ProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }

                    }
                });

            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();


            }


        }

    }
}








