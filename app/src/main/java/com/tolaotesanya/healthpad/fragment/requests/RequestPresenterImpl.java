package com.tolaotesanya.healthpad.fragment.requests;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.helper.State;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RequestPresenterImpl implements RequestPresenter {

    private DatabaseReference mConsultReqDatabase;
    private FirebasePresenter presenter;
    private DatabaseReference mUserDatabase;
    private IntentPresenter intentPresenter;
    private String mdoctor_id;


    public RequestPresenterImpl(Context context) {
        presenter = new FirebaseDatabaseLayer(context);
        intentPresenter = presenter.getIntentPresenter();
        mdoctor_id = presenter.getMcurrent_user_id();
        mConsultReqDatabase = presenter.getmRootRef()
                .child("Consultation_Req")
                .child(presenter.getMcurrent_user_id());
        mConsultReqDatabase.keepSynced(true);
        mUserDatabase = presenter.getmRootRef()
                .child("Users");

    }

    @Override
    public DatabaseReference getmUserReqDatabase() {
        return mUserDatabase;
    }

    @Override
    public DatabaseReference getmConsultReqDatabase() {
        return mConsultReqDatabase;
    }

    public IntentPresenter getIntentPresenter() {
        return intentPresenter;
    }

    public String getMdoctor_id() {
        return mdoctor_id;
    }

    public void loadDatabase(final Context context, String user_id, State mapType){
        Map databaseMap = presenter.setupDatabaseMap(user_id, mapType);
        presenter.getmRootRef().updateChildren(databaseMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(context, "There was an error",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
