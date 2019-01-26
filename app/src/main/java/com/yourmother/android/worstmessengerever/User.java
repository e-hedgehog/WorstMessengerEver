package com.yourmother.android.worstmessengerever;

public class User {

    private String mUsername;
    private String mEmail;

    public User(String username, String email) {
        mUsername = username;
        mEmail = email;
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
}
