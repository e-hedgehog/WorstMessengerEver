package com.yourmother.android.worstmessengerever.screens.messenger.contacts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.yourmother.android.worstmessengerever.screens.messenger.chat.ConversationActivity;
import com.yourmother.android.worstmessengerever.R;
import com.yourmother.android.worstmessengerever.entities.User;
import com.yourmother.android.worstmessengerever.screens.base.BaseViewHolder;

public class ContactHolder extends BaseViewHolder {

    private CardView mCardView;
    private ImageView mContactImage;
    private TextView mUsernameTextView;
    private TextView mEmailTextView;

    private User mUser;

    public ContactHolder(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(v -> {
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            activity.startActivity(ConversationActivity.newIntent(activity, mUser));
        });

        mCardView = itemView.findViewById(R.id.contact_item_card);
        mContactImage = itemView.findViewById(R.id.contact_item_image);
        mUsernameTextView = itemView.findViewById(R.id.contact_item_username);
        mEmailTextView = itemView.findViewById(R.id.contact_item_email);
    }

    public void bind(Context context, User user) {
        mUser = user;
        mUsernameTextView.setText(user.getUsername());
        mEmailTextView.setText(user.getEmail());

        String firstLetter = user.getUsername().substring(0, 1).toUpperCase();
        TextDrawable drawable = TextDrawable.builder()
                .beginConfig().bold().endConfig()
                .buildRound(firstLetter, user.getProfileImageColor());
        mContactImage.setImageDrawable(drawable);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mCardView.getLayoutParams();
        params.topMargin = 0;
        mCardView.setLayoutParams(params);
    }

    public void bindFirst(Context context, User user) {
        bind(context, user);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mCardView.getLayoutParams();
        params.topMargin = dpToPx(context, R.dimen.component_margin_8dp);
        mCardView.setLayoutParams(params);
    }
}
