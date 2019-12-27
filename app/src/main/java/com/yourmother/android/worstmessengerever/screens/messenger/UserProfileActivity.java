package com.yourmother.android.worstmessengerever.screens.messenger;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.yourmother.android.worstmessengerever.screens.base.SingleFragmentActivity;

public class UserProfileActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, UserProfileActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return UserProfileFragment.newInstance();
    }

}
