package com.tolaotesanya.healthpad.business;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.utils.State;

public class DoctorProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileDetails, mReviews;
    private Button mBtnReqConsultation;
    private ProgressDialog progressDialog;

    private DatabaseReference mDoctorDatabase;
    private DatabaseReference mReqConsulDatabase;
    private DatabaseReference mConsulationsDatabase;
    private FirebaseUser mCurrentuser;

    private String mCurrent_state;
    private String mlastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mDoctorDatabase = FirebaseDatabase.getInstance().getReference().child("Doctors").child(user_id);
        mReqConsulDatabase = FirebaseDatabase.getInstance().getReference().child("Consultation_Req");
        mConsulationsDatabase = FirebaseDatabase.getInstance().getReference().child("Consultations");
        mCurrentuser = FirebaseAuth.getInstance().getCurrentUser();

        mProfileName = findViewById(R.id.profile_name);
        mProfileDetails = findViewById(R.id.profile_details);
        mReviews = findViewById(R.id.profile_reviews);
        mProfileImage = findViewById(R.id.profile_image);
        mBtnReqConsultation = findViewById(R.id.profile_send_reg_btn);

        mCurrent_state = "not_consul";


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading User Data");
        progressDialog.setMessage("Please wait while we load the user data. ");
        progressDialog.setCanceledOnTouchOutside(false);

        mDoctorDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String firstName = dataSnapshot.child("first_name").getValue().toString();
                mlastName = dataSnapshot.child("last_name").getValue().toString();
                String speciality = dataSnapshot.child("speciality").getValue().toString();
                String location = dataSnapshot.child("location").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                String fullName = "Dr " + firstName + " " + mlastName;
                String details = "I am a " + speciality +
                        "\n" + "based in " + location;

                mProfileName.setText(fullName);
                mProfileDetails.setText(details);

                Picasso.get().load(image).placeholder(R.drawable.ic_launcher_foreground).into(mProfileImage);

                //friends list/ request feature
                mReqConsulDatabase.child(mCurrentuser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(user_id)){
                                    String req_type = dataSnapshot.child(user_id).child("request_type")
                                            .getValue().toString();
                                    if(req_type.equals("received")){
                                        mCurrent_state = "req_received";
                                        mBtnReqConsultation.setText("Accept Consultation");
                                    } else if(req_type.equals("sent")){
                                        mCurrent_state = "req_sent";
                                        mBtnReqConsultation.setText("Cancel Consultation Request");
                                    }
                                    progressDialog.dismiss();
                                } else {
                                    mReqConsulDatabase.child(mCurrentuser.getUid())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.hasChild(user_id)){
                                                        mCurrent_state = "consul";
                                                        mBtnReqConsultation.setText("UnFollow " + mlastName);
                                                        progressDialog.dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    progressDialog.dismiss();
                                                }
                                            });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mBtnReqConsultation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnReqConsultation.setEnabled(false);

                //friends request section
                if (mCurrent_state.equals("not_consul")) {
                    //current user sends a request to the user of interest using his user id
                    mReqConsulDatabase.child(mCurrentuser.getUid()).child(user_id)
                            .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //once done, add the senders details to the receivers table also
                                mReqConsulDatabase.child(user_id).child(mCurrentuser.getUid())
                                        .child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mBtnReqConsultation.setEnabled(true); //grey out button
                                        mCurrent_state = "req_sent";
                                        mBtnReqConsultation.setText(R.string.cancel_consul); //change the text of button
                                        Toast.makeText(DoctorProfileActivity.this, "Request Sent",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                Toast.makeText(DoctorProfileActivity.this, "Failed Sending Request",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });

    }
}
