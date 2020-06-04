package com.tolaotesanya.healthpad.activities.accountsettings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.dependencies.DependencyRegistry;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";

    //UI element
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;
    private Button mSettingsButton;

    //Injection
    private FirebasePresenter presenter;
    private IntentPresenter intentPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        setupToolbar();
        attachUI();
        //Bundle bundle = getIntent().getExtras();
        DependencyRegistry.shared.inject(this);

    }

    public void configureWith(FirebasePresenter presenter, IntentPresenter intentPresenter) {
    this.presenter = presenter;
    this.intentPresenter = intentPresenter;

        setupUI();
    }

    private void setupUI() {
        String poster_id = getIntent().getStringExtra("user_id");
        DatabaseReference mUserDatabase;
        if(poster_id != null){
            mUserDatabase = presenter.getmUserDatabase().child(poster_id);
            mSettingsButton.setEnabled(false);
            mSettingsButton.setVisibility(View.INVISIBLE);
        } else {
            mUserDatabase = presenter.getmUserDatabase().child(presenter.getMcurrent_user_id());
            mSettingsButton.setEnabled(true);
            mSettingsButton.setVisibility(View.VISIBLE);
        }

        //Value Event Listener to get the data from database
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("name")) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                    //Ensures default image stays on screen
                    if (!image.equals("default")) {
                        //Ensure placeholder stays on screen until image downloads from database
                        Picasso.get().load(image).placeholder(R.drawable.health_pad_logo).into(mDisplayImage);
                    }
                    mName.setText(name);
                    mStatus.setText(status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //persist data
                String statusValue = mStatus.getText().toString();
                String dNameValue = mName.getText().toString();
                intentPresenter.sendtoSetting(AccountActivity.this, statusValue, dNameValue);
            }
        });
    }

    private void attachUI() {
        mDisplayImage = findViewById(R.id.img_setting);
        mName = findViewById(R.id.text_display_name);
        mStatus = findViewById(R.id.text_status);
        mSettingsButton = findViewById(R.id.change_settings);
    }

    private void setupToolbar() {
        Toolbar mToolbar = findViewById(R.id.account_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
