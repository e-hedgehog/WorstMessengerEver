package com.yourmother.android.worstmessengerever;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MessengerActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, MessengerActivity.class);
    }

    public static Intent newIntent(Context context, int flags) {
        return newIntent(context).setFlags(flags);
    }

    @Override
    protected Fragment createFragment() {
        return MessengerFragment.newInstance();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(fragment instanceof BaseFragment) || !((BaseFragment) fragment).onBackPressed())
             super.onBackPressed();
    }
}
