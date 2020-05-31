package com.tolaotesanya.healthpad.activities.doctors;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.helper.DialogFragmentHelper;
import com.tolaotesanya.healthpad.helper.DoctorsViewHolder;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.enums.ClassName;
import com.tolaotesanya.healthpad.modellayer.model.Doctors;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AllDoctorsActivity extends AppCompatActivity {
    private static final String TAG = "AllDoctorsActivity";
    
    private RecyclerView mDoctorList;
    private FirebasePresenter presenter;
    private FirebaseRecyclerAdapter adapter;
    private Context mContext = AllDoctorsActivity.this;
    private String userOnline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_doctors);
        presenter = new FirebaseDatabaseLayer(mContext);

        setupToolbar();

        mDoctorList = findViewById(R.id.all_doctors_recycler);
        mDoctorList.setLayoutManager(new LinearLayoutManager(this));
        fetch();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.all_doctors_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Doctors");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void checkLastSeenOnline(String doctor_id) {

    }

    private void fetch() {
        DatabaseReference databaseReference = presenter.getmRootRef()
                .child("Doctors");

        final FirebaseRecyclerOptions<Doctors> options =
                new FirebaseRecyclerOptions.Builder<Doctors>()
                        .setQuery(databaseReference, Doctors.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Doctors, DoctorsViewHolder>(
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
                holder.setDetails(model.getSpeciality(), model.getLocation());
                holder.setImage(model.getThumb_image());

                final String doctorid = getRef(position).getKey();
                presenter.getmUserDatabase()
                        .child(doctorid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("online")) {
                            userOnline = dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(userOnline);
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //more options or add fragment dialog
                        DialogFragmentHelper dialogFragmentHelper =
                                new DialogFragmentHelper(null, presenter, doctorid, model.getFirst_name(), ClassName.AllDoctors, userOnline);
                        dialogFragmentHelper.setCancelable(false);
                        dialogFragmentHelper.show(getSupportFragmentManager(), "DIALOG_FRAGMENT");
                    }
                });
            }
        };
        mDoctorList.setAdapter(adapter);
    }
    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
        adapter.stopListening();
    }

}