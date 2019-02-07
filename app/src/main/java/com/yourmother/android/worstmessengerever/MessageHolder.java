package com.yourmother.android.worstmessengerever;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MessageHolder extends BaseViewHolder {

    private static final String TAG = "MessageHolder";

    private TextView mMessageText;
    private TextView mMessageDate;
    private TextView mMessageUser;
    private LinearLayout mLayout;
    private CardView mCardView;

    private FirebaseUser mFirebaseUser;

    public MessageHolder(@NonNull View itemView) {
        super(itemView);

        mCardView = itemView.findViewById(R.id.message_card);
        mLayout = itemView.findViewById(R.id.message_item_layout);
        mMessageText = itemView.findViewById(R.id.message_text);
        mMessageDate = itemView.findViewById(R.id.message_date);
        mMessageUser = itemView.findViewById(R.id.message_user);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void bind(Context context, Message message) {
        Log.i(TAG, message.getUserUid() == null ? "userUid is null" : "uid not null");
        String text = message.getText();
        mMessageText.setText(message.getText());
        String date = DateFormat
                .format("HH:mm:ss", message.getDate()).toString();
        mMessageDate.setText(date);

        if (text.length() <= date.length())
            mLayout.setOrientation(LinearLayout.HORIZONTAL);
        else
            mLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mCardView.getLayoutParams();
        if (message.getUserUid().equals(mFirebaseUser.getUid())) {
            mMessageUser.setVisibility(View.GONE);
            params.gravity = Gravity.END;
            params.leftMargin = dpToPx(context, R.dimen.message_margin_48dp);
            params.rightMargin = dpToPx(context, R.dimen.component_margin_8dp);
            mCardView.setCardBackgroundColor(context
                    .getResources().getColor(R.color.colorMessage));
        } else {
            mMessageUser.setVisibility(View.VISIBLE);
            mMessageUser.setText(message.getUsername());
            params.gravity = Gravity.START;
            params.leftMargin = dpToPx(context, R.dimen.component_margin_8dp);
            params.rightMargin = dpToPx(context, R.dimen.message_margin_48dp);
            mCardView.setCardBackgroundColor(Color.WHITE);
        }
        params.topMargin = 0;
        mCardView.setLayoutParams(params);
    }

    public void bindFirst(Context context, Message message) {
        bind(context, message);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mCardView.getLayoutParams();
        if (message.getUserUid().equals(mFirebaseUser.getUid())) {
            params.topMargin = dpToPx(context, R.dimen.component_margin_8dp);
        } else
            params.topMargin = 0;

        mCardView.setLayoutParams(params);
    }
}
