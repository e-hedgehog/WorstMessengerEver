package com.yourmother.android.worstmessengerever.screens.messenger.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yourmother.android.worstmessengerever.R;
import com.yourmother.android.worstmessengerever.entities.Message;

import java.util.Collections;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessageHolder> {

    private List<Message> mMessagesList;
    private Context mContext;

    public MessagesAdapter(Context context, List<Message> messages) {
        mMessagesList = messages;
        mContext = context;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.message_item, viewGroup, false);


        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder messageHolder, int i) {
        Collections.sort(mMessagesList);
        if (i == 0)
            messageHolder.bindFirst(mContext, mMessagesList.get(i));
        else
            messageHolder.bind(mContext, mMessagesList.get(i));
    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }
}
