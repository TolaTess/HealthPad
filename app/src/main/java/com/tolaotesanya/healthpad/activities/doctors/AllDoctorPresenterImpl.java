package com.tolaotesanya.healthpad.activities.doctors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.helper.DialogFragmentAllUserHelper;
import com.tolaotesanya.healthpad.helper.DoctorsViewHolder;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.model.Doctors;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

public class AllDoctorPresenterImpl implements AllDoctorPresenter {
    private static final String TAG = "AllDoctorPresenterImpl";

    private FirebasePresenter presenter;
    private FragmentManager fragmentManager;
    private IntentPresenter intentPresenter;
    private Context mContext;
    private String doctorid;
    private String userOnline;

    public AllDoctorPresenterImpl(Context context, FirebasePresenter firebasePresenter, FragmentManager fragmentManager, IntentPresenter intentPresenter) {
        this.mContext = context;
        this.presenter = firebasePresenter;
        this.fragmentManager = fragmentManager;
        this.intentPresenter = intentPresenter;
    }

    public FirebaseRecyclerAdapter setupAdapter(String data) {

        DatabaseReference doctorQuery = presenter.getmRootRef()
                .child("Doctors");
        Query query = doctorQuery.orderByChild("last_name").startAt(data).endAt(data+"\uf8ff");

        FirebaseRecyclerOptions<Doctors> options = new FirebaseRecyclerOptions.Builder<Doctors>()
                .setQuery(query, Doctors.class)
                .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Doctors, DoctorsViewHolder>(
                options) {
            @Override
            public DoctorsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: ");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_doctors_list, parent, false);
                return new DoctorsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final DoctorsViewHolder holder, final int position, final Doctors model) {
                Log.d(TAG, "onBindViewHolder: ");
                final String fullName = "Dr " + model.getFirst_name() + " " + model.getLast_name();
                holder.setFullName(fullName);
                final String detailsString = model.getSpeciality() + " based in \n" + model.getLocation();
                holder.setDetails(detailsString);

                doctorid = getRef(position).getKey();

                presenter.getmUserDatabase()
                        .child(doctorid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("online")) {
                            userOnline = dataSnapshot.child("online").getValue().toString();
                            String profileImage = dataSnapshot.child("thumb_image").getValue().toString();
                            model.setImage(profileImage);
                            holder.setUserOnline(userOnline);
                            holder.setImage(model.getImage());
                            Log.d(TAG, "onBindViewHolder: " + "profile image " + profileImage);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragmentAllUserHelper dialogFragmentAllUserHelper =
                                new DialogFragmentAllUserHelper(intentPresenter, presenter, doctorid, fullName, detailsString, model.getImage(), userOnline);
                        dialogFragmentAllUserHelper.show(fragmentManager, "DIALOG_FRAGMENT");
                    }
                });
            }
        };
        return adapter;
    }

}
