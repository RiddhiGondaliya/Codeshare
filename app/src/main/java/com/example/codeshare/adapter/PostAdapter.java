package com.example.codeshare.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.codeshare.R;
import com.example.codeshare.UploadActivity;
import com.example.codeshare.model.Postmodel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;




public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    Boolean Likechecker = false;
    String currentUserID;


    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    DatabaseReference postRefLike, LikeRef;

    private android.content.Context mContext;
    private List<Postmodel> postModels;

    public PostAdapter(android.content.Context context, List<Postmodel> postModels) {
        this.mContext = context;
        this.postModels = postModels;
    }


    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        String PostId = postModels.get(position).PostID;


        mAuth = FirebaseAuth.getInstance();
        postRefLike = FirebaseDatabase.getInstance().getReference().child("Likes");


        RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.default_image_placeholder)
                .error(R.drawable.default_image_placeholder);

        if (postModels.get(position).getPostImage() != null && !postModels.get(position).getPostImage().trim().equalsIgnoreCase("")) {
            Log.d("Image", postModels.get(position).getPostImage());
            Glide.with(mContext)
                    .load(postModels.get(position).getPostImage().trim()).apply(options)
                    .into(holder.postimage);
        } else {
            holder.postimage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.default_image_placeholder));

        }

        if (postModels.get(position).getProfileimage() != null && !postModels.get(position).getProfileimage().trim().equalsIgnoreCase("")) {

            Log.d("Image1", postModels.get(position).getProfileimage());
            Glide.with(mContext)
                    .load(postModels.get(position).getProfileimage().trim()).apply(options)
                    .into(holder.profileImage);
        } else {
            holder.profileImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.img_default_user));
        }

        holder.fullname.setText(postModels.get(position).getUsername());
        holder.date.setText(postModels.get(position).getDate());
        holder.description.setText(postModels.get(position).getDescription());

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String shareLink = postModels.get(position).getDescription();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Codeshare Wants to share the Github Link have a Look!");
                shareIntent.putExtra(Intent.EXTRA_TEXT,shareLink);
                mContext.startActivity(Intent.createChooser(shareIntent,"share via"));

            }
        });



        holder.likeImageStatus(PostId);
       holder.likeimage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Likechecker = true;

               postRefLike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(Likechecker.equals(true)){
                            if(snapshot.child(PostId).hasChild(currentUserID))
                            {

                                postRefLike.child(PostId).child(currentUserID).removeValue();
                                Likechecker= false;
                            }
                            else
                            {
                                postRefLike.child(PostId).child(currentUserID).setValue(true);
                                Likechecker= false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });


            }
        });
    }

        @Override
        public int getItemCount () {
            return postModels.size();
        }


        public class PostViewHolder extends RecyclerView.ViewHolder {


            View mView;
            ImageView postimage, profileImage;
            TextView description, fullname, time, date, showlikecount, comment ;
            ImageButton likeimage, share;
            int countLikes;


            public PostViewHolder(@NonNull View itemView) {
                super(itemView);

                currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                mContext = itemView.getContext();

                postimage = itemView.findViewById(R.id.status_image);
                profileImage = itemView.findViewById(R.id.people_image);
                description = itemView.findViewById(R.id.postdescription);
                fullname = itemView.findViewById(R.id.people_name);
                date = itemView.findViewById(R.id.date);
                likeimage = itemView.findViewById(R.id.like_img);
                showlikecount = itemView.findViewById(R.id.like_count);

                //comment = itemView.findViewById(R.id.comment_img);
                share = itemView.findViewById(R.id.share_img);

            }
             public void likeImageStatus(final String PostId){
                postRefLike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(PostId).hasChild(currentUserID)){
                            countLikes = (int) dataSnapshot.child(PostId).getChildrenCount();
                            likeimage.setImageResource(R.drawable.ic_action_liked);
                            showlikecount.setText(Integer.toString(countLikes)+ (" Likes"));
                        }
                         else {
                            countLikes = (int) dataSnapshot.child(PostId).getChildrenCount();
                            likeimage.setImageResource(R.drawable.icon_like);
                            showlikecount.setText(Integer.toString(countLikes)+ (" Likes"));

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

             }


        }

    }


