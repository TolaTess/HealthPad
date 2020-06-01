package com.tolaotesanya.healthpad.fragment.posts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.tolaotesanya.healthpad.fragment.chat.ChatFragment;
import com.tolaotesanya.healthpad.helper.GetTimeAgo;
import com.tolaotesanya.healthpad.helper.PostsViewHolder;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.model.ChatConversation;
import com.tolaotesanya.healthpad.modellayer.model.Posts;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostsPresenterImpl implements PostsPresenter {

    private FirebasePresenter presenter;
    private FirebaseRecyclerAdapter<Posts, PostsViewHolder> mAdapter;
    private DatabaseReference mUserDatabase;
    private ValueEventListener mValueEventListener;
    private Context mContext;
    FirebaseRecyclerAdapter<ChatConversation, ChatFragment.ChatsViewHolder> chatsAdapter;

    public PostsPresenterImpl(Context context) {
        this.mContext = context;
        presenter = new FirebaseDatabaseLayer(context);
        mUserDatabase = presenter.getmRootRef()
                .child("Users");
    }

    public void fetchRequest(RecyclerView mPostRecycler) {

        final Query postQuery = presenter.getmRootRef().child("Posts");
        
        FirebaseRecyclerOptions<Posts> options =
                new FirebaseRecyclerOptions.Builder<Posts>()
                        .setQuery(postQuery, Posts.class)
                        .build();
        mAdapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(
                options) {
            @Override
            public PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_feed_list, parent, false);
                return new PostsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final PostsViewHolder holder, final int position, final Posts model) {
                long timeofPost = model.getTimestamp();
                GetTimeAgo getTimeAgo = new GetTimeAgo();
                final String timePosted = getTimeAgo.getTimeAgo(timeofPost, mContext);
                holder.setTime(timePosted);
                final String caption = model.getTitle();
                holder.setCaption(caption);
                final String body = model.getBody();
                final String postImage = model.getPost_image();
                final String poster_id = model.getUser_id();

                mValueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String username = dataSnapshot.child("name").getValue().toString();
                        final String profile_image = dataSnapshot.child("thumb_image").getValue().toString();
                        holder.setPostDisplayType(postImage, body, profile_image);
                        final String likes = model.getLikes();
                        holder.setLikes(likes);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                presenter.getIntentPresenter().postProfileIntent(poster_id, username, timePosted, postImage, caption, body);
                            }
                        });
                        holder.itemView.findViewById(R.id.feed_like_icon).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.setLikeButton();
                                String post_key = getRef(position).getKey();
                                int likesInt = Integer.parseInt(likes);
                                likesInt = likesInt + 1;
                                String newlikes = String.valueOf(likesInt);
                                holder.setLikes(newlikes);
                                presenter.getmRootRef().child("Posts")
                                        .child(post_key).child("likes").setValue(newlikes);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                mUserDatabase.child(poster_id).addValueEventListener(mValueEventListener);
            }
        };
        mAdapter.startListening();
        mPostRecycler.setAdapter(mAdapter);
    }

    public void stopAdapter() {
        mAdapter.stopListening();
        mUserDatabase.removeEventListener(mValueEventListener);
    }

}
