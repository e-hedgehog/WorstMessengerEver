package com.yourmother.android.worstmessengerever;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Map;

public class ConversationHolder extends BaseViewHolder {

    private static final String TAG = "ConversationHolder";

    private DatabaseReference mConversationsReference;
    private FirebaseUser mCurrentUser;

    private CardView mCardView;
    private TextView mUserTextView;
    private TextView mMessageTextView;
    private TextView mDateTextView;
    private TextView mUnseenCountTextView;
    private ImageView mConversationImage;

    private User mUser;
    private String mChatType;

    public ConversationHolder(@NonNull View itemView) {
        super(itemView);

        mConversationsReference = FirebaseDatabase.getInstance()
                .getReference("conversations");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        itemView.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            mConversationsReference.child(mChatType).child("counters")
                    .child(mCurrentUser.getUid()).setValue(0);
            mUnseenCountTextView.setVisibility(View.GONE);
            activity.startActivity(ConversationActivity.newIntent(activity, mUser));
        });

        mCardView = itemView.findViewById(R.id.conversation_item_card);
        mUserTextView = itemView.findViewById(R.id.conversation_item_sender);
        mMessageTextView = itemView.findViewById(R.id.conversation_item_text);
        mDateTextView = itemView.findViewById(R.id.conversation_item_date);
        mUnseenCountTextView = itemView.findViewById(R.id.conversation_item_unseen_count);
        mConversationImage = itemView.findViewById(R.id.conversation_item_image);
    }

    public void bind(Context context, Map.Entry<User, Message> entry) {
        mUser = entry.getKey();

        findUnseenCount();

        String firstLetter = entry.getKey().getUsername().substring(0, 1).toUpperCase();
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig().bold().endConfig()
                .buildRound(firstLetter, entry.getKey().getProfileImageColor());
        mConversationImage.setImageDrawable(drawable);

        mUserTextView.setText(entry.getKey().getUsername());
        mDateTextView.setText(DateFormat.format("HH:mm", entry.getValue().getDate()));
        if (entry.getValue().getUserUid().equals(mCurrentUser.getUid()))
            mMessageTextView.setText(String.format("You: %s", entry.getValue().getText()));
        else
            mMessageTextView.setText(entry.getValue().getText());

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mCardView.getLayoutParams();
        params.topMargin = 0;
        mCardView.setLayoutParams(params);
    }

    public void bindFirst(Context context, Map.Entry<User, Message> entry) {
        bind(context, entry);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mCardView.getLayoutParams();
        params.topMargin = dpToPx(context, R.dimen.component_margin_8dp);
        mCardView.setLayoutParams(params);
    }

    private void findUnseenCount() {
        mConversationsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            String chatType1 = mCurrentUser.getUid() + "/" + mUser.getUserUid();
            String chatType2 = mUser.getUserUid() + "/" + mCurrentUser.getUid();

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = 0;
                if (dataSnapshot.hasChild(chatType1)) {
                    mChatType = chatType1;
                    count = (long) dataSnapshot.child(chatType1).child("counters")
                            .child(mCurrentUser.getUid()).getValue();
                    Log.i(TAG, "count(type1) = " + count);
                    mUnseenCountTextView.setText(String.format(Locale.getDefault(), "+%d", count));
                } else if (dataSnapshot.hasChild(chatType2)) {
                    mChatType = chatType2;
                    count = (long) dataSnapshot.child(chatType2).child("counters")
                            .child(mCurrentUser.getUid()).getValue();
                    Log.i(TAG, "count(type2) = " + count);
                    mUnseenCountTextView.setText(String.format(Locale.getDefault(), "+%d", count));
                }

                mUnseenCountTextView.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
