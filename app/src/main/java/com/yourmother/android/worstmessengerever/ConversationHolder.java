package com.yourmother.android.worstmessengerever;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class ConversationHolder extends BaseViewHolder {

    private CardView mCardView;
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

        mCardView = itemView.findViewById(R.id.conversation_item_card);
        mUserTextView = itemView.findViewById(R.id.conversation_item_sender);
        mMessageTextView = itemView.findViewById(R.id.conversation_item_text);
        mConversationImage = itemView.findViewById(R.id.conversation_item_image);
    }

    public void bind(Context context, Map.Entry<User, Message> entry) {
        mUser = entry.getKey();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mUserTextView.setText(entry.getKey().getUsername());
        if (entry.getValue().getUserUid().equals(currentUser.getUid()))
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
}
