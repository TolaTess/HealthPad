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
import com.tolaotesanya.healthpad.activities.business.AllDoctorsActivity;
import com.tolaotesanya.healthpad.activities.chat.ChatActivity;
import com.tolaotesanya.healthpad.activities.profile.DoctorProfileActivity;
import com.tolaotesanya.healthpad.modellayer.enums.ClassName;

public class IntentPresenter {
    private static final String TAG = "IntentPresenter";

    private Context mContext;
    private Activity mActivity;

    public IntentPresenter(Context mContext) {
        this.mContext = mContext;
        mActivity = (Activity) mContext;
    }

    public void presentIntent(ClassName activity, String userid, String extra) {
        switch (activity) {
            case Main:
                Intent homeIntent = new Intent(mContext, MainActivity.class);
                homeIntent.putExtra("user_id", userid);// send user id to use it to get all other info in db
                mContext.startActivity(homeIntent);
                break;
            case Settings:
                Intent settingIntent = new Intent(mContext, SettingsActivity.class);
                settingIntent.putExtra("user_id", userid);// send user id to use it to get all other info in db
                mContext.startActivity(settingIntent);
                break;
            case Account:
                Intent accountIntent = new Intent(mContext, AccountActivity.class);
                accountIntent.putExtra("user_id", userid);
                accountIntent.putExtra("lastSeen", extra);// send user id to use it to get all other info in db
                mContext.startActivity(accountIntent);
                break;
            case AllDoctors:
                Intent allUserIntent = new Intent(mContext, AllDoctorsActivity.class);
                allUserIntent.putExtra("user_id", userid);
                allUserIntent.putExtra("username", extra);// send user id to use it to get all other info in db
                mContext.startActivity(allUserIntent);
                break;
            case DoctorRegister:
                Intent doctorRegIntent = new Intent(mContext, DoctorRegisterActivity.class);
                doctorRegIntent.putExtra("user_id", userid);
                mContext.startActivity(doctorRegIntent);
                break;
            case Chats:
                Intent chatIntent = new Intent(mContext, ChatActivity.class);
                chatIntent.putExtra("doctor_id", userid);
                chatIntent.putExtra("username", extra);// send user id to use it to get all other info in db
                mContext.startActivity(chatIntent);
                break;
            case Auth:
                Intent authIntent = new Intent(mContext, AuthActivity.class);
                mContext.startActivity(authIntent);
                mActivity.finish();
                break;
            case Profile:
                Intent profileIntent = new Intent(mContext, DoctorProfileActivity.class);
                profileIntent.putExtra("doctor_id", userid);
                profileIntent.putExtra("username", extra);// send user id to use it to get all other info in db
                mContext.startActivity(profileIntent);
                break;
            case Register:
                Intent registerIntent = new Intent(mContext, RegisterActivity.class);
                mContext.startActivity(registerIntent);
                break;
            case Login:
                Intent loginIntent = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(loginIntent);
                break;
        }
    }

}
