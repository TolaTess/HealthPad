package com.tolaotesanya.healthpad.activities.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.helper.MessageAdapter;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private Context mContext = ChatActivity.this;
    private ChatActivity mMainView = ChatActivity.this;
    private ChatPresenter chatPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        String mChatReceiverUser = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("username");

        chatPresenter = new ChatPresenterImpl(mContext, mMainView, mChatReceiverUser);

        setupToolbar();
        attachUI(userName);


    }

    private void setupToolbar() {
        Toolbar mToolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(mToolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
    }

    private void attachUI(String userName) {
        TextView mNameView = findViewById(R.id.custom_bar_name);
        ImageView mChatSendBtn = findViewById(R.id.chat_msg_send);

        chatPresenter.loadMessages();

        mNameView.setText(userName);

        chatPresenter.checkLastSeenOnline();

        chatPresenter.createChatDatabase();

        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatPresenter.sendMessages();
            }
        });

    }
}
