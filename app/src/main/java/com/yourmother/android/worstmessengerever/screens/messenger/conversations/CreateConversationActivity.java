package com.yourmother.android.worstmessengerever.screens.messenger.conversations;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.yourmother.android.worstmessengerever.screens.base.SingleFragmentActivity;
import com.yourmother.android.worstmessengerever.screens.messenger.contacts.ContactsListFragment;

public class CreateConversationActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return ContactsListFragment.newInstance(ContactsListFragment.Mode.CREATE_CONVERSATION);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("New Conversation");
    }
}
