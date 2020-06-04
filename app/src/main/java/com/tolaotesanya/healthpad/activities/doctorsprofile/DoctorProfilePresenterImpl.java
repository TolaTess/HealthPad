package com.tolaotesanya.healthpad.activities.doctorsprofile;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.enums.State;

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
    private String fullname;
    private String image;
    private String details;

    public DoctorProfilePresenterImpl(FirebasePresenter presenter, String doctor_id, String fullname, String image, String details) {
        this.presenter = presenter;
        this.doctor_id = doctor_id;
        this.fullname = fullname;
        this.image = image;
        this.details = details;
        mCurrentuser_id = this.presenter.getMcurrent_user_id();
        mDoctorDatabase = this.presenter.getmRootRef().child("Doctors").child(doctor_id);
        mDoctorDatabase.keepSynced(true);
        mReqConsulDatabase = this.presenter.getmRootRef().child("Consultation_Req");
        mReqConsulDatabase.keepSynced(true);
        mConsulationsDatabase = this.presenter.getmRootRef().child("Consultations");
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

    public String getDoctor_id() {
        return doctor_id;
    }

    public String getFullname() {
        return fullname;
    }

    public String getImage() {
        return image;
    }

    public String getDetails() {
        return details;
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
