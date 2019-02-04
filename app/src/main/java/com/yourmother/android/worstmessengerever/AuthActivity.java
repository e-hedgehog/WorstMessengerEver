package com.yourmother.android.worstmessengerever;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

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
