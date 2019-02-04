package com.yourmother.android.worstmessengerever;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class ConversationHolder extends RecyclerView.ViewHolder {

    private TextView mUserTextView;
    private TextView mMessageTextView;
    private ImageView mConversationImage;

    private User mUser;

    public ConversationHolder(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            activity.startActivity(ConversationActivity.newIntent(activity, mUser));
        });

        mUserTextView = itemView.findViewById(R.id.conversation_item_sender);
        mMessageTextView = itemView.findViewById(R.id.conversation_item_text);
        mConversationImage = itemView.findViewById(R.id.conversation_item_image);
    }

    public void bind(Map.Entry<User, Message> entry) {
        mUser = entry.getKey();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mUserTextView.setText(entry.getKey().getUsername());
        if (entry.getValue().getUserUid().equals(currentUser.getUid()))
            mMessageTextView.setText(String.format("You: %s", entry.getValue().getText()));
        else
            mMessageTextView.setText(entry.getValue().getText());
    }
}
