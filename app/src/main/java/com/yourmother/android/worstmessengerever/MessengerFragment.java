package com.yourmother.android.worstmessengerever;

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MessengerFragment extends BaseFragment implements OnConversationEventListener {

    private static final String TAG = "MessengerFragment";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mMessagesReference;
    private DatabaseReference mConversationsReference;
    private DatabaseReference mUsersReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private ConversationsAdapter mAdapter;

    private List<Message> mConversationsList;
    private List<User> mConversationUsersList;
    private Map<User, Message> mConversationsMap;
    private String mCurrentUserUid;

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

        if (mConversationsList == null)
            mConversationsList = new ArrayList<>();
        if (mConversationUsersList == null)
            mConversationUsersList = new ArrayList<>();
        if (mConversationsMap == null)
            mConversationsMap = new HashMap<>();

        isOnline(getActivity());

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        if (mFirebaseUser != null)
            mCurrentUserUid = mFirebaseUser.getUid();

        mConversationsReference = mDatabase.getReference("conversations");
        mUsersReference = mDatabase.getReference("users");

        mAuthStateListener = firebaseAuth -> {
            if (mAuth.getCurrentUser() == null) {
                startActivity(AuthActivity.newIntent(getActivity()));
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

//        if (isOnline(getActivity()))
        if (mCurrentUserUid != null) {
            mUsersReference.child(mCurrentUserUid)
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

            findExistingConversationsCreatedByCurrentUser();
            findExistingConversationsCreatedBySomeone();
            Log.i(TAG, mConversationsMap.toString());
        }

        mProgressBar = view.findViewById(R.id.messenger_progress_bar);

        mRecyclerView = view.findViewById(R.id.messenger_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();


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

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new ConversationsAdapter(getActivity(),
                    new ArrayList<>(mConversationsMap.entrySet()));
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setConversations(new ArrayList<>(mConversationsMap.entrySet()));
            mAdapter.notifyDataSetChanged();
        }
    }

    private void findExistingConversationsCreatedByCurrentUser() {
        mConversationsReference.child(mCurrentUserUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Log.i(TAG, dataSnapshot.getKey() + " is the key");
                        if (dataSnapshot.exists())
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Log.i(TAG, snapshot.getKey() + "is a key");
                                findLastMessageInConversation(mCurrentUserUid, snapshot.getKey());
                            }
//                findConversationTitle(dataSnapshot.getKey());
//                mAdapter.notifyDataSetChanged();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void findExistingConversationsCreatedBySomeone() {
        mConversationsReference
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Log.i(TAG, dataSnapshot.getKey() + " is the key");
                        for (DataSnapshot snapshot: dataSnapshot.getChildren())
                            if (snapshot.hasChild(mCurrentUserUid)) {
                                Log.i(TAG, snapshot.getKey() + " is the key");
                                findLastMessageInConversation(snapshot.getKey(), mCurrentUserUid);
                            }
//                mAdapter.notifyDataSetChanged();
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void findLastMessageInConversation(String uidSender, String uidReceiver) {
        mConversationsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "Sender uid: " + uidSender + " | Receiver uid: " + uidReceiver);
                DataSnapshot lastSnapshot = getSnapshotOnLastMessage(
                        dataSnapshot.child(uidSender).child(uidReceiver));
                Message lastMessage;
                if (lastSnapshot != null) {
                    lastMessage = lastSnapshot.getValue(Message.class);
//                    mConversationsList.add(lastMessage);
                    Log.i(TAG, "Last message: " + lastMessage.getText());
                    if (uidSender.equals(mCurrentUserUid))
                        findConversationTitle(uidReceiver, lastMessage);
                    else
                        findConversationTitle(uidSender, lastMessage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "Cancelled finding last message in conversation");
            }
        });
    }

    private DataSnapshot getSnapshotOnLastMessage(DataSnapshot snapshot) {
        if (snapshot == null)
            return null;

        Iterator iterator = snapshot.getChildren().iterator();
        DataSnapshot lastSnapshot = null;
        while (iterator.hasNext()) {
            lastSnapshot = (DataSnapshot) iterator.next();
        }
        return lastSnapshot;
    }

    private Message getLastMessage(Message message1, Message message2) {
        if (message1 == null) {
            return message2;
        } else if (message2 == null) {
            return message1;
        } else if (message1.getDate() > message2.getDate()) {
            return message1;
        } else
            return message2;
    }

    private void findConversationTitle(String uid, Message message) {
        mUsersReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    mConversationsMap.put(user, message);
                    Log.i(TAG, mConversationsMap.toString());
                    updateUI();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "Finding conversation title cancelled");
            }
        });
    }

    @Override
    public void onConversationCreated() {

    }

    @Override
    public void onConversationUpdated(User user, Message message) {
        mConversationsMap.put(user, message);
        updateUI();
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
