package com.tolaotesanya.healthpad.activities.auth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.dependencies.DependencyRegistry;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.enums.ClassName;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DoctorRegisterActivity extends AppCompatActivity {
    private static final String TAG = "DoctorsActivity";

    //UI elements
    private TextInputLayout mFirstName;
    private TextInputLayout mLastName;
    private TextInputLayout mSpeciality;
    private TextInputLayout mClinicName;
    private TextInputLayout mLocation;
    private Button mSaveButton;
    private ProgressDialog mRegProgress;

    //Injection
    private FirebasePresenter presenter;
    private IntentPresenter intentPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);

        setupToolbar();
        attachUI();

        DependencyRegistry.shared.inject(this);
    }

    public void configureWith(FirebasePresenter presenter, IntentPresenter intentPresenter) {
        this.presenter = presenter;
        this.intentPresenter = intentPresenter;

        setupUI();
    }

    private void setupUI() {
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = mFirstName.getEditText().getText().toString();
                String lastName = mLastName.getEditText().getText().toString();
                String speciality = mSpeciality.getEditText().getText().toString();
                String clinicName = mClinicName.getEditText().getText().toString();
                String location = mLocation.getEditText().getText().toString();

                if(!TextUtils.isEmpty(firstName) || !TextUtils.isEmpty(lastName)
                        || !TextUtils.isEmpty(speciality) || !TextUtils.isEmpty(location)){
                    mRegProgress = new ProgressDialog(DoctorRegisterActivity.this);
                    mRegProgress.setTitle("Registering Doctor");
                    mRegProgress.setMessage("Please wait while we create your Doctor account");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    registerDoctor(firstName, lastName, speciality, clinicName, location);
                }
            }
        });
    }

    private void attachUI() {
        mFirstName = findViewById(R.id.doctor_fName);
        mLastName = findViewById(R.id.doctor_lName);
        mSpeciality = findViewById(R.id.doctor_specality);
        mClinicName = findViewById(R.id.doctor_clinic_name);
        mLocation = findViewById(R.id.doctor_location);
        mSaveButton = findViewById(R.id.doctor_btn);

    }

    private void setupToolbar() {
        Log.d(TAG, "setupToolbar: ");
        Toolbar mToolbar = findViewById(R.id.doctor_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Doctor Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.bringToFront();
    }

    private void registerDoctor(String firstName, final String lastName,
                                String speciality, String clinicName, String location){
        DatabaseReference doctorDatabase = presenter.getmRootRef().child("Doctors")
                .child(presenter.getMcurrent_user_id());
        final DatabaseReference userDatabase = presenter.getmUserDatabase()
                .child(presenter.getMcurrent_user_id());

        Map doctorMap = presenter.registerDoctor(firstName, lastName, speciality, clinicName, location);

        //send hashmap to database
        doctorDatabase.setValue(doctorMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Map doctorUser = new HashMap();
                    String doctorName = "Dr " + lastName;
                    doctorUser.put("name", doctorName);
                    doctorUser.put("user_type", "doctor");
                    userDatabase.updateChildren(doctorUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mRegProgress.dismiss();
                                intentPresenter.presentIntent(DoctorRegisterActivity.this, ClassName.Account, presenter.getMcurrent_user_id(), null);
                            } else{
                                Toast.makeText(DoctorRegisterActivity.this, "Doctor Setup failed",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

}
