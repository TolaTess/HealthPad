package com.tolaotesanya.healthpad.dependencies;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.tolaotesanya.healthpad.activities.accountsettings.AccountActivity;
import com.tolaotesanya.healthpad.activities.accountsettings.SettingsActivity;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;

import java.util.NoSuchElementException;

public class DependencyRegistry {

    public static DependencyRegistry shared = new DependencyRegistry();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private IntentPresenter intentPresenter = new IntentPresenter();

    public FirebasePresenter presenter = createFirebasePresenter();
    private FirebasePresenter createFirebasePresenter() {
        return new FirebaseDatabaseLayer(mAuth);
    }

    public void inject(SettingsActivity activity){
        activity.configureWith(presenter, intentPresenter);
    }

    public void inject(AccountActivity activity, Bundle bundle){
        String user_id = idFromBundle(bundle);
        activity.configureWith(presenter, intentPresenter);
    }

    private String idFromBundle(Bundle bundle) {
        if(bundle == null) throw new NoSuchElementException("Unable to get user_id from bundle");

        String userid = bundle.getString("user_id");
        if(userid == null) throw new NoSuchElementException("Unable to get user_id from bundle");
        return userid;
    }


}
