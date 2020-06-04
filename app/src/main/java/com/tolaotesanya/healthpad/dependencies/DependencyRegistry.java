package com.tolaotesanya.healthpad.dependencies;

import com.google.firebase.auth.FirebaseAuth;
import com.tolaotesanya.healthpad.activities.accountsettings.SettingsActivity;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;

public class DependencyRegistry {

    public static DependencyRegistry shared = new DependencyRegistry();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    //declare FirebasePresenter here once context is removed

    public void inject(SettingsActivity activity){
        FirebasePresenter presenter = new FirebaseDatabaseLayer(activity);
        IntentPresenter intentPresenter = new IntentPresenter(activity);
        activity.configureWith(presenter, intentPresenter);

    }


}
