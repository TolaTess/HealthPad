package com.tolaotesanya.healthpad.activities.profile;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.helper.State;

public interface DoctorProfilePresenter {

    DatabaseReference getmConsulationsDatabase();
    DatabaseReference getmReqConsulDatabase();
    DatabaseReference getmDoctorDatabase();
    void loadDatabase(final Context context, State mapType);
    String getmCurrentuser_id();

}
