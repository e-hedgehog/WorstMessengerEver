package com.yourmother.android.worstmessengerever.screens.messenger.conversations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yourmother.android.worstmessengerever.R;
import com.yourmother.android.worstmessengerever.entities.Message;
import com.yourmother.android.worstmessengerever.entities.User;
import com.yourmother.android.worstmessengerever.screens.base.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ConversationsListFragment extends BaseFragment implements BaseFragment.Searchable {

    private static final String TAG = "ConvListFragment";

    private DatabaseReference mConversationsReference;
    private DatabaseReference mUsersReference;

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private ConversationsAdapter mAdapter;

    private Map<User, Message> mConversationsMap;
    private String mCurrentUserUid;

    public static ConversationsListFragment newInstance() {

        Bundle args = new Bundle();

        ConversationsListFragment fragment = new ConversationsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "ConversationsListFragment: onCreate()");

        if (mConversationsMap == null)
            mConversationsMap = new HashMap<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null)
            mCurrentUserUid = firebaseUser.getUid();

        mConversationsReference = database.getReference("conversations");
        mUsersReference = database.getReference("users");

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messenger_child,
                container, false);

        if (mCurrentUserUid != null) {
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
    public void updateUI() {
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
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Log.i(TAG, snapshot.getKey() + "is a key");
                                findLastMessageInConversation(mCurrentUserUid, snapshot.getKey());
                            }

                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private void findExistingConversationsCreatedBySomeone() {
        mConversationsReference
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren())
                            if (snapshot.hasChild(mCurrentUserUid)) {
                                Log.i(TAG, snapshot.getKey() + " is the key");
                                findLastMessageInConversation(snapshot.getKey(), mCurrentUserUid);
                            }

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
                        dataSnapshot.child(uidSender).child(uidReceiver).child("messages"));
                Message lastMessage;
                if (lastSnapshot != null) {
                    lastMessage = lastSnapshot.getValue(Message.class);
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
    public void search(String query) {
        List<Map.Entry<User, Message>> resultList = new ArrayList<>();

        if (query == null || query.length() == 0) {
            resultList.addAll(mConversationsMap.entrySet());
        } else {
            String searchString = query.toLowerCase().trim();
            for (Map.Entry<User, Message> entry : mConversationsMap.entrySet())
                if (entry.getKey().getUsername().toLowerCase().contains(searchString))
                    resultList.add(entry);
        }

        mAdapter.setConversations(resultList);
        mAdapter.notifyDataSetChanged();
    }
}
