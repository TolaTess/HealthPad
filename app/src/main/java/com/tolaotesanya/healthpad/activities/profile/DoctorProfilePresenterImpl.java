package com.tolaotesanya.healthpad.activities.profile;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.tolaotesanya.healthpad.helper.State;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DoctorProfilePresenterImpl implements DoctorProfilePresenter{

    private DatabaseReference mDoctorDatabase;
    private DatabaseReference mReqConsulDatabase;
    private DatabaseReference mConsulationsDatabase;
    private String mCurrentuser_id;
    private FirebasePresenter presenter;
    private final String doctor_id;

    public DoctorProfilePresenterImpl(Context context, String doctor_id) {
        presenter = new FirebaseDatabaseLayer(context);
        this.doctor_id = doctor_id;
        mCurrentuser_id = presenter.getMcurrent_user_id();
        mDoctorDatabase = presenter.getmRootRef().child("Doctors").child(doctor_id);
        mDoctorDatabase.keepSynced(true);
        mReqConsulDatabase = presenter.getmRootRef().child("Consultation_Req");
        mReqConsulDatabase.keepSynced(true);
        mConsulationsDatabase = presenter.getmRootRef().child("Consultations");
        mConsulationsDatabase.keepSynced(true);
         }

    public DatabaseReference getmDoctorDatabase() {
        return mDoctorDatabase;
    }

    public DatabaseReference getmReqConsulDatabase() {
        return mReqConsulDatabase;
    }

    public DatabaseReference getmConsulationsDatabase() {
        return mConsulationsDatabase;
    }

    public String getmCurrentuser_id() {
        return mCurrentuser_id;
    }

    public void loadDatabase(final Context context, State mapType){
        Map databaseMap = presenter.setupDatabaseMap(doctor_id, mapType);
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
