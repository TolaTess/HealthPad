package com.tolaotesanya.healthpad.fragment.requests;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RequestPresenterImpl implements RequestPresenter {

    private FirebaseAuth mAuth;
    private DatabaseReference mConsultReqDatabase;
    private DatabaseReference mUserDatabase;

    public RequestPresenterImpl(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mConsultReqDatabase = FirebaseDatabase.getInstance().getReference()
                .child("Consultation_Req")
                .child(mAuth.getCurrentUser().getUid());
        //mFriendsReqDatabase.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference()
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
