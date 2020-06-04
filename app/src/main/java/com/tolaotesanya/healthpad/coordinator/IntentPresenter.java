package com.tolaotesanya.healthpad.coordinator;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.tolaotesanya.healthpad.activities.MainActivity;
import com.tolaotesanya.healthpad.activities.accountsettings.AccountActivity;
import com.tolaotesanya.healthpad.activities.accountsettings.SettingsActivity;
import com.tolaotesanya.healthpad.activities.auth.AuthActivity;
import com.tolaotesanya.healthpad.activities.auth.DoctorRegisterActivity;
import com.tolaotesanya.healthpad.activities.auth.LoginActivity;
import com.tolaotesanya.healthpad.activities.auth.RegisterActivity;
import com.tolaotesanya.healthpad.activities.doctors.AllDoctorsActivity;
import com.tolaotesanya.healthpad.activities.chat.ChatActivity;
import com.tolaotesanya.healthpad.activities.doctorsprofile.DoctorProfileActivity;
import com.tolaotesanya.healthpad.activities.posts.PostsActivity;
import com.tolaotesanya.healthpad.activities.posts.PostsGalleryActivity;
import com.tolaotesanya.healthpad.modellayer.enums.ClassName;

public class IntentPresenter {
    private static final String TAG = "IntentPresenter";

  /*  private Context mContext;
    private Activity mActivity;*/

    /*public IntentPresenter(Context mContext) {
        this.mContext = mContext;
        mActivity = (Activity) mContext;
    }*/

    public void postProfileIntent(Context context, String poster_id, String username, String timestamp, String poster_image, String caption, String body){
        Intent postIntent = new Intent(context, PostsGalleryActivity.class);
        postIntent.putExtra("poster_id", poster_id);
        postIntent.putExtra("username", username);
        postIntent.putExtra("timestamp", timestamp);
        postIntent.putExtra("poster_image", poster_image);
        postIntent.putExtra("title", caption);
        postIntent.putExtra("body", body);
        context.startActivity(postIntent);
    }

    public void doctorProfileIntent(Context context, String doctor_id, String fullname, String details, String image){
        Intent doctorIntent = new Intent(context, DoctorProfileActivity.class);
        doctorIntent.putExtra("doctor_id", doctor_id);
        doctorIntent.putExtra("fullname", fullname);
        doctorIntent.putExtra("details", details);
        doctorIntent.putExtra("image", image);
        context.startActivity(doctorIntent);
    }

    public void sendtoAuth(Context context){
        Activity activity = (Activity) context;
        Intent authIntent = new Intent(context, AuthActivity.class);
        context.startActivity(authIntent);
        activity.finish();
    }

    public void presentIntent(Context context, ClassName activity, String userid, String extra) {
        switch (activity) {
            case Main:
                Intent homeIntent = new Intent(context, MainActivity.class);
                homeIntent.putExtra("user_id", userid);
                context.startActivity(homeIntent);
                break;
            case Settings:
                Intent settingIntent = new Intent(context, SettingsActivity.class);
                settingIntent.putExtra("user_id", userid);
                context.startActivity(settingIntent);
                break;
            case Account:
                Intent accountIntent = new Intent(context, AccountActivity.class);
                accountIntent.putExtra("user_id", userid);
                accountIntent.putExtra("lastSeen", extra);
                context.startActivity(accountIntent);
                break;
            case AllDoctors:
                Intent allUserIntent = new Intent(context, AllDoctorsActivity.class);
                allUserIntent.putExtra("user_id", userid);
                allUserIntent.putExtra("username", extra);
                context.startActivity(allUserIntent);
                break;
            case DoctorRegister:
                Intent doctorRegIntent = new Intent(context, DoctorRegisterActivity.class);
                doctorRegIntent.putExtra("user_id", userid);
                context.startActivity(doctorRegIntent);
                break;
            case Chats:
                Intent chatIntent = new Intent(context, ChatActivity.class);
                chatIntent.putExtra("doctor_id", userid);
                chatIntent.putExtra("username", extra);
                context.startActivity(chatIntent);
                break;
            case Profile:
                Intent profileIntent = new Intent(context, DoctorProfileActivity.class);
                profileIntent.putExtra("doctor_id", userid);
                profileIntent.putExtra("username", extra);
                context.startActivity(profileIntent);
                break;
            case Register:
                Intent registerIntent = new Intent(context, RegisterActivity.class);
                context.startActivity(registerIntent);
                break;
            case Login:
                Intent loginIntent = new Intent(context, LoginActivity.class);
                context.startActivity(loginIntent);
                break;
            case Posts:
                Intent postIntent = new Intent(context, PostsActivity.class);
                context.startActivity(postIntent);
                break;
        }
    }

}
