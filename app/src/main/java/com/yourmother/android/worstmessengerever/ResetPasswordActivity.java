package com.yourmother.android.worstmessengerever;

import android.support.v4.app.Fragment;

public class ResetPasswordActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return ResetPasswordFragment.newInstance();
    }
}
