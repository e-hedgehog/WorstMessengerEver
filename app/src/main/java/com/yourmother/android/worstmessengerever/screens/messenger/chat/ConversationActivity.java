package com.yourmother.android.worstmessengerever.screens.messenger.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.yourmother.android.worstmessengerever.entities.User;
import com.yourmother.android.worstmessengerever.screens.base.SingleFragmentActivity;
import com.yourmother.android.worstmessengerever.screens.messenger.MessengerActivity;

public class ConversationActivity extends SingleFragmentActivity {

    public static final String EXTRA_USER =
            "com.yourmother.android.worstmessengerever.user";

    private static final int NEW_INTENT_FLAGS =
            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK;

    public static Intent newIntent(Context context, User user) {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra(EXTRA_USER, user);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        User user = (User) getIntent().getSerializableExtra(EXTRA_USER);
        return ConversationFragment.newInstance(user);
    }

    @Override
    public void onBackPressed() {
        startActivity(MessengerActivity.newIntent(this, NEW_INTENT_FLAGS));
        finish();
    }
}
