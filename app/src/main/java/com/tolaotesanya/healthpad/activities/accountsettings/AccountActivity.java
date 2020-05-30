package com.tolaotesanya.healthpad.activities.accountsettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "AccountActivity";

    //UI element
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;
    private Button mSettingsButton;

    private Context mContext = AccountActivity.this;

    //Firebase
    private FirebasePresenter presenter;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        presenter = new FirebaseDatabaseLayer(mContext);
        setupToolbar();
        attachUI();

        String poster_id = getIntent().getStringExtra("user_id");
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void attachUI() {
        mDisplayImage = findViewById(R.id.img_setting);
        mName = findViewById(R.id.text_display_name);
        mStatus = findViewById(R.id.text_status);
        mSettingsButton = findViewById(R.id.change_settings);

        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //persist data
                String statusValue = mStatus.getText().toString();
                String dNameValue = mName.getText().toString();
                Intent settingsIntent = new Intent(AccountActivity.this, SettingsActivity.class);
                settingsIntent.putExtra("status_value", statusValue);
                settingsIntent.putExtra("display_name_value", dNameValue);
                startActivity(settingsIntent);
            }
        });

    }

    private void setupToolbar() {
        Toolbar mToolbar = findViewById(R.id.account_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
