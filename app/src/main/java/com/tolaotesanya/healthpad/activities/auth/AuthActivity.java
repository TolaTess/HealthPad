package com.tolaotesanya.healthpad.activities.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.dependencies.DependencyRegistry;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.enums.ClassName;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {

    private Button createButton;
    private Button loginButton;

    private FirebasePresenter presenter;
    private IntentPresenter intentPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        attachUI();
        DependencyRegistry.shared.inject(this);
    }

    public void configureWith(FirebasePresenter presenter, IntentPresenter intentPresenter) {
        this.presenter = presenter;
        this.intentPresenter = intentPresenter;
    }

    private void setupUI() {
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentPresenter.presentIntent(AuthActivity.this, ClassName.Register, null, null);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentPresenter.presentIntent(AuthActivity.this, ClassName.Login, null, null);
            }
        });
    }

    private void attachUI() {
        createButton = findViewById(R.id.reg_btn);
        loginButton = findViewById(R.id.login_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(presenter.getmCurrentUser() != null){
            intentPresenter.presentIntent(AuthActivity.this, ClassName.Main, presenter.getMcurrent_user_id(), null);
        } else {
          setupUI();
        }
    }


}
