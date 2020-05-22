package com.tolaotesanya.healthpad.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.model.Doctors;
import com.tolaotesanya.healthpad.model.Users;
import com.tolaotesanya.healthpad.utils.DoctorsViewHolder;

public class AllDoctorsActivity extends AppCompatActivity {
    private static final String TAG = "AllDoctorsActivity";
    
    private RecyclerView mDoctorList;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_doctors);

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
