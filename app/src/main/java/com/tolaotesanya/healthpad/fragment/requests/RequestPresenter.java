package com.tolaotesanya.healthpad.fragment.requests;

import android.content.Context;
import android.widget.TextView;

import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.modellayer.enums.State;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public interface RequestPresenter {

    void stopAdapter();
    void receivedAdapterSetup(RecyclerView mReceivedList, final TextView noReqReceived, final FragmentManager fragmentManager);
    IntentPresenter getIntentPresenter();
    void loadDatabase(final Context context, String user_id, State mapType);
}
