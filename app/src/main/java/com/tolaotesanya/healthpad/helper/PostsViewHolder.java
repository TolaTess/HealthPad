package com.tolaotesanya.healthpad.helper;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostsViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public PostsViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setCaption(String title) {
        TextView details = mView.findViewById(R.id.feed_caption);
        details.setText(title);
    }

    public void setTime(String time) {
        TextView timeView = mView.findViewById(R.id.time_posted);
        timeView.setText(time);
    }

    public void setPostDisplayType(String postImage, String body, String profile_image) {
        RelativeLayout imageLayout = mView.findViewById(R.id.relativeLayout_image);
        ImageView postImageView = mView.findViewById(R.id.feed_image);
        CircleImageView profileImageView = mView.findViewById(R.id.feed_profile_image);
        TextView bodyView = mView.findViewById(R.id.post_feed_body);
        bodyView.setText(body);
        Picasso.get().load(profile_image).placeholder(R.drawable.ic_launcher_foreground).into(profileImageView);
        if(postImage.equals("default")){
            imageLayout.setVisibility(View.GONE);
        } else {
            imageLayout.setVisibility(View.VISIBLE);
            Picasso.get().load(postImage).placeholder(R.drawable.ic_launcher_foreground).into(postImageView);
        }
    }
    public void setLikeButton() {
        ImageView likeImageView = mView.findViewById(R.id.feed_like_icon);
        Picasso.get().load(R.drawable.filled_heart).placeholder(R.drawable.heart).into(likeImageView);
    }

    public void setLikes(String likes) {
        TextView likesView = mView.findViewById(R.id.likes_count);
        String newLikes = likes + " likes";
        likesView.setText(newLikes);
    }

}
