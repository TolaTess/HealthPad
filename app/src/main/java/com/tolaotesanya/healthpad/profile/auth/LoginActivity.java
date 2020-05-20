package com.tolaotesanya.healthpad.profile.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
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
import com.tolaotesanya.healthpad.MainActivity;
import com.tolaotesanya.healthpad.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    //Firebase
    private FirebaseAuth mAuth;

    //UI Element
    private Button mCreateButton;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;

    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mRegProgress = new ProgressDialog(this);

        setupToolbar();
        attachUI();
    }

    private void attachUI() {
        mEmail = findViewById(R.id.login_email);
        mPassword = findViewById(R.id.login_password);
        mCreateButton = findViewById(R.id.login_button);

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
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            mRegProgress.dismiss();
                            sendToStart();

                        } else {
                            // If sign in fails, display a message to the user.
                            mRegProgress.hide();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void sendToStart() {
        Intent intent = new Intent(this, MainActivity.class);
        //Ensure user can not go back to login screen once logged in
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
