package com.tolaotesanya.healthpad.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.tolaotesanya.healthpad.MainActivity;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.profile.auth.AuthActivity;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN = 5000;

    //Variables
    private Animation topAnimation;
    private Animation bottomAnimation;

    //View Element
    private ImageView splashImage;
    private TextView title;
    private TextView slogan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        splashImage = findViewById(R.id.splash_image);
        title = findViewById(R.id.splash_title);
        slogan = findViewById(R.id.splash_value);

        splashImage.setAnimation(topAnimation);
        title.setAnimation(bottomAnimation);
        slogan.setAnimation(bottomAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);

    }
}
