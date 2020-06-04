package com.tolaotesanya.healthpad.activities.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.helper.GetTimeAgo;
import com.tolaotesanya.healthpad.helper.MessageAdapter;
import com.tolaotesanya.healthpad.modellayer.enums.State;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.model.ReceivedMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatPresenterActivityImpl implements ChatActivityPresenter {
    private static final String TAG = "ChatPresentActImpl";
    private static final int GALLERY_PICK = 1;

    private FirebasePresenter presenter;
    private DatabaseReference mMessageRef;
    private MessageAdapter mAdapter;
    private static final int TOTAL_ITEM_TO_LOAD = 5;
    private int mCurrentPage = 1;
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPreKey = "";
    private Context mContext;
    private String mChatReceiverUser;
    private String mUsername;
    private final List<ReceivedMessage> messagesList = new ArrayList<>();
    //private ChatActivity mView;


    public ChatPresenterActivityImpl(Context context, FirebasePresenter presenter, String user_id, String userName) {
        this.mContext = context;
        this.presenter = presenter;
        this.mChatReceiverUser = user_id;
        this.mUsername = userName;

        mAdapter = new MessageAdapter(presenter, messagesList);

        mMessageRef = presenter.getmRootRef().child("Messages")
                .child(presenter.getMcurrent_user_id()).child(user_id);
        mMessageRef.keepSynced(true);
    }

    public FirebasePresenter getPresenter() {
        return presenter;
    }

    @Override
    public void createChatDatabase() {
        presenter.getmRootRef().child("Chat").child(presenter.getMcurrent_user_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild(mChatReceiverUser)) {
                            Map chatUserMap = presenter.setupMessageChatDB(mChatReceiverUser, null, State.chat);

                            presenter.getmRootRef().updateChildren(chatUserMap,
                                    new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError,
                                                               @NonNull DatabaseReference databaseReference) {
                                            if (databaseError != null) {
                                                Log.d("CHAT_LOG", databaseError.getMessage());
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void checkLastSeenOnline(TextView mName, final TextView mLastSeen, final CircleImageView mProfileImage) {
        mName.setText(mUsername);
        presenter.getmUserDatabase()
                .child(mChatReceiverUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                Log.d(TAG, "onDataChange: datasnapshot " + dataSnapshot.getKey());
                Log.d(TAG, "onDataChange: " + " image link " + image);

                Picasso.get().load(image).placeholder(R.drawable.health_pad_logo).into(mProfileImage);

                if (online.equals("true")) {

                    mLastSeen.setText("online");

                } else {

                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long lastTime = Long.parseLong(online);

                    String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, mContext);
                    mLastSeen.setText(lastSeenTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void loadMessages(final RecyclerView mMessageRecyclerView, final SwipeRefreshLayout mRefreshLayout) {
        mMessageRecyclerView.setAdapter(mAdapter);
        Query messageQuery = mMessageRef.limitToLast(mCurrentPage * TOTAL_ITEM_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ReceivedMessage message = dataSnapshot.getValue(ReceivedMessage.class);

                itemPos++;

                if (itemPos == 1) {
                    String messageKey = dataSnapshot.getKey();
                    mLastKey = messageKey;
                    mPreKey = messageKey;
                }
                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                //ensure we see the last item on chat
                mMessageRecyclerView.scrollToPosition(messagesList.size() - 1);

                //turn off refreshing
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void sendMessages(TextView mChatMessageView) {
        String message = mChatMessageView.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            Map messageMap = presenter.setupMessageChatDB(mChatReceiverUser, message, State.messageDB);
            //set message input to blank
            mChatMessageView.setText("");
            presenter.getmRootRef().updateChildren(messageMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("CHAT_LOG", databaseError.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public void loadMoreMessages(final RecyclerView mMessageRecyclerView, final LinearLayoutManager mLinearLayout, final SwipeRefreshLayout mRefreshLayout) {
        Query messageQuery = mMessageRef.orderByKey().endAt(mLastKey).limitToLast(TOTAL_ITEM_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ReceivedMessage message = dataSnapshot.getValue(ReceivedMessage.class);
                String messageKey = dataSnapshot.getKey();

                if (!mPreKey.equals(messageKey)) {
                    messagesList.add(itemPos++, message);
                } else {
                    mPreKey = mLastKey;
                }
                if (itemPos == 1) {

                    mLastKey = messageKey;

                }

                mAdapter.notifyDataSetChanged();

                //ensure we see the last item on chat
                mMessageRecyclerView.scrollToPosition(messagesList.size() - 1);

                //turn off refreshing
                mRefreshLayout.setRefreshing(false);
                //stays at the position rather than going back to size -1
                mLinearLayout.scrollToPositionWithOffset(10, 0);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setupUI(ImageView mChatSendBtn, final TextView mChatMessageView,
                         final RecyclerView mMessageRecyclerView, final LinearLayoutManager mLinearLayout, final SwipeRefreshLayout mRefreshLayout) {

        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessages(mChatMessageView);
            }
        });
        loadMessages(mMessageRecyclerView, mRefreshLayout);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPos = 0;
                loadMoreMessages(mMessageRecyclerView, mLinearLayout, mRefreshLayout);
            }
        });
    }
}
