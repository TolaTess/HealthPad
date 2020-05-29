package com.tolaotesanya.healthpad.modellayer.database;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.tolaotesanya.healthpad.helper.State;

import java.util.Map;

public interface FirebasePresenter {

    FirebaseAuthLayer getHelper();
    String getMcurrent_user_id();
    DatabaseReference getmRootRef();
    DatabaseReference getmUserDatabase();
    Map registerUser(String display_name);
    Map setupDatabaseMap(String muser_id, State mapType);
    StorageReference getmStorageRef();
    Map setupMessageChatDB(String doctor_id, String message, State mapType);

}
