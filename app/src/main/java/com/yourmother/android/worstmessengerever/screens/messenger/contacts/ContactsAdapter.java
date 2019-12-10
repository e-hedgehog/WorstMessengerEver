package com.yourmother.android.worstmessengerever.screens.messenger.contacts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yourmother.android.worstmessengerever.R;
import com.yourmother.android.worstmessengerever.entities.User;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactHolder> {

    private Context mContext;
    private List<User> mContacts;

    public ContactsAdapter(Context context, List<User> contacts) {
        mContext = context;
        mContacts = contacts;
    }


    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.contact_item, viewGroup, false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder contactHolder, int i) {
        if (i == 0)
            contactHolder.bindFirst(mContext, mContacts.get(0));
        else
            contactHolder.bind(mContext, mContacts.get(i));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public void setContacts(List<User> contacts) {
        mContacts = contacts;
    }
}