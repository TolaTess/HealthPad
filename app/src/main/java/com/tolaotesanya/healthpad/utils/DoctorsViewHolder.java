package com.tolaotesanya.healthpad.utils;

import android.view.View;
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

    public void setFullName(String fname, String lName){
        TextView fullName = mView.findViewById(R.id.doctors_name);
        String nameString = "Dr " + fname + " " + lName;
        fullName.setText(nameString);}

    public void setDetails(String speciality, String location){
        TextView details = mView.findViewById(R.id.doctors_details);
        String detailsString = "I am a " + speciality +
                "\n" + "based in " + location;
        details.setText(detailsString);
    }

    public void setImage(String thumb_image){
        CircleImageView mThumbImage = mView.findViewById(R.id.doctors_image);
            //placeholder holds a picture on file before the getting database
            Picasso.get().load(thumb_image).placeholder(R.drawable.ic_launcher_foreground).into(mThumbImage);
    }
}
