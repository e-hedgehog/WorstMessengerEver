package com.yourmother.android.worstmessengerever.screens.messenger.contacts;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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

    private ContactsAdapter mAdapter;

    private CardView mCardView;
    private ConstraintLayout mContainer;
    private ImageView mContactImage;
    private TextView mUsernameTextView;
    private TextView mEmailTextView;

    private User mUser;

    public ContactHolder(@NonNull View itemView, ContactsListFragment.Mode fragmentMode, ContactsAdapter adapter) {
        super(itemView);
        mAdapter = adapter;

        mCardView = itemView.findViewById(R.id.contact_item_card);
        mContainer = itemView.findViewById((R.id.contact_item_container));
        mContactImage = itemView.findViewById(R.id.contact_item_image);
        mUsernameTextView = itemView.findViewById(R.id.contact_item_username);
        mEmailTextView = itemView.findViewById(R.id.contact_item_email);

        mContainer.setOnClickListener(v -> {
            if (!adapter.isSelectingStarted()) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.startActivity(ConversationActivity.newIntent(activity, mUser));
            } else {
                chooseUser(mUser);
            }
        });

        if (fragmentMode == ContactsListFragment.Mode.CREATE_CONVERSATION) {
            mContainer.setOnLongClickListener(v -> {
                if (!adapter.isSelectingStarted()) {
                    adapter.setSelectingStarted(true);
                    chooseUser(mUser);
                } else {
                    adapter.setSelectingStarted(false);
                    adapter.clearSelectedUsers();
                }
                return true;
            });
        }
    }

    public void bind(User user) {
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

        mContainer.setBackgroundColor(user.isMarked() ? Color.CYAN : Color.WHITE);
    }

    public void bindFirst(Context context, User user) {
        bind(user);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mCardView.getLayoutParams();
        params.topMargin = dpToPx(context, R.dimen.component_margin_8dp);
        mCardView.setLayoutParams(params);
    }

    private void chooseUser(User user) {
        if (!user.isMarked()) {
            mContainer.setBackgroundColor(Color.CYAN);
            mAdapter.addSelectedUser(user);
            user.setMarked(true);
        } else {
            mContainer.setBackgroundColor(Color.WHITE);
            mAdapter.removeSelectedUser(user);
            user.setMarked(false);
        }
    }
}
