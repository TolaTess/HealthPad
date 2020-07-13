package com.tolaotesanya.healthpad.activities.doctors;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

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
    private AllDoctorPresenter presenter;
    private EditText searchDoctors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_doctors);

        setupToolbar();
        attachUI();

        DependencyInjection.shared.inject(this);
    }

    public void configureWith(final AllDoctorPresenter allDoctorPresenter) {
        this.presenter = allDoctorPresenter;
    }

    private void attachUI() {
        mDoctorList = findViewById(R.id.all_doctors_recycler);
        mDoctorList.setLayoutManager(new LinearLayoutManager(this));
        searchDoctors = findViewById(R.id.search_box);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        adapter = presenter.setupAdapter("");
        searchDoctors.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString() != null){
                    adapter = presenter.setupAdapter(s.toString());
                } else {
                    adapter = presenter.setupAdapter("");
                }
                mDoctorList.setAdapter(adapter);
                adapter.startListening();
            }
        });
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
