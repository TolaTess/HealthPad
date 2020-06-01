package com.tolaotesanya.healthpad.fragment.chat;

import android.widget.TextView;

import com.tolaotesanya.healthpad.coordinator.IntentPresenter;

import androidx.recyclerview.widget.RecyclerView;

public interface ChatPresenter {

    IntentPresenter getIntentPresenter();
    void fetchChat(RecyclerView mConvList, final TextView noReqReceived);
    void stopAdapter();
}
