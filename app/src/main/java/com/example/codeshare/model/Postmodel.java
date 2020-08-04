package com.example.codeshare.model;


public class Postmodel extends PostId{

    public String uid, time, date, description, username, postImage, profileimage, like, comment, share;

    public Postmodel() {

    }




    public Postmodel(String uid, String time, String date, String description, String username, String postImage, String profileimage, String like, String comment, String share) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.description = description;
        this.username = username;
        this.postImage = postImage;
        this.profileimage = profileimage;
        this.like = like;
        this.comment = comment;
        this.share = share;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }


    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
    }
}

