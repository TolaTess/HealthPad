package com.tolaotesanya.healthpad.helper;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorsViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public DoctorsViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setFullName(String fName) {
        TextView fullName = mView.findViewById(R.id.doctors_name);
        fullName.setText(fName);
    }

    public void setDetails(String detailsString) {
        TextView details = mView.findViewById(R.id.doctors_details);
        details.setText(detailsString);
    }

    public void setImage(String thumb_image) {
        CircleImageView mThumbImage = mView.findViewById(R.id.doctors_image);
        Picasso.get().load(thumb_image).placeholder(R.drawable.health_pad_logo).into(mThumbImage);
    }

    public void setUserOnline(String online) {
        ImageView mOnlineView = mView.findViewById(R.id.all_user_online_icon);
        TextView mLastSeen = mView.findViewById(R.id.all_user_lastseen);
        if(online.equals("true")){
            mOnlineView.setVisibility(View.VISIBLE);

        } else{
            mOnlineView.setVisibility(View.INVISIBLE);
            GetTimeAgo getTimeAgo = new GetTimeAgo();
            long lastTime = Long.parseLong(online);

            String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, mView.getContext());
            String lastseenText = "Avaliable " + lastSeenTime;
            mLastSeen.setText(lastseenText);
        }
    }
}
