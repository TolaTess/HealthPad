package com.tolaotesanya.healthpad.modellayer.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.helper.State;

import java.util.Map;

public interface FirebasePresenter {

    FirebaseAuthLayer getHelper();
    String getMcurrent_user_id();
    DatabaseReference getmRootRef();
    DatabaseReference getmUserDatabase();
    IntentPresenter getIntentPresenter();
    Map setupDatabaseMap(String muser_id, State mapType);
    StorageReference getmStorageRef();
    Map setupMessageChatDB(String doctor_id, String message, State mapType);
    Map registerDoctor(String firstName, String lastName,
                       String speciality, String clinicName, String location);
    Map setupUserMap(String displayName);
    Map setupPostMap(String caption, String body, String download_uri, String thumb_download_uri);

}
