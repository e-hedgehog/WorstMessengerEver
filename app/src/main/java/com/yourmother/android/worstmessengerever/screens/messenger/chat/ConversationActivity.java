package com.yourmother.android.worstmessengerever.screens.messenger.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.yourmother.android.worstmessengerever.entities.GroupChat;
import com.yourmother.android.worstmessengerever.entities.User;
import com.yourmother.android.worstmessengerever.screens.base.SingleFragmentActivity;
import com.yourmother.android.worstmessengerever.screens.messenger.MessengerActivity;

public class ConversationActivity extends SingleFragmentActivity {

    public static final String EXTRA_USER =
            "com.yourmother.android.worstmessengerever.user";

    public static final String EXTRA_GROUP_CHAT =
            "com.yourmother.android.worstmessengerever.groupchat";

    private static final int NEW_INTENT_FLAGS =
            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;

    public static Intent newIntent(Context context, User user) {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra(EXTRA_USER, user);
        return intent;
    }

    public static Intent newIntent(Context context, GroupChat groupChat) {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra(EXTRA_GROUP_CHAT, groupChat);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
//        if (getIntent().hasExtra(Intent.EXTRA_USER)) {
            User user = (User) getIntent().getSerializableExtra(EXTRA_USER);
            return ConversationFragment.newInstance(user);
//        } else {
//            GroupChat groupChat = (GroupChat) getIntent().getSerializableExtra(EXTRA_GROUP_CHAT);
//            return ConversationFragment.newInstance(groupChat);
//        }
    }

    @Override
    public void onBackPressed() {
        startActivity(MessengerActivity.newIntent(this, NEW_INTENT_FLAGS));
        finish();
    }
}
