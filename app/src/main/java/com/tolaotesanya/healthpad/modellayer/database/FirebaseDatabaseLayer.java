package com.tolaotesanya.healthpad.modellayer.database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tolaotesanya.healthpad.helper.State;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FirebaseDatabaseLayer implements FirebasePresenter {
    private static final String TAG = "FirebaseDatabaseLayer";
    private DatabaseReference mRootRef;
    private DatabaseReference mUserDatabase;
    private FirebaseAuthLayer helper;
    private String mcurrent_user_id;
    private StorageReference mStorageRef;

    public FirebaseDatabaseLayer() {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        helper = new FirebaseAuthLayer();
        helper.setupFirebase();
        if (helper.getmAuth().getCurrentUser() != null) {
            mcurrent_user_id = helper.getmAuth().getCurrentUser().getUid();
        }
    }

    public FirebaseAuthLayer getHelper() {
        return helper;
    }

    public StorageReference getmStorageRef() {
        return mStorageRef;
    }

    public String getMcurrent_user_id() {
        return mcurrent_user_id;
    }

    public DatabaseReference getmRootRef() {
        return mRootRef;
    }

    public DatabaseReference getmUserDatabase() {
        return mUserDatabase;
    }

    public Map registerUser(String display_name) {

        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("name", display_name);
        userMap.put("status", "Chatting with friends the right way");
        userMap.put("image", "default");
        userMap.put("online", "true");
        userMap.put("thumb_image", "default");

        return userMap;
    }

    public Map setupDatabaseMap(String doctor_id, State mapType) {
        Map setupMap = new HashMap();
        String currentDate = DateFormat.getDateTimeInstance().format(new Date());

        switch (mapType) {
            case not_consul:
                Log.d(TAG, "setupDatabaseMap:");
                //req friend
                DatabaseReference newNotifRef = mRootRef.child("Notifications").child(doctor_id).push();
                String newNotifId = newNotifRef.getKey();

                HashMap<String, String> notifs = new HashMap<>();
                notifs.put("from", mcurrent_user_id);
                notifs.put("type", "request");

                setupMap.put("Consultation_Req/" + mcurrent_user_id + "/" + doctor_id + "/request_type", "sent");
                setupMap.put("Consultation_Req/" + doctor_id + "/" + mcurrent_user_id + "/request_type", "received");
                setupMap.put("Notifications/" + doctor_id + "/" + newNotifId, notifs);
                break;
            case consul:
                //remove follow
                setupMap.put("Follows/" + mcurrent_user_id + "/" + doctor_id, null);
                break;
            case request_received:
                //accept req

                HashMap<String, String> userMap = new HashMap<>();
                userMap.put("name", "user's name");
                userMap.put("date", currentDate);
                userMap.put("user_type", "user");

                final HashMap<String, String> doctorMap = new HashMap<>();
                userMap.put("name", "user's name");
                doctorMap.put("date", currentDate);
                doctorMap.put("user_type", "doctor");

                setupMap.put("Consultations/" + mcurrent_user_id + "/" + doctor_id + "/", userMap);
                setupMap.put("Consultations/" + doctor_id + "/" + mcurrent_user_id + "/", doctorMap);
                setupMap.put("Follows/" + mcurrent_user_id + "/" + doctor_id + "/follow_type", "following");

                setupMap.put("Consultation_Req/" + mcurrent_user_id + "/" + doctor_id, null);
                setupMap.put("Consultation_Req/" + doctor_id + "/" + mcurrent_user_id, null);
                break;
            case request_sent:
                //decline friend request
                setupMap.put("Consultation_Req/" + mcurrent_user_id + "/" + doctor_id, null);
                setupMap.put("Consultation_Req/" + doctor_id + "/" + mcurrent_user_id, null);
                break;
        }
        return setupMap;
    }

}
