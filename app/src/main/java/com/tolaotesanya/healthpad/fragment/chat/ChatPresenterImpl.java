package com.tolaotesanya.healthpad.fragment.chat;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;

public class ChatPresenterImpl implements ChatPresenter{

    private DatabaseReference mConsultDatabase;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mMessageDatabase;
    private IntentPresenter intentPresenter;

    public ChatPresenterImpl(Context context) {
        FirebasePresenter presenter = new FirebaseDatabaseLayer(context);
        intentPresenter = new IntentPresenter(context);
        mConsultDatabase = presenter.getmRootRef().child("Consultations")
                .child(presenter.getMcurrent_user_id());
        mConsultDatabase.keepSynced(true);
        mMessageDatabase = presenter.getmRootRef()
                .child("Messages").child(presenter.getMcurrent_user_id());
        mMessageDatabase.keepSynced(true);
        mUserDatabase = presenter.getmRootRef()
                .child("Users");
    }


    @Override
    public DatabaseReference getmConsultDatabase() {
        return mConsultDatabase;
    }

    @Override
    public DatabaseReference getmUserChatDatabase() {
        return mUserDatabase;
    }

    @Override
    public DatabaseReference getmMessageDatabase() {
        return mMessageDatabase;
    }

    @Override
    public IntentPresenter getIntentPresenter() {
        return intentPresenter;
    }


}
