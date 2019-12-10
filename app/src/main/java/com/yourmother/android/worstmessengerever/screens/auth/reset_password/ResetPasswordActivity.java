package com.yourmother.android.worstmessengerever.screens.auth.reset_password;

import android.support.v4.app.Fragment;

import com.yourmother.android.worstmessengerever.screens.base.SingleFragmentActivity;

public class ResetPasswordActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return ResetPasswordFragment.newInstance();
    }
}
