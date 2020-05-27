package com.tolaotesanya.healthpad.activities.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.activities.accountsettings.AccountActivity;
import com.tolaotesanya.healthpad.helper.State;

public class DoctorProfileActivity extends AppCompatActivity {

    private CircleImageView mProfileImage;
    private TextView mProfileName, mProfileDetails, mReviews;
    private Button mBtnReqConsultation, mDeclineButton;
    private ProgressDialog progressDialog;
    private Context mContext = DoctorProfileActivity.this;

    //private DoctorProfileActivity mView = DoctorProfileActivity.this;
    private DoctorProfilePresenter presenter;

    private State mCurrent_state;
    private String mCurrent_user_id;
    private String doctor_id;
    private String mlastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        doctor_id = getIntent().getStringExtra("doctor_id");

        attachUI();
        presenter = new DoctorProfilePresenterImpl(doctor_id);
        mCurrent_user_id = presenter.getmCurrentuser_id();

        mCurrent_state = State.not_consul;
        reqConsultation();

    }

    private void attachUI() {
        mProfileDetails = findViewById(R.id.profile_details);
        mProfileName = findViewById(R.id.profile_name);
        mProfileDetails = findViewById(R.id.profile_details);
        mReviews = findViewById(R.id.profile_reviews);
        mProfileImage = findViewById(R.id.profile_image);
        mBtnReqConsultation = findViewById(R.id.profile_send_reg_btn);
        mDeclineButton = findViewById(R.id.decline_button);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Doctor's Profile");
        progressDialog.setMessage("Please wait while we load the doctor's data. ");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void loadProfileDatabase() {
        switch (mCurrent_state) {
            case not_consul:
                //Request Friends
                presenter.loadDatabase(mContext, State.not_consul);
                btnSettings();
                mCurrent_state = State.request_sent;
                mBtnReqConsultation.setText("Cancel Request");
                break;
            case consul:
                //remove Friend from Friend DB - UnFriend
                presenter.loadDatabase(mContext, State.consul);
                btnSettings();
                mCurrent_state = State.not_consul;
                mBtnReqConsultation.setText("Send Request");
                break;
            case request_received:
                //Accept Friend, Delete Friend Req data and data to Friends DB
                presenter.loadDatabase(mContext, State.request_received);
                btnSettings();
                mCurrent_state = State.consul;
                String unfollowString = "Unfollow" + " ";
                mBtnReqConsultation.setText(unfollowString);
                break;
            case request_sent:
                //Cancel Friend Request
                cancelFriendRequest();
        }
    }

    private void btnSettings() {
        mBtnReqConsultation.setEnabled(true);
        mDeclineButton.setVisibility(View.INVISIBLE);
        mDeclineButton.setEnabled(false);
    }

    private void cancelFriendRequest() {
        presenter.loadDatabase(mContext, State.request_sent);
        btnSettings();
        mCurrent_state = State.not_consul;
        mBtnReqConsultation.setText("Send Request");
    }

    public void reqConsultation() {
        presenter.getmDoctorDatabase().addValueEventListener(new ValueEventListener() {
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
                presenter.getmReqConsulDatabase().child(mCurrent_user_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(doctor_id)) {
                                    String req_type = dataSnapshot.child(doctor_id).child("request_type")
                                            .getValue().toString();
                                    if (req_type.equals("received")) {
                                        mCurrent_state = State.request_received;
                                        mBtnReqConsultation.setText(R.string.accept_consul);
                                    } else if (req_type.equals("sent")) {
                                        mCurrent_state = State.request_sent;
                                        mBtnReqConsultation.setText(R.string.cancel_consul);
                                    }
                                    progressDialog.dismiss();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                presenter.getmConsulationsDatabase().child(mCurrent_user_id)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(doctor_id)) {
                                    mCurrent_state = State.consul;
                                    mBtnReqConsultation.setText("Unfollow Dr " + mlastName);
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

                //request section
                if (mCurrent_user_id.equals(doctor_id)) {
                    mBtnReqConsultation.setText("Accounts"); // bug
                    Intent intent = new Intent(DoctorProfileActivity.this, AccountActivity.class);
                    intent.putExtra("user_id", doctor_id);
                    intent.putExtra("username", mlastName);
                    startActivity(intent);
                } else {
                    loadProfileDatabase();

                }

              /*  if (mCurrent_state.equals("not_consul")) {
                    //current user sends a request to the user of interest using his user id
                    presenter.getmReqConsulDatabase().child(mCurrent_user_id).child(doctor_id)
                            .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Add senders details under receiver
                                presenter.getmReqConsulDatabase().child(doctor_id).child(mCurrent_user_id)
                                        .child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mBtnReqConsultation.setEnabled(true); //grey out button
                                        mCurrent_state = State.request_sent;
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
        });*/


            }
        });
    }
}
