package com.tolaotesanya.healthpad.activities.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.activities.MainActivity;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseAuthLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    //UI elements
    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateButton;
    private ProgressDialog mRegProgress;

    private Context mContext = RegisterActivity.this;

    //Firebase
    private FirebaseAuthLayer mAuth;
    private FirebasePresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegProgress = new ProgressDialog(this);
        mAuth = new FirebaseAuthLayer();
        presenter = new FirebaseDatabaseLayer(mContext);

        setupToolbar();
        attachUI();
    }

    private void attachUI() {
        Log.d(TAG, "attachUI: ");
        mDisplayName = findViewById(R.id.reg_display_name);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);
        mCreateButton = findViewById(R.id.reg_create_button);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                //Ensure fields are not empty
                if(!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create your account");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    register_user(display_name, email, password);
                }
            }
        });
    }

    private void setupToolbar() {
        Log.d(TAG, "setupToolbar: ");
        Toolbar mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.bringToFront();
    }

    private void register_user(final String display_name, String email, String password){
        mAuth.getmAuth().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    String currentUserid = mAuth.getMcurrent_user().getUid();
                    //Information to pass to Users table in database
                    Map userMap = presenter.setupUserMap(display_name);

                    //send hashmap to database
                    presenter.getmUserDatabase().child(currentUserid).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mRegProgress.dismiss();
                                sendToStart();
                            }
                        }
                    });

                } else{
                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this, "An error occurred, please try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void sendToStart() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
