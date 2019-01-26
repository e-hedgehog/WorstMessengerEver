package com.yourmother.android.worstmessengerever;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessengerFragment extends BaseFragment {

    private static final String TAG = "MessengerFragment";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mMessagesReference;
    private DatabaseReference mUsersReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private List<Message> mMessagesList;

    private ProgressBar mProgressBar;
    private ImageButton mSendButton;
    private EditText mMessageEditText;
    private RecyclerView mRecyclerView;
    private MessagesAdapter mAdapter;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView mNavigationView;

    public static MessengerFragment newInstance() {

        Bundle args = new Bundle();

        MessengerFragment fragment = new MessengerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);

        Log.i(TAG, "MessengerFragment: onCreate()");

        if (mMessagesList == null)
            mMessagesList = new ArrayList<>();

        isOnline(getActivity());

        mDatabase = FirebaseDatabase.getInstance();
        mMessagesReference = mDatabase.getReference("messages");
        mUsersReference = mDatabase.getReference("users");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            startActivity(new Intent(getActivity(), AuthActivity.class));
            Log.i(TAG, "Current user is null, activity finishing");
            getActivity().finish();
        }

        mAuthStateListener = firebaseAuth -> {
            if (mAuth.getCurrentUser() == null) {
                startActivity(new Intent(getActivity(), AuthActivity.class));
                Log.i(TAG, "AuthState changed, activity finishing");
                getActivity().finish();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messenger, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = view.findViewById(R.id.messenger_toolbar);
        activity.setSupportActionBar(toolbar);

        mDrawerLayout = view.findViewById(R.id.messenger_drawer_container);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                activity, mDrawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

        mNavigationView = view.findViewById(R.id.messenger_navigation_view);
        mNavigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.sign_out:
                    mAuth.signOut();
                    Log.i(TAG, "Signed out");
                    return true;
                default:
                    return true;
            }
        });

        if (isOnline(getActivity()))
            mUsersReference.child(mFirebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User currentUser = dataSnapshot.getValue(User.class);
                            View view = mNavigationView.getHeaderView(0);
                            TextView emailTextView = view.findViewById(R.id.nav_header_email);
                            emailTextView.setText(currentUser.getEmail());
                            TextView usernameTextView = view.findViewById(R.id.nav_header_username);
                            usernameTextView.setText(currentUser.getUsername());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.i(TAG, "Single Value Event cancelled");
                        }
                    });

        mProgressBar = view.findViewById(R.id.messenger_progress_bar);
        mMessageEditText = view.findViewById(R.id.message_edittext);
        mSendButton = view.findViewById(R.id.send_button);
        mSendButton.setOnClickListener(v -> {
            if (isOnline(activity))
                mUsersReference.child(mFirebaseUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User currentUser = dataSnapshot.getValue(User.class);
                                String msg = mMessageEditText.getText().toString().trim();
                                Message message = new Message(mFirebaseUser.getUid(),
                                        currentUser.getUsername(), msg, new Date().getTime());
                                if (!msg.equals("")) {
                                    mMessagesReference.push().setValue(message);
                                    mMessageEditText.setText("");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.i(TAG, "Single Value Event cancelled");
                            }
                        });
        });

        mMessagesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mMessagesList.add(dataSnapshot.getValue(Message.class));
                mProgressBar.setVisibility(View.INVISIBLE);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.smoothScrollToPosition(mMessagesList.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        mRecyclerView = view.findViewById(R.id.messenger_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        manager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new MessagesAdapter(activity, mMessagesList);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.fragment_messenger_menu, menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (mActionBarDrawerToggle.onOptionsItemSelected(item))
//            return true;
//
//        return super.onOptionsItemSelected(item);
//    }

}
