package com.yourmother.android.worstmessengerever;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class ConversationActivity extends SingleFragmentActivity {

    public static final String EXTRA_USER =
            "com.yourmother.android.worstmessengerever.uid";
    public static final String EXTRA_FRAGMENT =
            "com.yourmother.android.worstmessengerever.fragment";

    public static Intent newIntent(Context context, User user) {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra(EXTRA_USER, user);
        return intent;
    }

    public static Intent newIntent(Context context, BaseFragment fragment, User user) {
        return newIntent(context, user).putExtra(EXTRA_FRAGMENT, fragment);
    }

    @Override
    protected Fragment createFragment() {
        User user = (User) getIntent().getSerializableExtra(EXTRA_USER);
        BaseFragment fragment = (BaseFragment) getIntent().getSerializableExtra(EXTRA_FRAGMENT);
        if (fragment == null)
            return ConversationFragment.newInstance(user);
        return ConversationFragment.newInstance(fragment, user);
    }
}
