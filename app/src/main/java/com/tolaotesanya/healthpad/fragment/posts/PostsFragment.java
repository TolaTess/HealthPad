package com.tolaotesanya.healthpad.fragment.posts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.dependencies.DependencyInjection;
import com.tolaotesanya.healthpad.helper.PostsViewHolder;
import com.tolaotesanya.healthpad.modellayer.model.Posts;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class PostsFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private RecyclerView mPostRecycler;

    //Firebase
    private PostsPresenter presenter;

    public PostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: HomeFragment");
        View mMainView = inflater.inflate(R.layout.fragment_posts, container, false);

        mPostRecycler = mMainView.findViewById(R.id.home_feed_list);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mMainView.getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mPostRecycler.setLayoutManager(mLayoutManager);

        DependencyInjection.shared.inject(this);
        return mMainView;
    }

    public void configureWith(PostsPresenter postsPresenter) {
        this.presenter = postsPresenter;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.fetchRequest(mPostRecycler);
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.stopAdapter();
    }

}
