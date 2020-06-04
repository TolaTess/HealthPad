package com.tolaotesanya.healthpad.activities.chat;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

public interface ChatActivityPresenter {

    void checkLastSeenOnline(TextView mName, final TextView mLastSeen, final CircleImageView mProfileImage);
    void sendMessages(TextView mChatMessageView);
    void loadMoreMessages(final RecyclerView mMessageRecyclerView, final LinearLayoutManager mLinearLayout, final SwipeRefreshLayout mRefreshLayout);
    void loadMessages(final RecyclerView mMessageRecyclerView, final SwipeRefreshLayout mRefreshLayout);
    void createChatDatabase();
    FirebasePresenter getPresenter();
    void setupUI(ImageView mChatSendBtn, final TextView mChatMessageView,
                 final RecyclerView mMessageRecyclerView, final LinearLayoutManager mLinearLayout, final SwipeRefreshLayout mRefreshLayout);

}
