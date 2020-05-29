package com.tolaotesanya.healthpad.fragment.requests;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;

public class RequestPresenterImpl implements RequestPresenter {

    private DatabaseReference mConsultReqDatabase;
    private DatabaseReference mUserDatabase;


    public RequestPresenterImpl(Context context) {
        FirebasePresenter presenter = new FirebaseDatabaseLayer(context);
        mConsultReqDatabase = presenter.getmRootRef()
                .child("Consultation_Req")
                .child(presenter.getMcurrent_user_id());
        //mFriendsReqDatabase.keepSynced(true);
        mUserDatabase = presenter.getmRootRef()
                .child("Users");
        //mUserDatabase.keepSynced(true);

    }

    @Override
    public DatabaseReference getmUserReqDatabase() {
        return mUserDatabase;
    }

    @Override
    public DatabaseReference getmConsultReqDatabase() {
        return mConsultReqDatabase;
    }
}
