package com.tolaotesanya.healthpad.business;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tolaotesanya.healthpad.R;

public class DoctorProfileActivity extends AppCompatActivity {

      private DatabaseReference mDoctorDatabase;
    private DatabaseReference mReqConsulDatabase;
    private DatabaseReference mConsulationsDatabase;
    private FirebaseUser mCurrentuser;

    private DoctorProfileActivity mView = DoctorProfileActivity.this;
    private DoctorProfilePresenter doctorPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mDoctorDatabase = FirebaseDatabase.getInstance().getReference().child("Doctors").child(user_id);
        mReqConsulDatabase = FirebaseDatabase.getInstance().getReference().child("Consultation_Req");
        mConsulationsDatabase = FirebaseDatabase.getInstance().getReference().child("Consultations");
        mCurrentuser = FirebaseAuth.getInstance().getCurrentUser();

        doctorPresenter = new DoctorProfilePresenter(mDoctorDatabase, mReqConsulDatabase, mConsulationsDatabase, mCurrentuser, user_id, mView);
        doctorPresenter.reqConsultation();

    }
}
