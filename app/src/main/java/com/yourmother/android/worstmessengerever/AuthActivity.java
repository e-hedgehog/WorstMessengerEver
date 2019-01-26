package com.yourmother.android.worstmessengerever;

import android.support.v4.app.Fragment;

public class AuthActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return AuthFragment.newInstance();
    }

}
