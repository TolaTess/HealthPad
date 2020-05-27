package com.tolaotesanya.healthpad.modellayer.database;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.tolaotesanya.healthpad.helper.State;

import java.util.Map;

public interface FirebasePresenter {

    FirebaseAuthLayer getHelper();
    String getMcurrent_user_id();
    DatabaseReference getmRootRef();
    DatabaseReference getmUserDatabase();
    Map registerUser(String display_name);
    Map setupDatabaseMap(String muser_id, State mapType);
    void loadDatabase(final Context context, String muser_id, State mapType);

}
