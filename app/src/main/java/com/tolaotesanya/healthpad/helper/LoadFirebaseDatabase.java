package com.tolaotesanya.healthpad.helper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.enums.State;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LoadFirebaseDatabase {
    private static final String TAG = "LoadFirebaseDatabase";

    private FirebasePresenter presenter;
    private String doctorid;

    public LoadFirebaseDatabase(FirebasePresenter presenter, String doctorid) {
        this.presenter = presenter;
        this.doctorid = doctorid;
    }

    public void loadDatabase(State mapType){
        Map databaseMap = presenter.setupDatabaseMap(doctorid, mapType);
        presenter.getmRootRef().updateChildren(databaseMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.d(TAG, "onComplete: " + databaseError);
                }
            }
        });
    }
}
