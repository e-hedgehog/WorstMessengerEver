package com.yourmother.android.worstmessengerever;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

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

public class ConversationFragment extends BaseFragment {

    private static final String TAG = "ConversationFragment";
    private static final String ARG_USER = "user";

    private ProgressBar mProgressBar;
    private ImageButton mSendButton;
    private EditText mMessageEditText;
    private RecyclerView mRecyclerView;
    private MessagesAdapter mAdapter;

    private List<Message> mMessagesList;
    private User mConversationUser;

    private DatabaseReference mConversationsReference;
    private DatabaseReference mSentMessagesReference;
    private DatabaseReference mReceivedMessagesReference;
    private DatabaseReference mUsersReference;
    private FirebaseUser mFirebaseUser;

    @NonNull
    public static ConversationFragment newInstance(User user) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);

        ConversationFragment newFragment = new ConversationFragment();
        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mMessagesList == null)
            mMessagesList = new ArrayList<>();

        mConversationUser = (User) getArguments().getSerializable(ARG_USER);
        Log.i(TAG, mConversationUser == null ? "user is null" : "user not null");

        isOnline(getActivity());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        mFirebaseUser = auth.getCurrentUser();

        mConversationsReference = database.getReference("conversations");
        mSentMessagesReference = database.getReference("conversations")
                .child(mFirebaseUser.getUid()).child(mConversationUser.getUserUid());
        mReceivedMessagesReference = database.getReference("conversations")
                .child(mConversationUser.getUserUid()).child(mFirebaseUser.getUid());
        mUsersReference = database.getReference("users");

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle(mConversationUser.getUsername());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        mProgressBar = view.findViewById(R.id.conversation_progress_bar);
        mMessageEditText = view.findViewById(R.id.message_field);
        mSendButton = view.findViewById(R.id.send_button);
        mSendButton.setOnClickListener(v -> {
            if (isOnline(getActivity()))
                mUsersReference.child(mFirebaseUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User currentUser = dataSnapshot.getValue(User.class);
                                String msg = mMessageEditText.getText().toString().trim();
                                Message message = new Message(currentUser.getUserUid(),
                                        currentUser.getUsername(), msg, new Date().getTime());
                                if (!msg.equals("")) {
                                    saveNewMessage(message);
                                    mMessageEditText.setText("");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.i(TAG, "Single Value Event cancelled");
                            }
                        });
        });

        childEventListener(mSentMessagesReference);
        childEventListener(mReceivedMessagesReference);

        mRecyclerView = view.findViewById(R.id.conversation_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new MessagesAdapter(getActivity(), mMessagesList);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

    private void saveNewMessage(Message message) {
        mConversationsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            String chatType1 = mFirebaseUser.getUid() + "/" + mConversationUser.getUserUid();
            String chatType2 = mConversationUser.getUserUid() + "/" + mFirebaseUser.getUid();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(chatType1)) {
                    mSentMessagesReference.push().setValue(message);
                } else if (dataSnapshot.hasChild(chatType2)) {
                    mReceivedMessagesReference.push().setValue(message);
                } else {
                    mSentMessagesReference.push().setValue(message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void childEventListener(DatabaseReference reference) {
        if (mMessagesList.isEmpty())
            mProgressBar.setVisibility(View.INVISIBLE);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (!dataSnapshot.exists())
                    return;

                Log.i(TAG, "onChildAdded key is " + dataSnapshot.getKey());
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
    }

}
