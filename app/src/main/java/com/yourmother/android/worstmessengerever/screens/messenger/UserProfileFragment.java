package com.yourmother.android.worstmessengerever.screens.messenger;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yourmother.android.worstmessengerever.R;
import com.yourmother.android.worstmessengerever.entities.User;
import com.yourmother.android.worstmessengerever.screens.base.BaseFragment;

public class UserProfileFragment extends BaseFragment {

    private static final int REQUEST_IMAGE = 1;

    private TextView usernameTextView;
    private TextView emailTextView;
    private ImageView avatarImage;
    private Button changeAvatarButton;

    private User mCurrentUser;

    private DatabaseReference mUsersReference;
    private FirebaseUser mFirebaseUser;

    public static UserProfileFragment newInstance() {
        return new UserProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = firebaseAuth.getCurrentUser();
        mUsersReference = database.getReference("users");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameTextView = view.findViewById(R.id.profile_username);
        emailTextView = view.findViewById(R.id.profile_email);
        avatarImage = view.findViewById(R.id.profile_avatar);
        changeAvatarButton = view.findViewById(R.id.profile_change_avatar);
        changeAvatarButton.setOnClickListener(v -> chooseNewAvatar());

        setCurrentUserData();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == REQUEST_IMAGE) {
            Uri imageUri = data.getData();
            if (imageUri != null)
                uploadNewAvatar(imageUri);
        }
    }

    private void setCurrentUserData() {
        if (isOnline(getActivity()))
            mUsersReference.child(mFirebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            mCurrentUser = dataSnapshot.getValue(User.class);
                            usernameTextView.setText(mCurrentUser.getUsername());
                            emailTextView.setText(mCurrentUser.getEmail());

                            if (mCurrentUser.getImageUrl() != null && !mCurrentUser.getImageUrl().isEmpty())
                                Glide.with(getContext().getApplicationContext())
                                        .load(mCurrentUser.getImageUrl())
                                        .centerCrop()
                                        .fitCenter()
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(avatarImage);
                            else {
                                String firstLetter = mCurrentUser.getUsername().substring(0, 1).toUpperCase();
                                TextDrawable drawable = TextDrawable.builder()
                                        .beginConfig().bold().endConfig()
                                        .buildRound(firstLetter, mCurrentUser.getProfileImageColor());
                                avatarImage.setImageDrawable(drawable);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.i("userProfile", "Single Value Event cancelled");
                        }
                    });
    }

    private void chooseNewAvatar() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void uploadNewAvatar(Uri imageUri) {
        StorageReference storageReference =
                FirebaseStorage.getInstance()
                        .getReference(mFirebaseUser.getUid())
                        .child("avatar")
                        .child(imageUri.getLastPathSegment());

        storageReference.putFile(imageUri).continueWithTask(task -> {
            if (!task.isSuccessful())
                throw task.getException();

            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(getActivity(),
                task -> {
                    if (task.isSuccessful()) {
                        updateUserInfo(task.getResult().toString());
                    } else {
                        Log.w("userProfile", "Image upload task was not successful.",
                                task.getException());
                    }
                });
    }

    private void updateUserInfo(String imageUrl) {
        mCurrentUser.setImageUrl(imageUrl);
        mUsersReference.child(mCurrentUser.getUserUid()).setValue(mCurrentUser);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, newInstance())
                .commit();
    }
}
