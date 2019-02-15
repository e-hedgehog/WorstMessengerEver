package com.yourmother.android.worstmessengerever;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class ConversationActivity extends SingleFragmentActivity {

    public static final String EXTRA_USER =
            "com.yourmother.android.worstmessengerever.user";

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
}
