package com.tolaotesanya.healthpad.fragment.posts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.helper.GetTimeAgo;
import com.tolaotesanya.healthpad.helper.PostsViewHolder;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.model.Posts;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostsPresenterImpl implements PostsPresenter {

    private FirebasePresenter presenter;
    private IntentPresenter intentPresenter;
    private FirebaseRecyclerAdapter<Posts, PostsViewHolder> mAdapter;
    private DatabaseReference mUserDatabase;
    private ValueEventListener mValueEventListener;
    private Context mContext;

    public PostsPresenterImpl(Context context, FirebasePresenter presenter, IntentPresenter intentPresenter) {
        this.mContext = context;
        this.presenter = presenter;
        this.intentPresenter = intentPresenter;
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
                final String user_id = model.getUser_id();
                final String post_key = getRef(position).getKey();

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
                                intentPresenter.postProfileIntent(mContext, poster_id, username, timePosted, postImage, caption, body);
                            }
                        });
                        holder.itemView.findViewById(R.id.feed_like_icon).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.setLikeButton();

                                int likesInt = Integer.parseInt(likes);
                                likesInt = likesInt + 1;
                                String newlikes = String.valueOf(likesInt);
                                holder.setLikes(newlikes);
                                presenter.getmRootRef().child("Posts")
                                        .child(post_key).child("likes").setValue(newlikes);
                            }
                        });
                        ImageView delete = holder.itemView.findViewById(R.id.delete_posts);
                        if (user_id.equals(presenter.getMcurrent_user_id())) {
                            delete.setVisibility(View.VISIBLE);
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        presenter.getmRootRef().child("Posts")
                                                .child(post_key).removeValue();
                                }
                            });
                        } else {
                            delete.setVisibility(View.INVISIBLE);
                        }
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
       // mUserDatabase.removeEventListener(mValueEventListener);
        mAdapter.stopListening();

    }

}
