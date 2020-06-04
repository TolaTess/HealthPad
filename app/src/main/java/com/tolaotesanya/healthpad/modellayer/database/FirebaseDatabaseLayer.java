package com.tolaotesanya.healthpad.modellayer.database;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tolaotesanya.healthpad.modellayer.enums.State;

import java.util.HashMap;
import java.util.Map;

public class FirebaseDatabaseLayer implements FirebasePresenter {
    private static final String TAG = "FirebaseDatabaseLayer";
    private DatabaseReference mRootRef;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private String mcurrent_user_id;
    private StorageReference mStorageRef;
    private String imageLink;
    private String thumb_imageLink;

    public FirebaseDatabaseLayer(FirebaseAuth mAuth) {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        //mRootRef.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        //mUserDatabase.keepSynced(true);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mCurrentUser = mAuth.getCurrentUser();

        if (mCurrentUser != null) {
            mcurrent_user_id = mCurrentUser.getUid();
        }
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

    public FirebaseUser getmCurrentUser() {
        return mCurrentUser;
    }

    public Map setupMessageChatDB(String doctor_id, String message, State mapType) {
        Map setupMap = new HashMap();
        switch (mapType) {
            case messageDB:
                //create Messages
                String current_user_ref = "Messages/" + mcurrent_user_id + "/" + doctor_id;
                String chat_user_ref = "Messages/" + doctor_id + "/" + mcurrent_user_id;

                DatabaseReference user_message_push = mRootRef.child("Messages")
                        .child(mcurrent_user_id).child(doctor_id).push();

                String push_id = user_message_push.getKey();

                Map messageMap = new HashMap();
                messageMap.put("message", message);
                messageMap.put("seen", false);
                messageMap.put("type", "text");
                messageMap.put("time", ServerValue.TIMESTAMP);
                messageMap.put("from", mcurrent_user_id);

                setupMap.put(current_user_ref + "/" + push_id, messageMap);
                setupMap.put(chat_user_ref + "/" + push_id, messageMap);
                break;
            case chat:
                Map chatAddMap = new HashMap();
                chatAddMap.put("seen", false);
                chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                setupMap.put("Chat/" + mcurrent_user_id + "/" + doctor_id, chatAddMap);
                setupMap.put("Chat/" + doctor_id + "/" + mcurrent_user_id, chatAddMap);
                break;
        }
        return setupMap;
    }

    public Map setupDatabaseMap(String doctor_id, State mapType) {
        Map setupMap = new HashMap();

        DatabaseReference newNotifRef = mRootRef.child("Notifications").child(doctor_id).push();
        String newNotifId = newNotifRef.getKey();
        HashMap<String, String> notifs = new HashMap<>();

        notifs.put("from", mcurrent_user_id);
        notifs.put("type", "request");

        switch (mapType) {
            case not_consul:
                Log.d(TAG, "setupDatabaseMap:");
                //req friend
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
                Map userMap = new HashMap();
                userMap.put("timestamp", ServerValue.TIMESTAMP);
                userMap.put("seen", false);
                userMap.put("user_type", "user");

                final Map doctorMap = new HashMap();
                doctorMap.put("timestamp", ServerValue.TIMESTAMP);
                userMap.put("seen", false);
                doctorMap.put("user_type", "doctor");

                setupMap.put("Consultations/" + mcurrent_user_id + "/" + doctor_id + "/", userMap);
                setupMap.put("Consultations/" + doctor_id + "/" + mcurrent_user_id + "/", doctorMap);
                setupMap.put("Follows/" + mcurrent_user_id + "/" + doctor_id + "/follow_type", "following");

                setupMap.put("Consultation_Req/" + mcurrent_user_id + "/" + doctor_id, null);
                setupMap.put("Consultation_Req/" + doctor_id + "/" + mcurrent_user_id, null);
                break;
            case request_sent:
                //decline request
                setupMap.put("Consultation_Req/" + mcurrent_user_id + "/" + doctor_id, null);
                setupMap.put("Consultation_Req/" + doctor_id + "/" + mcurrent_user_id, null);
                setupMap.put("Notifications/" + doctor_id + "/" + newNotifId, notifs);
                break;
        }
        return setupMap;
    }

    public Map registerDoctor(String firstName, String lastName,
                              String speciality, String clinicName, String location) {

        //Information to pass to Doctors table
        Map doctorMap = new HashMap();
        doctorMap.put("first_name", firstName);
        doctorMap.put("last_name", lastName);
        doctorMap.put("speciality", speciality);
        doctorMap.put("clinic_name", clinicName);
        doctorMap.put("location", location);
        /*doctorMap.put("image", "default");
        doctorMap.put("thumb_image", "default");*/

        return doctorMap;
    }

    public Map setupUserMap(String displayName) {
        //Information to pass to Users table in database
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("name", displayName);
        userMap.put("status", "Need your expert opinion");
        userMap.put("image", "default");
        userMap.put("online", "true");
        userMap.put("thumb_image", "default");
        userMap.put("user_type", "user");

        return userMap;
    }

    public Map setupPostMap(String title, String body, String download_uri, String thumb_download_uri) {
        DatabaseReference user_post_push = mRootRef.child("Posts")
                .push();

        String post_push_id = user_post_push.getKey();

        Map postMap = new HashMap();
        postMap.put("timestamp", ServerValue.TIMESTAMP);
        postMap.put("title", title);
        postMap.put("body", body);
        postMap.put("likes", "1");
        postMap.put("post_type", "tips");
        postMap.put("user_id", mcurrent_user_id);
        postMap.put("post_image", download_uri);
        postMap.put("thumb_image", thumb_download_uri);

        //use updateChildren instead of setValue
        Map setupMap = new HashMap();
        setupMap.put("Posts/" + post_push_id, postMap);
        return setupMap;
    }

}
