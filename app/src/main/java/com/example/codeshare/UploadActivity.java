package com.example.codeshare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codeshare.model.Postmodel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UploadActivity extends AppCompatActivity {

    @BindView(R.id.privacy_spinner)
    Spinner privacySpinner;
    @BindView(R.id.postBtnTxt)
    TextView postBtnTxt;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.dialogAvatar)
    CircleImageView dialogAvatar;
    @BindView(R.id.status_edit)
    EditText statusEdit;
    @BindView(R.id.status_image)
    ImageView statusImage;
    @BindView(R.id.add_image)
    Button addImage;

    ProgressDialog progressDialog;
    private static final int Gallery_pick =1;
    private Uri ImageUri;
    private String Description;


    private StorageReference postImagesrefernace;
    private DatabaseReference userRef, postsRef;
    private FirebaseAuth mAuth;




    private  String saveCurrentDate;
    private String saveCurrentTime;
    private String postRendomName;
    private String current_user_id;
    private String imageUrl;
    private String Like;

    private Task<Uri> downloadurl;

    private List<Postmodel> postmodelList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        postImagesrefernace = FirebaseStorage.getInstance().getReference();
        userRef= FirebaseDatabase.getInstance().getReference().child("User");
        postsRef= FirebaseDatabase.getInstance().getReference().child("posts");



        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_white);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //progressbar


        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        postBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidatePostInfo();
            }
        });
    }

    private void ValidatePostInfo() {
        Description = statusEdit.getText().toString();
        if(ImageUri == null){
            Toast.makeText(this,"please select post image",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(Description)){
            Toast.makeText(this,"please share what you have done!",Toast.LENGTH_SHORT).show();
        }
        else {


            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Add new Post");
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Uploading...");
            storeImageToFirebase();
        }
    }

    private void storeImageToFirebase() {

        Calendar callforDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(callforDate.getTime());

        Calendar callforTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentDate = currentTime.format(callforDate.getTime());

        postRendomName = saveCurrentDate+saveCurrentTime;






        StorageReference filepath = postImagesrefernace.child("post Images/").child(ImageUri.getLastPathSegment()+ postRendomName + ".jpg");
        filepath.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());

                if (uriTask.isSuccessful()){
                    Uri urlImage = uriTask.getResult();
                    if (urlImage!= null){
                        imageUrl = urlImage.toString();
                        Toast.makeText(UploadActivity.this,"image uploaded successfully to the storage",Toast.LENGTH_SHORT).show();
                        SavingPostInformationToDatabse();
                    }
                    progressDialog.dismiss();

                }
                else {
                    String message = uriTask.getException().getMessage();
                    Toast.makeText(UploadActivity.this,"error occured " + message, Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void SavingPostInformationToDatabse() {
        userRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String userFullName = dataSnapshot.child("username").getValue().toString();
                    String userProfileImage = dataSnapshot.child("image").getValue().toString();


                    Postmodel postmodel=  new Postmodel();
                    postmodel.setDate(saveCurrentDate);
                    postmodel.setUid(current_user_id);
                    postmodel.setTime(saveCurrentTime);
                    postmodel.setDescription(Description);
                    postmodel.setProfileimage(userProfileImage);
                    postmodel.setUsername(userFullName);
                    postmodel.setPostImage(imageUrl);
                    postmodel.setLike(Like);


                  /*  HashMap postMap = new HashMap();
                    postMap.put("uid",current_user_id);
                    postMap.put("date",saveCurrentDate);
                    postMap.put("time",saveCurrentTime);
                    postMap.put("description",Description);
                    postMap.put("image",userProfileImage);
                    postMap.put("name",userFullName);*/

                    postsRef.child(current_user_id + postRendomName).setValue(postmodel)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()){

                                        SendUserToMainActivity();
                                        Toast.makeText(UploadActivity.this,"post is uploaded sucsessflly", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                    else {
                                        Toast.makeText(UploadActivity.this,"Error occured while uploading the post ", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                    }
                                }
                            });




                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(UploadActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_pick);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== Gallery_pick  && resultCode == RESULT_OK  && data!= null){

            ImageUri = data.getData();
            statusImage.setImageURI(ImageUri);



        }
    }
    public void postbtn(View view) {
        storeImageToFirebase();
    }
}