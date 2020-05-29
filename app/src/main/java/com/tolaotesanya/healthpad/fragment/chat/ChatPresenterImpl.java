package com.tolaotesanya.healthpad.fragment.chat;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;

public class ChatPresenterImpl implements ChatPresenter{

    private DatabaseReference mConvDatabase;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mMessageDatabase;

    public ChatPresenterImpl(Context context) {
        FirebasePresenter presenter = new FirebaseDatabaseLayer();
        mConvDatabase = presenter.getmRootRef().child("Chat")
                .child(presenter.getMcurrent_user_id());
        mConvDatabase.keepSynced(true);
        mMessageDatabase = presenter.getmRootRef()
                .child("Messages").child(presenter.getMcurrent_user_id());
        mMessageDatabase.keepSynced(true);
        mUserDatabase = presenter.getmRootRef()
                .child("Users");
        mUserDatabase.keepSynced(true);
    }


    @Override
    public DatabaseReference getmConvDatabase() {
        return mConvDatabase;
    }

    @Override
    public DatabaseReference getmUserChatDatabase() {
        return mUserDatabase;
    }

    @Override
    public DatabaseReference getmMessageDatabase() {
        return mMessageDatabase;
    }
}
