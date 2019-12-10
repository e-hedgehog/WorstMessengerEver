package com.yourmother.android.worstmessengerever.screens.messenger.contacts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yourmother.android.worstmessengerever.R;
import com.yourmother.android.worstmessengerever.entities.User;
import com.yourmother.android.worstmessengerever.screens.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class ContactsListFragment extends BaseFragment implements BaseFragment.Searchable {

    private static final String TAG = "ContactsListFragment";

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private ContactsAdapter mAdapter;

    private List<User> mUsers;

    private DatabaseReference mUsersReference;

    public static Fragment newInstance() {
        return new ContactsListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mUsers == null)
            mUsers = new ArrayList<>();

        mUsersReference = FirebaseDatabase.getInstance().getReference("users");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messenger_child,
                container, false);

        mProgressBar = view.findViewById(R.id.messenger_progress_bar);
        mRecyclerView = view.findViewById(R.id.messenger_recycler_view);

        findContacts();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();

        return view;
    }

    @Override
    public void updateUI() {
        if (mAdapter == null) {
            mAdapter = new ContactsAdapter(getActivity(), mUsers);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setContacts(mUsers);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void findContacts() {
        mUsersReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                if (!user.getUserUid().equals(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid())) {
                    mUsers.add(user);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    updateUI();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "Finding contacts cancelled");
            }
        });
    }

    @Override
    public void search(String query) {
        List<User> resultList = new ArrayList<>();
        if (query == null || query.length() == 0) {
            resultList.addAll(mUsers);
        } else {
            String searchString = query.toLowerCase().trim();
            for (User user : mUsers)
                if (user.getUsername().toLowerCase().contains(searchString) ||
                        user.getEmail().toLowerCase().contains(searchString)) {
                    resultList.add(user);
                }
        }

        mAdapter.setContacts(resultList);
        mAdapter.notifyDataSetChanged();
    }
}
