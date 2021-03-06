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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.dependencies.DependencyRegistry;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    //UI elements
    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateButton;
    private ProgressDialog mRegProgress;

    //Injection
    private FirebaseAuth mAuth;
    private FirebasePresenter presenter;
    private IntentPresenter intentPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupToolbar();
        attachUI();

        DependencyRegistry.shared.inject(this);
    }

    public void configureWith(FirebaseAuth mAuth, FirebasePresenter presenter, IntentPresenter intentPresenter) {
    this.mAuth = mAuth;
    this.presenter = presenter;
    this.intentPresenter = intentPresenter;
        setupUI();
    }

    private void setupUI() {
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

    private void attachUI() {
        Log.d(TAG, "attachUI: ");
        mDisplayName = findViewById(R.id.reg_display_name);
        mEmail = findViewById(R.id.reg_email);
        mPassword = findViewById(R.id.reg_password);
        mCreateButton = findViewById(R.id.reg_create_button);
        mRegProgress = new ProgressDialog(this);
    }

    private void setupToolbar() {
        Log.d(TAG, "setupToolbar: ");
        Toolbar mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create an account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.bringToFront();
    }

    private void register_user(final String display_name, String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    //Information to pass to Users table in database
                    updateDatabase(user, display_name);

                } else{
                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this, "An error occurred, please try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateDatabase(FirebaseUser user, String display_name) {
        Map userMap = presenter.setupUserMap(display_name);
        presenter.getmUserDatabase().child(user.getUid()).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mRegProgress.dismiss();
                    intentPresenter.sendToStart(RegisterActivity.this);
                }
            }
        });
    }

}
