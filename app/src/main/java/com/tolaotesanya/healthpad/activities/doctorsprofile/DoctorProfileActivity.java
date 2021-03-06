package com.tolaotesanya.healthpad.activities.doctorsprofile;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.dependencies.DependencyInjection;
import com.tolaotesanya.healthpad.modellayer.enums.State;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorProfileActivity extends AppCompatActivity {

    private CircleImageView mProfileImage;
    private TextView mProfileName, mProfileDetails, mReviews;
    private Button mBtnReqConsultation;
    private ProgressDialog progressDialog;
    private Context mContext = DoctorProfileActivity.this;
    private DoctorProfilePresenter presenter;

    private State mCurrent_state;
    private String mCurrent_user_id;
    private String doctor_id;
    private String fullName;
    private String details;
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        setupToolbar();
        attachUI();

        Bundle bundle = getIntent().getExtras();
        DependencyInjection.shared.inject(this, bundle);
        mCurrent_state = State.not_consul;
    }

    public void configureWith(DoctorProfilePresenter allDoctorPresenter) {
        this.presenter = allDoctorPresenter;
        mCurrent_user_id = presenter.getmCurrentuser_id();
        doctor_id = presenter.getDoctor_id();
        fullName = presenter.getFullname();
        details = presenter.getDetails();
        image = presenter.getImage();
        reqConsultation();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Doctors");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void attachUI() {
        mProfileDetails = findViewById(R.id.post_profile_body);
        mProfileName = findViewById(R.id.profile_name);
        mProfileDetails = findViewById(R.id.post_profile_body);
        mReviews = findViewById(R.id.profile_reviews);
        mProfileImage = findViewById(R.id.profile_image);
        mBtnReqConsultation = findViewById(R.id.profile_send_reg_btn);

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
                mBtnReqConsultation.setEnabled(true);
                mCurrent_state = State.request_sent;
                mBtnReqConsultation.setText("Cancel Request");
                break;
            case consul:
                //remove Friend from Friend DB - UnFriend
                presenter.loadDatabase(mContext, State.consul);
                mBtnReqConsultation.setEnabled(true);
                mCurrent_state = State.not_consul;
                mBtnReqConsultation.setText("Send Request");
                break;
            case request_received:
                //Accept Friend, Delete Friend Req data and data to Friends DB
                presenter.loadDatabase(mContext, State.request_received);
                mBtnReqConsultation.setEnabled(true);
                mCurrent_state = State.consul;
                String unfollowString = "Unfollow" + " ";
                mBtnReqConsultation.setText(unfollowString);
                break;
            case request_sent:
                //Cancel Friend Request
                cancelFriendRequest();
        }
    }

    private void cancelFriendRequest() {
        presenter.loadDatabase(mContext, State.request_sent);
        mBtnReqConsultation.setEnabled(true);
        mCurrent_state = State.not_consul;
        mBtnReqConsultation.setText("Send Request");
    }

    public void reqConsultation() {

        mProfileName.setText(fullName);
        mProfileDetails.setText(details);
        Picasso.get().load(image).placeholder(R.drawable.health_pad_logo).into(mProfileImage);

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
                                    mBtnReqConsultation.setText("Unfollow Dr " + fullName);
                                    progressDialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                progressDialog.dismiss();
                            }
                        });

        mBtnReqConsultation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnReqConsultation.setEnabled(false);
                loadProfileDatabase();
            }
        });
    }

}
