package com.tolaotesanya.healthpad.business;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.NonNull;
import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorProfilePresenter {

    private CircleImageView mProfileImage;
    private TextView mProfileName, mProfileDetails, mReviews;
    private Button mBtnReqConsultation;
    private ProgressDialog progressDialog;

    private DatabaseReference mDoctorDatabase;
    private DatabaseReference mReqConsulDatabase;
    private DatabaseReference mConsulationsDatabase;
    private DatabaseReference mFollowsDatabase;
    private FirebaseUser mCurrentuser;

    private String mCurrent_state;
    private String mlastName;
    private DoctorProfileActivity view;

    private final String doctor_id;

    public DoctorProfilePresenter(DatabaseReference mDoctorDatabase, DatabaseReference mReqConsulDatabase, DatabaseReference mConsulationsDatabase, DatabaseReference mFollowsDatabase, FirebaseUser mCurrentuser, String doctor_id, DoctorProfileActivity view) {
        this.mDoctorDatabase = mDoctorDatabase;
        this.mReqConsulDatabase = mReqConsulDatabase;
        this.mConsulationsDatabase = mConsulationsDatabase;
        this.mFollowsDatabase = mFollowsDatabase;
        this.mCurrentuser = mCurrentuser;
        this.doctor_id = doctor_id;
        this.view = view;

        attachUI();

        progressDialog = new ProgressDialog(view);
        progressDialog.setTitle("Loading Doctor's Profile");
        progressDialog.setMessage("Please wait while we load the doctor's data. ");
        progressDialog.setCanceledOnTouchOutside(false);

        mCurrent_state = "not_consul";
    }

    private void attachUI() {
        mProfileDetails = view.findViewById(R.id.profile_details);
        mProfileName = view.findViewById(R.id.profile_name);
        mProfileDetails = view.findViewById(R.id.profile_details);
        mReviews = view.findViewById(R.id.profile_reviews);
        mProfileImage = view.findViewById(R.id.profile_image);
        mBtnReqConsultation = view.findViewById(R.id.profile_send_reg_btn);
    }

    public void reqConsultation() {
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

                //Request Consultation
                mReqConsulDatabase.child(mCurrentuser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(doctor_id)) {
                                    String req_type = dataSnapshot.child(doctor_id).child("request_type")
                                            .getValue().toString();
                                    if (req_type.equals("received")) {
                                        mCurrent_state = "req_received";
                                        mBtnReqConsultation.setText("Accept Consultation");
                                    } else if (req_type.equals("sent")) {
                                        mCurrent_state = "req_sent";
                                        mBtnReqConsultation.setText("Cancel Consultation Request");
                                    }
                                    progressDialog.dismiss();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    mFollowsDatabase.child(mCurrentuser.getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(doctor_id)) {
                                        mCurrent_state = "follow";
                                        mBtnReqConsultation.setText("UnFollow Dr " + mlastName);
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    progressDialog.dismiss();
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
                    mReqConsulDatabase.child(mCurrentuser.getUid()).child(doctor_id)
                            .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Add senders details under receiver
                                mReqConsulDatabase.child(doctor_id).child(mCurrentuser.getUid())
                                        .child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mBtnReqConsultation.setEnabled(true); //grey out button
                                        mCurrent_state = "req_sent";
                                        mBtnReqConsultation.setText(R.string.cancel_consul); //change the text of button
                                        Toast.makeText(view, "Request Sent",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                Toast.makeText(view, "Failed Sending Request",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                cancelConsultation();
                acceptConsulation();
                unFollowDoctors();

            }
        });


    }

    private void acceptConsulation() {
        //Accept Request
        if (mCurrent_state.equals("req_received")) {

            String currentDate = DateFormat.getDateTimeInstance().format(new Date());
            HashMap<String, String> userMap = new HashMap<>();
            userMap.put("name", currentDate);
            userMap.put("user_type", "User");

            final HashMap<String, String> doctorMap = new HashMap<>();
            doctorMap.put("name", currentDate);
            doctorMap.put("user_type", "Doctor");

            mConsulationsDatabase.child(mCurrentuser.getUid()).child(doctor_id).setValue(userMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mConsulationsDatabase.child(doctor_id).child(mCurrentuser.getUid())
                                        .setValue(doctorMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mFollowsDatabase.child(mCurrentuser.getUid())
                                                    .child(doctor_id).child("follow_type").setValue("following").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        mReqConsulDatabase.child(mCurrentuser.getUid()).child(doctor_id)
                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    mReqConsulDatabase.child(doctor_id).child(mCurrentuser.getUid())
                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()) {
                                                                                mBtnReqConsultation.setEnabled(true); //grey out button
                                                                                mCurrent_state = "follow";
                                                                                mBtnReqConsultation.setText("Unfollow Dr " + mlastName);
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(view, "Consultation Could not be Accepted",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void cancelConsultation() {
        // Cancel Requests
        if (mCurrent_state.equals("req_sent")) {
            mReqConsulDatabase.child(mCurrentuser.getUid()).child(doctor_id)
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        mReqConsulDatabase.child(doctor_id).child(mCurrentuser.getUid())
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    mBtnReqConsultation.setEnabled(true);
                                    mCurrent_state = "not_consul";
                                    mBtnReqConsultation.setText(R.string.req_consul);
                                }
                            }
                        });
                    } else {
                        Toast.makeText(view, "Cancellation Failed",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void unFollowDoctors(){
        // Unfollow  section
        if (mCurrent_state.equals("follow")) {
            mFollowsDatabase.child(mCurrentuser.getUid()).child(doctor_id)
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        mFollowsDatabase.child(doctor_id).child(mCurrentuser.getUid())
                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mBtnReqConsultation.setEnabled(true); //grey out button
                                mCurrent_state = "not_consul";
                                mBtnReqConsultation.setText(R.string.req_consul);
                            }
                        });
                    } else {
                        Toast.makeText(view, "Unfollow Failed",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
