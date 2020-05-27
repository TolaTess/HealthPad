package com.tolaotesanya.healthpad.activities.auth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tolaotesanya.healthpad.R;

public class AuthActivity extends AppCompatActivity {

    private Button createButton;
    private Button loginButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        createButton = findViewById(R.id.reg_btn);
        loginButton = findViewById(R.id.login_btn);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}
