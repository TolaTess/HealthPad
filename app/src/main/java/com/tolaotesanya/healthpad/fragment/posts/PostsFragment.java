package com.tolaotesanya.healthpad.fragment.posts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.helper.GetTimeAgo;
import com.tolaotesanya.healthpad.helper.PostsViewHolder;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.model.Posts;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class PostsFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private RecyclerView mPostRecycler;

    //Firebase
    private FirebasePresenter presenter;
    private FirebaseRecyclerAdapter<Posts, PostsViewHolder> mAdapter;

    public PostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: HomeFragment");
        View mMainView = inflater.inflate(R.layout.fragment_posts, container, false);

        presenter = new FirebaseDatabaseLayer(getContext());

        mPostRecycler = mMainView.findViewById(R.id.home_feed_list);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mMainView.getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mPostRecycler.setLayoutManager(mLayoutManager);
        fetch();

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    private void fetch() {
        Log.d(TAG, "fetch: HomeFragment");

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
                final String timePosted = getTimeAgo.getTimeAgo(timeofPost, getContext());
                holder.setTime(timePosted);
                final String caption = model.getTitle();
                holder.setCaption(caption);
                final String body = model.getBody();
                final String postImage = model.getPost_image();
                final String poster_id = model.getUser_id();
                presenter.getmUserDatabase().child(poster_id).addValueEventListener(new ValueEventListener() {
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
                });
            }
        };
        mPostRecycler.setAdapter(mAdapter);
    }
}
