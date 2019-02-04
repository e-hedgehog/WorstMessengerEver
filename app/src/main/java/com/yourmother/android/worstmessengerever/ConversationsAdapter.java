package com.yourmother.android.worstmessengerever;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationHolder> {

    private static final String TAG = "ConversationsAdapter";

    private Context mContext;
//    private Message[] mMessages;
//    private User[] mUsers;
    private List<Map.Entry<User, Message>> mConversations;

    public ConversationsAdapter(Context context, List<Map.Entry<User, Message>> conversations) {
        mContext = context;

        mConversations = conversations;
        Collections.sort(conversations, (o1, o2) ->
                (int) (o1.getValue().getDate() - o2.getValue().getDate()));
        Log.i(TAG, mConversations.toString());
//        mMessages = (Message[]) conversationsMap.values().toArray();
//        mUsers = (User[]) conversationsMap.keySet().toArray();
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
//        Log.i(TAG, Arrays.toString(mMessages));
//        Log.i(TAG, Arrays.toString(mUsers));
        Log.i(TAG, mConversations.toString() + " in adapter");
        conversationHolder.bind(mConversations.get(i));
    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    public void setConversations(List<Map.Entry<User, Message>> conversations) {
        mConversations = conversations;
    }
}
