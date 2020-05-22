package com.tolaotesanya.healthpad.profile.auth.doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.profile.account.AccountActivity;
import com.tolaotesanya.healthpad.profile.account.SettingsActivity;

import java.util.HashMap;

public class DoctorsActivity extends AppCompatActivity {
    private static final String TAG = "DoctorsActivity";

    //UI elements
    private TextInputLayout mFirstName;
    private TextInputLayout mLastName;
    private TextInputLayout mSpeciality;
    private TextInputLayout mClinicName;
    private TextInputLayout mLocation;
    private Button mSaveButton;

    private ProgressDialog mRegProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);

        setupToolbar();
        attachUI();
    }

    private void attachUI() {
        mFirstName = findViewById(R.id.doctor_fName);
        mLastName = findViewById(R.id.doctor_lName);
        mSpeciality = findViewById(R.id.doctor_specality);
        mClinicName = findViewById(R.id.doctor_clinic_name);
        mLocation = findViewById(R.id.doctor_location);
        mSaveButton = findViewById(R.id.doctor_btn);

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
                    mRegProgress = new ProgressDialog(DoctorsActivity.this);
                    mRegProgress.setTitle("Registering Doctor");
                    mRegProgress.setMessage("Please wait while we create your Doctor account");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    registerDoctor(firstName, lastName, speciality, clinicName, location);
                }
            }
        });
    }

    private void setupToolbar() {
        Log.d(TAG, "setupToolbar: ");
        Toolbar mToolbar = findViewById(R.id.doctor_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Doctor Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.bringToFront();
    }

    private void registerDoctor(String firstName, String lastName,
                                String speciality, String clinicName, String location){
        FirebaseUser currentUser = FirebaseAuth
                .getInstance().getCurrentUser();
        DatabaseReference doctorDatabase = FirebaseDatabase
                .getInstance().getReference().child("Doctors")
                .child(currentUser.getUid());

        //Information to pass to Doctors table
        HashMap<String, String> doctorMap = new HashMap<>();
        doctorMap.put("first_name", firstName);
        doctorMap.put("last_name", lastName);
        doctorMap.put("speciality", speciality);
        doctorMap.put("clinic_name", clinicName);
        doctorMap.put("location", location);
        doctorMap.put("image", "default");
        doctorMap.put("thumb_image", "default");

        //send hashmap to database
        doctorDatabase.setValue(doctorMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mRegProgress.dismiss();
                    sendToStart();
                } else{
                    Toast.makeText(DoctorsActivity.this, "Doctor Setup failed",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void sendToStart() {
        Intent accountIntent = new Intent(this, AccountActivity.class);
        startActivity(accountIntent);
    }


}
