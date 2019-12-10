package com.yourmother.android.worstmessengerever.screens.auth.register;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.yourmother.android.worstmessengerever.screens.base.SingleFragmentActivity;

public class RegisterActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, RegisterActivity.class);
    }

    public static Intent newIntent(Context context, int flags) {
        return newIntent(context).setFlags(flags);
    }

    @Override
    protected Fragment createFragment() {
        return RegisterFragment.newInstance();
    }
}
