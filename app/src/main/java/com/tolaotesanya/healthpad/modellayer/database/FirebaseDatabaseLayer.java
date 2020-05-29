package com.tolaotesanya.healthpad.modellayer.database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.fragment.HomeFragment;
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
    private IntentPresenter intentPresenter;

    public FirebaseDatabaseLayer(Context context) {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        intentPresenter = new IntentPresenter(context);

        helper = new FirebaseAuthLayer();
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

    public Map setupMessageChatDB(String doctor_id, String message, State mapType){
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

    public Map registerUser(String display_name) {

        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("name", display_name);
        userMap.put("status", "Chatting with friends the right way");
        userMap.put("image", "default");
        userMap.put("online", "true");
        userMap.put("thumb_image", "default");

        return userMap;
    }

    @Override
    public IntentPresenter getIntentPresenter() {
        return intentPresenter;
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

    public Map registerDoctor(String firstName, String lastName,
                              String speciality, String clinicName, String location){
        //Information to pass to Doctors table
        HashMap<String, String> doctorMap = new HashMap<>();
        doctorMap.put("first_name", firstName);
        doctorMap.put("last_name", lastName);
        doctorMap.put("speciality", speciality);
        doctorMap.put("clinic_name", clinicName);
        doctorMap.put("location", location);
        doctorMap.put("image", "default");
        doctorMap.put("thumb_image", "default");

        return doctorMap;
    }

    public Map setupUserMap(String displayName){
        //Information to pass to Users table in database
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("name", displayName);
        userMap.put("status", "Need your expert opinion");
        userMap.put("image", "default");
        userMap.put("thumb_image", "default");
        userMap.put("user_type", "user");

        return userMap;
    }

    public Map setupPostMap(String caption, String download_uri, String thumb_download_uri){
        DatabaseReference user_post_push = mRootRef.child("Posts")
                .push();

        String post_push_id = user_post_push.getKey();

        Map postMap = new HashMap();
        postMap.put("timestamp", ServerValue.TIMESTAMP);
        postMap.put("caption", caption);
        postMap.put("likes", "3");
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
