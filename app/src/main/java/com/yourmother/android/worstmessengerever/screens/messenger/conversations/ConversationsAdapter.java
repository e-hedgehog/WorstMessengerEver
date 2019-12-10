package com.yourmother.android.worstmessengerever.screens.messenger.conversations;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yourmother.android.worstmessengerever.R;
import com.yourmother.android.worstmessengerever.entities.Message;
import com.yourmother.android.worstmessengerever.entities.User;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationHolder> {

    private static final String TAG = "ConversationsAdapter";

    private Context mContext;
    private List<Map.Entry<User, Message>> mConversations;

    public ConversationsAdapter(Context context, List<Map.Entry<User, Message>> conversations) {
        mContext = context;

        mConversations = conversations;
        Log.i(TAG, mConversations.toString());
    }

    @NonNull
    @Override
    public ConversationHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.conversation_item, viewGroup, false);
        return new ConversationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationHolder conversationHolder, int i) {
        Collections.sort(mConversations, (o1, o2) ->
                (int) (o2.getValue().getDate() - o1.getValue().getDate()));
        Log.i(TAG, mConversations.toString() + " in adapter");
        if (i == 0)
            conversationHolder.bindFirst(mContext, mConversations.get(i));
        else
            conversationHolder.bind(mContext, mConversations.get(i));
    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    public void setConversations(List<Map.Entry<User, Message>> conversations) {
        mConversations = conversations;
    }
}
