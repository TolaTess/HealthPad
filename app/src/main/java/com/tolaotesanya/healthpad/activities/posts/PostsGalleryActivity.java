package com.tolaotesanya.healthpad.activities.posts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;

public class PostsGalleryActivity extends AppCompatActivity {

    private String poster_id;
    private String username;
    private String timestamp;
    private String caption;
    private String post_image;
    private String body;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_gallery);

        poster_id = getIntent().getStringExtra("poster_id");
        username = getIntent().getStringExtra("username");
        timestamp = getIntent().getStringExtra("timestamp");
        post_image = getIntent().getStringExtra("poster_image");
        caption = getIntent().getStringExtra("title");
        body = getIntent().getStringExtra("body");

        setupToolbar();
        attachUI();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.post_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Posts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void attachUI() {
        ImageView mPostImage = findViewById(R.id.post_profile_image);
        TextView mPostTitle = findViewById(R.id.post_profile_title);
        TextView mPostBody = findViewById(R.id.post_profile_body);
        TextView mPostAuthor = findViewById(R.id.post_profile_author);
        TextView mPostTime = findViewById(R.id.post_profile_time);

        if(post_image.equals("default")){
            mPostImage.setVisibility(View.GONE);
        } else{
            Picasso.get().load(post_image).placeholder(R.drawable.ic_launcher_foreground).into(mPostImage);
        }
        mPostTitle.setText(caption);
        String newUsername = "by " + username;
        mPostAuthor.setText(newUsername);
        String newTime = "Posted " + timestamp;
        mPostTime.setText(newTime);
        mPostBody.setText(body);
    }
}
