package com.tolaotesanya.healthpad.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.tolaotesanya.healthpad.R;

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
                Intent splashIntent = new Intent(SplashActivity.this, MainActivity.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(splashImage, "transition_image");
                pairs[1] = new Pair<View, String>(title, "transition_name");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pairs);
                    startActivity(splashIntent, options.toBundle());
                }
            }
        },SPLASH_SCREEN);

    }
}
