package com.tolaotesanya.healthpad.fragment.chat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.helper.GetTimeAgo;
import com.tolaotesanya.healthpad.modellayer.enums.ClassName;
import com.tolaotesanya.healthpad.modellayer.model.ChatConversation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";

    private View mMainView;
    private RecyclerView mConvList;
    private ChatPresenter chatPresenter;

    FirebaseRecyclerAdapter<ChatConversation, ChatsViewHolder> chatsAdapter;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        mMainView = inflater.inflate(R.layout.fragment_chat, container, false);

        chatPresenter = new ChatPresenterImpl(getContext());

        mConvList = mMainView.findViewById(R.id.conv_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mConvList.setLayoutManager(linearLayoutManager);

        return mMainView;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();

        Query convQuery = chatPresenter.getmConvDatabase().orderByChild("timestamp");

        FirebaseRecyclerOptions<ChatConversation> options =
                new FirebaseRecyclerOptions.Builder<ChatConversation>()
                        .setQuery(convQuery, ChatConversation.class)
                        .build();


        chatsAdapter =
                new FirebaseRecyclerAdapter<ChatConversation, ChatsViewHolder>(
                        options) {
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        Log.d(TAG, "onCreateViewHolder: ");
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.chat_list_view, parent, false);
                        return new ChatsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, final int position, @NonNull final ChatConversation model) {
                        Log.d(TAG, "onBindViewHolder: ");
                        //holder.setDate(model.getDate_time());
                        final String list_user_id = getRef(position).getKey();
                        Query lastMessageQuery = chatPresenter.getmMessageDatabase().child(list_user_id).limitToLast(1);

                        lastMessageQuery.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                if(dataSnapshot.hasChild("message")) {
                                    String data = dataSnapshot.child("message").getValue().toString();
                                    holder.setMessage(data, model.isSeen());
                                    long timeofPost = model.getTimestamp();
                                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                                    String timePosted = getTimeAgo.getTimeAgo(timeofPost, getContext());
                                    holder.setTime(timePosted);

                                }
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
                        chatPresenter.getmUserChatDatabase().child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String userName = dataSnapshot.child("name").getValue().toString();
                                String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                                if(dataSnapshot.hasChild("online")){
                                    String userOnline = dataSnapshot.child("online").getValue().toString();
                                    holder.setUserOnline(userOnline);
                                }
                                holder.setName(userName);
                                holder.setUserImage(userThumb);

                                holder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        chatPresenter.getIntentPresenter().presentIntent(ClassName.Chats, list_user_id, userName);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                };

        chatsAdapter.startListening();
        mConvList.setAdapter(chatsAdapter);

    }

    @Override
    public void onStop() {
        super.onStop();
        chatsAdapter.stopListening();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userName = mView.findViewById(R.id.chat_username);
            userName.setText(name);
        }

        public void setMessage(String message, boolean isSeen) {
            TextView userStatus = mView.findViewById(R.id.chat_conv);
            userStatus.setText(message);
            if (!isSeen) {
                userStatus.setTypeface(userStatus.getTypeface(), Typeface.BOLD);
            } else{
                userStatus.setTypeface(userStatus.getTypeface(), Typeface.NORMAL);
            }
        }
        public void setTime(String time){
            TextView timeView = mView.findViewById(R.id.time_posted);
            timeView.setText(time);
        }

        public void setUserOnline(String online_status) {
            ImageView userOnlineView = mView.findViewById(R.id.chat_online_icon);

            if(online_status.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            } else{
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }

        public void setUserImage(String thumb){
            CircleImageView userImageView = mView.findViewById(R.id.chat_users_image);
            Picasso.get().load(thumb).placeholder(R.drawable.ic_launcher_foreground).into(userImageView);
        }


    }
}
