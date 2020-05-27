package com.tolaotesanya.healthpad.activities.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.helper.State;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorProfilePresenterImpl implements DoctorProfilePresenter{



    private DatabaseReference mDoctorDatabase;
    private DatabaseReference mReqConsulDatabase;
    private DatabaseReference mConsulationsDatabase;
    private DatabaseReference mFollowsDatabase;
    private String mCurrentuser_id;
    private FirebasePresenter presenter;
    private final String doctor_id;

    public DoctorProfilePresenterImpl(String doctor_id) {
        presenter = new FirebaseDatabaseLayer();
        this.doctor_id = doctor_id;
        mDoctorDatabase = presenter.getmRootRef().child("Doctors").child(doctor_id);
        mReqConsulDatabase = presenter.getmRootRef().child("Consultation_Req");
        mConsulationsDatabase = presenter.getmRootRef().child("Consultations");
        mFollowsDatabase = presenter.getmRootRef().child("Follows");
        mCurrentuser_id = presenter.getMcurrent_user_id();
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

    public DatabaseReference getmFollowsDatabase() {
        return mFollowsDatabase;
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
