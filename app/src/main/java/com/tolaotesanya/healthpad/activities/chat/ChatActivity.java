package com.tolaotesanya.healthpad.activities.chat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.tolaotesanya.healthpad.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private Context mContext = ChatActivity.this;
    private ChatActivity mMainView = ChatActivity.this;
    private ChatActivityPresenter chatPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setupToolbar();
        String mChatReceiverUser = getIntent().getStringExtra("doctor_id");
        String userName = getIntent().getStringExtra("username");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        this.getSupportActionBar().setCustomView(action_bar_view);

        chatPresenter = new ChatPresenterActivityImpl
                (mContext, mMainView, mChatReceiverUser, userName);

    }
    private void setupToolbar() {
        Toolbar mToolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
    }
}
