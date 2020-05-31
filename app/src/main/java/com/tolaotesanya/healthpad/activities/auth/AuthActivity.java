package com.tolaotesanya.healthpad.activities.auth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.internal.operators.completable.CompletableNever;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseUser;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.enums.ClassName;

public class AuthActivity extends AppCompatActivity {

    private Button createButton;
    private Button loginButton;
    private FirebasePresenter presenter;
    private Context mContext = AuthActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        presenter = new FirebaseDatabaseLayer(mContext);
    }

    private void attachUI() {
        createButton = findViewById(R.id.reg_btn);
        loginButton = findViewById(R.id.login_btn);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getIntentPresenter().presentIntent(ClassName.Register, null, null);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getIntentPresenter().presentIntent(ClassName.Login, null, null);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = presenter.getHelper().getMcurrent_user();
        if(currentUser != null){
            presenter.getIntentPresenter().presentIntent(ClassName.Main, presenter.getMcurrent_user_id(), null);
        } else {
            attachUI();
        }
    }
}
