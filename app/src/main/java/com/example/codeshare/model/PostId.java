package com.example.codeshare.model;

import com.google.firebase.database.Exclude;

import androidx.annotation.NonNull;

public class PostId {

    @Exclude
    public String PostID;

    public <T extends PostId> T withId(@NonNull final String id){
        this.PostID = id;
        return (T) this;
    }

}
