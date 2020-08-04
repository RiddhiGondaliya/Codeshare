package com.example.codeshare.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.codeshare.R;
import com.example.codeshare.adapter.PostAdapter;
import com.example.codeshare.model.PostId;
import com.example.codeshare.model.Postmodel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class NewsFeedFragment extends Fragment  {
    Context context;
    @BindView(R.id.newsfeed1)
    RecyclerView newsfeed1;
    @BindView(R.id.newsfeedProgressBar1)
    ProgressBar newsfeedProgressBar1;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, PostsRef, LikeRef;
    private StorageReference storageReference;




    PostAdapter postAdapter;
    List<Postmodel> postModels = new ArrayList<>();
    Unbinder unbinder;


    String currentUserID;
    String Storage_path_profile = "profile Images/";
    String Storage_path_post= "post Images/";
    Boolean Likecheaker;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("User");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("posts");
        LikeRef =  FirebaseDatabase.getInstance().getReference().child("Likes");
        // Assign FirebaseStorage instance to storageReference.
        storageReference = FirebaseStorage.getInstance().getReference();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        unbinder = ButterKnife.bind(this, view);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        newsfeed1.setLayoutManager(linearLayoutManager);
        postAdapter = new PostAdapter(context, postModels);
        newsfeed1.setAdapter(postAdapter);




        PostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postModels.clear();

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()){

                    String PostId = itemSnapshot.getKey();
                    Postmodel postmodel = itemSnapshot.getValue(Postmodel.class).withId(PostId);
                    postModels.add(postmodel);
                }

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        return view;
    }

    public  void setLikeButtonStatus(final String Postkey){

        LikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(Postkey).hasChild(currentUserID)){
                    LikeRef.child(Postkey).child(currentUserID).removeValue();
                    Likecheaker = false;
                }
                else {
                    LikeRef.child(Postkey).child(currentUserID).setValue(true);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}

