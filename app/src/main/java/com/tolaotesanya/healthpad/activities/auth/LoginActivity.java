package com.tolaotesanya.healthpad.activities.auth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.dependencies.DependencyRegistry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    //Injection
    private FirebaseAuth mAuth;
    private IntentPresenter intentPresenter;

    private TextInputLayout mEmail;
    private TextInputLayout mPassword;

    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mRegProgress = new ProgressDialog(this);

        setupToolbar();
        attachUI();

        DependencyRegistry.shared.inject(this);
    }
    public void configureWith(FirebaseAuth mAuth, IntentPresenter intentPresenter) {
        this.mAuth = mAuth;
        this.intentPresenter = intentPresenter;
    }

    private void attachUI() {
        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);
        //UI Element
        Button mCreateButton = findViewById(R.id.login_button);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mRegProgress.setTitle("Logging In");
                    mRegProgress.setMessage("Please wait while we check your credentials.");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    login_user(email, password);
                }
            }
        });
    }

    private void setupToolbar() {
       Toolbar mToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.bringToFront();
    }

    private void login_user(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mRegProgress.dismiss();
                            intentPresenter.sendToStart(LoginActivity.this);

                        } else {
                            // If sign in fails, display a message to the user.
                            mRegProgress.hide();
                            Toast.makeText(LoginActivity.this, "Authentication failed. Please try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
