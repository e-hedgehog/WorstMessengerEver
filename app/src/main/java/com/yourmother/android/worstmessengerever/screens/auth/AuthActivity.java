package com.yourmother.android.worstmessengerever.screens.auth;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.yourmother.android.worstmessengerever.screens.base.SingleFragmentActivity;

public class AuthActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, AuthActivity.class);
    }

    public static Intent newIntent(Context context, int flags) {
        return newIntent(context).setFlags(flags);
    }

    @Override
    protected Fragment createFragment() {
        return AuthFragment.newInstance();
    }

}
