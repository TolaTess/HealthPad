package com.tolaotesanya.healthpad.fragment.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.enums.ClassName;
import com.tolaotesanya.healthpad.modellayer.model.ChatConversation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class ChatPresenterImpl implements ChatPresenter {

    private DatabaseReference mConsultDatabase;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mMessageDatabase;
    private IntentPresenter intentPresenter;
    private ValueEventListener mValueEventListener;
    private ChildEventListener mcEventListener;
    private Query lastMessageQuery;
    private Context mContext;
    private FirebaseRecyclerAdapter<ChatConversation, ChatFragment.ChatsViewHolder> chatsAdapter;

    public ChatPresenterImpl(Context mContext, FirebasePresenter presenter, IntentPresenter intentPresenter) {
        this.mContext = mContext;
        this.intentPresenter = intentPresenter;
        mConsultDatabase = presenter.getmRootRef().child("Consultations")
                .child(presenter.getMcurrent_user_id());
        mConsultDatabase.keepSynced(true);
        mMessageDatabase = presenter.getmRootRef()
                .child("Messages").child(presenter.getMcurrent_user_id());
        mMessageDatabase.keepSynced(true);
        mUserDatabase = presenter.getmRootRef()
                .child("Users");
    }

    public void fetchChat(RecyclerView mConvList, final TextView noReqReceived) {
        Query convQuery = mConsultDatabase.orderByChild("timestamp");

        FirebaseRecyclerOptions<ChatConversation> options =
                new FirebaseRecyclerOptions.Builder<ChatConversation>()
                        .setQuery(convQuery, ChatConversation.class)
                        .build();


        chatsAdapter =
                new FirebaseRecyclerAdapter<ChatConversation, ChatFragment.ChatsViewHolder>(
                        options) {
                    @Override
                    public ChatFragment.ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.chat_list_view, parent, false);
                        return new ChatFragment.ChatsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final ChatFragment.ChatsViewHolder holder, final int position, @NonNull final ChatConversation model) {
                        final String list_user_id = getRef(position).getKey();
                        lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

                        mcEventListener = new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                if (dataSnapshot.hasChild("message")) {
                                    noReqReceived.setVisibility(View.INVISIBLE);
                                    String messageType = dataSnapshot.child("type").getValue().toString();
                                    String data = dataSnapshot.child("message").getValue().toString();
                                    holder.setMessage(data, messageType, model.isSeen());
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
                        };
                        lastMessageQuery.addChildEventListener(mcEventListener);

                        mValueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String userName = dataSnapshot.child("name").getValue().toString();
                                String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                                if (dataSnapshot.hasChild("online")) {
                                    String userOnline = dataSnapshot.child("online").getValue().toString();
                                    holder.setUserOnline(userOnline);
                                }
                                holder.setName(userName);
                                holder.setUserImage(userThumb);

                                holder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        intentPresenter.presentIntent(mContext, ClassName.Chats, list_user_id, userName);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        };
                        mUserDatabase.child(list_user_id).addValueEventListener(mValueEventListener);
                    }
                };

        chatsAdapter.startListening();
        mConvList.setAdapter(chatsAdapter);
    }

    public void stopAdapter() {
        chatsAdapter.stopListening();
        if(lastMessageQuery != null) {
            lastMessageQuery.removeEventListener(mcEventListener);
            mUserDatabase.removeEventListener(mValueEventListener);
        }
    }

}
