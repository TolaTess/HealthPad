package com.tolaotesanya.healthpad.fragment.requests;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.modellayer.enums.State;

public interface RequestPresenter {

    DatabaseReference getmUserReqDatabase();
    DatabaseReference getmConsultReqDatabase();
    IntentPresenter getIntentPresenter();
    String getMdoctor_id();
    void loadDatabase(final Context context, String user_id, State mapType);
}
