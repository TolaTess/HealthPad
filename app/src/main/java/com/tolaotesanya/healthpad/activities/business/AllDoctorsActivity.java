package com.tolaotesanya.healthpad.activities.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.activities.profile.DoctorProfileActivity;
import com.tolaotesanya.healthpad.helper.DisplayScreen;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.model.Doctors;
import com.tolaotesanya.healthpad.helper.DoctorsViewHolder;

public class AllDoctorsActivity extends AppCompatActivity {
    private static final String TAG = "AllDoctorsActivity";
    
    private RecyclerView mDoctorList;
    private DatabaseReference databaseReference;
    private FirebasePresenter presenter;
    private FirebaseRecyclerAdapter adapter;

    private String mCurrentUser_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_doctors);
        presenter = new FirebaseDatabaseLayer();
        mCurrentUser_id = presenter.getMcurrent_user_id();

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

    private void fetch() {
        databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Doctors");

        FirebaseRecyclerOptions<Doctors> options =
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
            protected void onBindViewHolder(DoctorsViewHolder holder, int position, Doctors model) {
                Log.d(TAG, "onBindViewHolder: ");

                holder.setFullName(model.getFirst_name(), model.getLast_name());
                holder.setDetails(model.getSpeciality(), model.getLocation());
                holder.setImage(model.getThumb_image());

                final String doctorid = getRef(position).getKey();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(AllDoctorsActivity.this, DoctorProfileActivity.class);
                        profileIntent.putExtra("doctor_id", doctorid); // send user id to use it to get all other info in db
                        startActivity(profileIntent);
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
