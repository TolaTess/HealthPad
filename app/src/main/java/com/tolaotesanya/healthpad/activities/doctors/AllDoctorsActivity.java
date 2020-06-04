package com.tolaotesanya.healthpad.activities.doctors;

import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.tolaotesanya.healthpad.R;
import com.tolaotesanya.healthpad.dependencies.DependencyInjection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AllDoctorsActivity extends AppCompatActivity {
    private static final String TAG = "AllDoctorsActivity";

    private RecyclerView mDoctorList;
    private FirebaseRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_doctors);

        setupToolbar();
        attachUI();

        DependencyInjection.shared.inject(this);
    }

    public void configureWith(AllDoctorPresenter allDoctorPresenter) {
        adapter = allDoctorPresenter.setupAdapter();
    }

    private void attachUI() {
        mDoctorList = findViewById(R.id.all_doctors_recycler);
        mDoctorList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        mDoctorList.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
        adapter.stopListening();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.all_doctors_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Doctors");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
