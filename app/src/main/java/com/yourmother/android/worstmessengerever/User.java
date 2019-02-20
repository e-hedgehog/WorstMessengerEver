package com.yourmother.android.worstmessengerever;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    private String mUsername;
    private String mEmail;
    private String mUserUid;
    private int mProfileImageColor;

    public User(String username, String email, String userUid, int profileImageColor) {
        mUsername = username;
        mEmail = email;
        mUserUid = userUid;
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

}
