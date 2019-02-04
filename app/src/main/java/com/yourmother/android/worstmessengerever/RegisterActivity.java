package com.yourmother.android.worstmessengerever;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

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
