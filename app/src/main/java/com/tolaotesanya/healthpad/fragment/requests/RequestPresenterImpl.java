package com.tolaotesanya.healthpad.fragment.requests;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.helper.DialogFragmentHelper;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.enums.ClassName;
import com.tolaotesanya.healthpad.modellayer.enums.State;
import com.tolaotesanya.healthpad.modellayer.model.Requests;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class RequestPresenterImpl implements RequestPresenter {

    private DatabaseReference mConsultReqDatabase;
    private FirebasePresenter presenter;
    private DatabaseReference mUserDatabase;
    private IntentPresenter intentPresenter;
    private FragmentManager fragmentManager;
    private ValueEventListener mValueEventListener;
    private String type;
    private FirebaseRecyclerAdapter<Requests, RequestFragment.RequestsViewHolder> reqAdapter;


    public RequestPresenterImpl(FirebasePresenter presenter,
                                FragmentManager fragmentManager, IntentPresenter intentPresenter) {
        this.presenter = presenter;
        this.fragmentManager = fragmentManager;
        this.intentPresenter = intentPresenter;
        mConsultReqDatabase = presenter.getmRootRef()
                .child("Consultation_Req")
                .child(presenter.getMcurrent_user_id());
        mConsultReqDatabase.keepSynced(true);
        mUserDatabase = presenter.getmRootRef()
                .child("Users");

    }

    public void receivedAdapterSetup(RecyclerView mReceivedList, final TextView noReqReceived) {

        Query friendsQuery = mConsultReqDatabase.orderByChild("request_type");

        FirebaseRecyclerOptions<Requests> options =
                new FirebaseRecyclerOptions.Builder<Requests>()
                        .setQuery(friendsQuery, Requests.class)
                        .build();

        reqAdapter =
                new FirebaseRecyclerAdapter<Requests, RequestFragment.RequestsViewHolder>(
                        options) {
                    @Override
                    public RequestFragment.RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.request_list_view, parent, false);
                        return new RequestFragment.RequestsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final RequestFragment.RequestsViewHolder holder, final int position, @NonNull final Requests model) {
                        final String list_user_id = getRef(position).getKey();

                        mValueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("request_type")){
                                    type = dataSnapshot.child("request_type").getValue().toString();
                                    noReqReceived.setVisibility(View.INVISIBLE);

                                    mValueEventListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            final String userName = dataSnapshot.child("name").getValue().toString();
                                            String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                                            if (dataSnapshot.hasChild("online")) {

                                                String userOnline = dataSnapshot.child("online").getValue().toString();
                                                holder.setUserOnline(userOnline);
                                            }
                                            holder.setName(userName);
                                            holder.setImage(userThumb);
                                            holder.setStatus(type);

                                            holder.mView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    DialogFragmentHelper dialogFragmentHelper =
                                                            new DialogFragmentHelper(intentPresenter, presenter, list_user_id, userName);
                                                    dialogFragmentHelper.show(fragmentManager, "DIALOG_FRAGMENT");
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    };

                                    mUserDatabase.child(list_user_id).addValueEventListener(mValueEventListener);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        };
                        mConsultReqDatabase.child(list_user_id).addValueEventListener(mValueEventListener);

                    }
                };
        reqAdapter.startListening();
        mReceivedList.setAdapter(reqAdapter);
    }

    public void stopAdapter(){
        reqAdapter.stopListening();
        if(type != null) {
            mConsultReqDatabase.removeEventListener(mValueEventListener);
            mUserDatabase.removeEventListener(mValueEventListener);
        }
    }
}
