package com.yourmother.android.worstmessengerever;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactHolder> {

    private Context mContext;
    private List<User> mUsers;

    public ContactsAdapter(Context context, List<User> users) {
        mContext = context;
        mUsers = users;
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
            contactHolder.bindFirst(mContext, mUsers.get(0));
        else
            contactHolder.bind(mContext, mUsers.get(i));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
