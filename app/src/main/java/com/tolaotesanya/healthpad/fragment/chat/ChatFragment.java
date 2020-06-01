package com.tolaotesanya.healthpad.fragment.chat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.modellayer.model.ChatConversation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";

    private View mMainView;
    private RecyclerView mConvList;
    private ChatPresenter chatPresenter;
    private TextView noReqReceived;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        mMainView = inflater.inflate(R.layout.fragment_chat, container, false);

        chatPresenter = new ChatPresenterImpl(getContext());

        mConvList = mMainView.findViewById(R.id.conv_list);
        noReqReceived = mMainView.findViewById(R.id.received_chat_msg);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mConvList.setLayoutManager(linearLayoutManager);

        return mMainView;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        chatPresenter.fetchChat(mConvList, noReqReceived);
    }

    @Override
    public void onStop() {
        super.onStop();
        chatPresenter.stopAdapter();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userName = mView.findViewById(R.id.chat_username);
            userName.setText(name);
        }

        public void setMessage(String message, String image, boolean isSeen) {
            TextView userMessage = mView.findViewById(R.id.chat_conv);
            if(!image.equals("image")){
                userMessage.setText(message);}
            else{
                userMessage.setText("an image");
            }
            if (!isSeen) {
                userMessage.setTypeface(userMessage.getTypeface(), Typeface.BOLD);
            } else{
                userMessage.setTypeface(userMessage.getTypeface(), Typeface.NORMAL);
            }
        }

        public void setUserOnline(String online_status) {
            ImageView userOnlineView = mView.findViewById(R.id.chat_online_icon);

            if(online_status.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            } else{
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }

        public void setUserImage(String thumb){
            CircleImageView userImageView = mView.findViewById(R.id.chat_users_image);
            Picasso.get().load(thumb).placeholder(R.drawable.health_pad_logo).into(userImageView);
        }


    }
}
