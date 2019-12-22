package com.yourmother.android.worstmessengerever.screens.messenger.contacts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yourmother.android.worstmessengerever.R;
import com.yourmother.android.worstmessengerever.entities.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactsAdapter extends RecyclerView.Adapter<ContactHolder> {

    private Context mContext;
    private List<User> mContacts;
    private ContactsListFragment.Mode mFragmentMode;

    private Set<User> mSelectedUsers;

    private OnGroupCreatingListener listener;

    private boolean isSelectingStarted = false;

    public ContactsAdapter(Context context, List<User> contacts, ContactsListFragment.Mode fragmentMode) {
        mContext = context;
        mContacts = contacts;
        mFragmentMode = fragmentMode;
    }


    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.contact_item, viewGroup, false);
        return new ContactHolder(view, mFragmentMode, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder contactHolder, int i) {
        if (i == 0)
            contactHolder.bindFirst(mContext, mContacts.get(0));
        else
            contactHolder.bind(mContacts.get(i));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public void setContacts(List<User> contacts) {
        mContacts = contacts;
    }

    public boolean isSelectingStarted() {
        return isSelectingStarted;
    }

    public void setSelectingStarted(boolean selectingStarted) {
        isSelectingStarted = selectingStarted;
        listener.onCreatingStateChanged();
    }

    public void addSelectedUser(User user) {
        if (mSelectedUsers == null)
            mSelectedUsers = new HashSet<>();
        mSelectedUsers.add(user);
    }

    public void removeSelectedUser(User user) {
        if (mSelectedUsers != null)
            mSelectedUsers.remove(user);
    }

    public void clearSelectedUsers() {
        if (mSelectedUsers == null || mSelectedUsers.isEmpty())
            return;

        for (User user: mSelectedUsers)
            user.setMarked(false);

        mSelectedUsers.clear();
        notifyDataSetChanged();
    }

    public List<String> getSelectedUsers() {
//        User[] users = new User[mSelectedUsers.size()];
//        mSelectedUsers.toArray(users);
        List<String> users = new ArrayList<>();
        for (User user: mSelectedUsers)
            users.add(user.getUserUid());
        return users;
    }

    public void setOnGroupCreatingListener(OnGroupCreatingListener listener) {
        this.listener = listener;
    }

    public interface OnGroupCreatingListener {
        void onCreatingStateChanged();
    }
}
