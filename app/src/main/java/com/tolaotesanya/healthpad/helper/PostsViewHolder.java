package com.tolaotesanya.healthpad.helper;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.modellayer.model.Posts;

import org.w3c.dom.Text;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostsViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public PostsViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setDisplayName(String display_name) {
        TextView Name = mView.findViewById(R.id.feed_display_name);
        Name.setText(display_name);
    }

    public void setCaption(String caption) {
        TextView details = mView.findViewById(R.id.feed_caption);
        details.setText(caption);
    }

    public void setTime(String time){
        TextView timeView = mView.findViewById(R.id.time_posted);
        timeView.setText(time);
    }

    public void setPostImage(String postImage) {
        ImageView PostImageView = mView.findViewById(R.id.feed_image);
        Picasso.get().load(postImage).placeholder(R.drawable.ic_launcher_foreground).into(PostImageView);
    }

    public void setThumbImage(String profile_image) {
        CircleImageView profileImageView = mView.findViewById(R.id.feed_profile_image);
        Picasso.get().load(profile_image).placeholder(R.drawable.ic_launcher_foreground).into(profileImageView);
    }

    public void setLikeButton(){
        ImageView likeImageView = mView.findViewById(R.id.feed_like_icon);
        Picasso.get().load(R.drawable.filled_heart).placeholder(R.drawable.heart).into(likeImageView);
    }

    public void setLikes(String likes){
        TextView likesView = mView.findViewById(R.id.feed_likes_text);
        String newLikes = likes + " likes";
         likesView.setText(newLikes);
    }

}