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
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
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
    private String mdoctor_id;
    FirebaseRecyclerAdapter<Requests, RequestFragment.RequestsViewHolder> reqAdapter;


    public RequestPresenterImpl(Context context) {
        presenter = new FirebaseDatabaseLayer(context);
        intentPresenter = presenter.getIntentPresenter();
        mdoctor_id = presenter.getMcurrent_user_id();
        mConsultReqDatabase = presenter.getmRootRef()
                .child("Consultation_Req")
                .child(presenter.getMcurrent_user_id());
        mConsultReqDatabase.keepSynced(true);
        mUserDatabase = presenter.getmRootRef()
                .child("Users");

    }

    public IntentPresenter getIntentPresenter() {
        return intentPresenter;
    }

    public void loadDatabase(final Context context, String user_id, State mapType){
        Map databaseMap = presenter.setupDatabaseMap(user_id, mapType);
        presenter.getmRootRef().updateChildren(databaseMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(context, "There was an error",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void receivedAdapterSetup(RecyclerView mReceivedList, final TextView noReqReceived, final FragmentManager fragmentManager) {

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
                        mConsultReqDatabase.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String type = dataSnapshot.child("request_type").getValue().toString();
                                if(dataSnapshot.hasChild("request_type")){
                                    noReqReceived.setVisibility(View.INVISIBLE);
                                    mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
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
                                                            new DialogFragmentHelper(null, list_user_id, userName, null, null, ClassName.Request, null);
                                                    dialogFragmentHelper.show(fragmentManager, "DIALOG_FRAGMENT");
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                };
        reqAdapter.startListening();
        mReceivedList.setAdapter(reqAdapter);
    }

    public void stopAdapter(){
        reqAdapter.stopListening();
    }
}
