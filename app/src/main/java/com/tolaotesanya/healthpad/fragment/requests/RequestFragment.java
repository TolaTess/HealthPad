package com.tolaotesanya.healthpad.fragment.requests;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tolaotesanya.healthpad.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class RequestFragment extends Fragment {
    private static final String TAG = "RequestFragment";

    private View mMainView;
    private RecyclerView mReceivedList;
    private RequestPresenter requestPresenter;
    private TextView noReqReceived;
    private FragmentManager fm;


    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_request, container, false);
        requestPresenter = new RequestPresenterImpl(getContext());
        fm = getFragmentManager();

        mReceivedList = mMainView.findViewById(R.id.allrecived_recycler);
        noReqReceived = mMainView.findViewById(R.id.received_req_msg);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mReceivedList.setLayoutManager(linearLayoutManager);

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        requestPresenter.receivedAdapterSetup(mReceivedList, noReqReceived, fm);
    }

    @Override
    public void onStop() {
        super.onStop();
        requestPresenter.stopAdapter();

    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "RequestsViewHolder";

        View mView;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userName = mView.findViewById(R.id.req_users_name);
            userName.setText(name);
        }
        public void setStatus(String request_type) {
            TextView request = mView.findViewById(R.id.req_users_status);
            if (request_type.equals("received")){
                request.setText(request_type);
                request.setTextColor(Color.GREEN);}
            else{
                request.setText(request_type);
            }
        }

        public void setImage(String thumb_image) {
            CircleImageView mThumbImage = mView.findViewById(R.id.req_users_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.health_pad_logo).into(mThumbImage);
        }

        public void setUserOnline(String online_status) {
            ImageView userOnlineView = mView.findViewById(R.id.req_online_icon);

            if (online_status.equals("true")) {
                userOnlineView.setVisibility(View.VISIBLE);
            } else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }

    }
}
