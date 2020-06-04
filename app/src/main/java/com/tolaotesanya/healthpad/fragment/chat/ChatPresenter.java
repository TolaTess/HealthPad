package com.tolaotesanya.healthpad.fragment.chat;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public interface ChatPresenter {

    void fetchChat(RecyclerView mConvList, final TextView noReqReceived);
    void stopAdapter();
}
