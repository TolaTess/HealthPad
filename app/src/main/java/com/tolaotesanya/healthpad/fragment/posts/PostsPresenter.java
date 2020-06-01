package com.tolaotesanya.healthpad.fragment.posts;

import androidx.recyclerview.widget.RecyclerView;

public interface PostsPresenter {

    void fetchRequest(RecyclerView mPostRecycler);
    void stopAdapter();
}
