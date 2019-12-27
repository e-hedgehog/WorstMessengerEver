package com.yourmother.android.worstmessengerever.entities;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    private String mUsername;
    private String mEmail;
    private String mUserUid;
    private String mImageUrl;
    private int mProfileImageColor;

    private boolean isMarked = false;

    public User(String username, String email, String userUid, String imageUrl, int profileImageColor) {
        mUsername = username;
        mEmail = email;
        mUserUid = userUid;
        mImageUrl = imageUrl;
        mProfileImageColor = profileImageColor;
    }

    public User() {
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getUserUid() {
        return mUserUid;
    }

    public void setUserUid(String userUid) {
        mUserUid = userUid;
    }

    public int getProfileImageColor() {
        return mProfileImageColor;
    }

    public void setProfileImageColor(int color) {
        mProfileImageColor = color;
    }

    @NonNull
    @Override
    public String toString() {
        return mUsername;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(mUsername, user.mUsername) &&
                Objects.equals(mEmail, user.mEmail) &&
                Objects.equals(mUserUid, user.mUserUid) &&
                Objects.equals(mProfileImageColor, user.mProfileImageColor);
    }

    @Override
    public int hashCode() {

        return Objects.hash(mUsername, mEmail, mUserUid, mProfileImageColor);
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }
}
