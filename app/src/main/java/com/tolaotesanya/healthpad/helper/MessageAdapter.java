package com.tolaotesanya.healthpad.helper;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.model.ReceivedMessage;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter{
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<ReceivedMessage> mMessageList;
    private FirebasePresenter presenter;
    private DatabaseReference mUserDatabse;


    public MessageAdapter(List<ReceivedMessage> mMessageList, Context mContext) {
        this.mMessageList = mMessageList;
        presenter = new FirebaseDatabaseLayer(mContext);
    }

    @Override
    public int getItemViewType(int position) {
        String current_user_id = presenter.getMcurrent_user_id();

        ReceivedMessage message = mMessageList.get(position);

        if (message.getFrom().equals(current_user_id)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sent_message_list, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.received_message_list, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
          ReceivedMessage messages =  mMessageList.get(position);
        switch (holder.getItemViewType()){
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(messages);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(messages);
                getUserDetails(holder, messages);
                break;
        }

    }

    private void getUserDetails(@NonNull final RecyclerView.ViewHolder holder, ReceivedMessage messages) {
        String from_user = messages.getFrom();
        mUserDatabse = presenter.getmUserDatabase().child(from_user);

        mUserDatabse.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                ((ReceivedMessageHolder) holder).setDisplayName(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public static class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView messageImage;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            messageImage = itemView.findViewById(R.id.send_image_chat);
        }

        public void bind(ReceivedMessage message) {
            messageText.setVisibility(View.VISIBLE);
            messageText.setText(message.getMessage());
            if(message.getType().equals("image")){
                messageImage.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.GONE);
                Picasso.get().load(message.getMessage()).placeholder(R.drawable.ic_launcher_foreground).into(messageImage);
            }
            GetTime getTime = new GetTime();
            long timeNow = message.getTime();

            String lastSeenTime = getTime.getTime(timeNow, itemView.getContext());
            timeText.setText(lastSeenTime);
        }
    }


    public static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        CircleImageView profileImage;
        ImageView messageImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText =  itemView.findViewById(R.id.text_message_time);
            nameText =  itemView.findViewById(R.id.text_message_name);
            messageImage = itemView.findViewById(R.id.rec_image_view);

        }

        public void bind(ReceivedMessage message) {
            messageText.setVisibility(View.VISIBLE);
            messageText.setText(message.getMessage());
            if(message.getType().equals("image")){
                messageImage.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.GONE);
                Picasso.get().load(message.getMessage()).placeholder(R.drawable.ic_launcher_foreground).into(messageImage);
            }
            GetTime getTime = new GetTime();
            long timeNow = message.getTime();

            String lastSeenTime = getTime.getTime(timeNow, itemView.getContext());
            timeText.setText(lastSeenTime);
        }

        public void setDisplayName(String name) {
            nameText.setText(name);
        }

    }
}
