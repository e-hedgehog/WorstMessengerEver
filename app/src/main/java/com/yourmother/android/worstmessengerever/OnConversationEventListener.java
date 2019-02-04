package com.yourmother.android.worstmessengerever;

public interface OnConversationEventListener {
    void onConversationCreated();

    void onConversationUpdated(User user, Message message);
}
