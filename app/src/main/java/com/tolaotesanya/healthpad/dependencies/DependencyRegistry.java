package com.tolaotesanya.healthpad.dependencies;

import com.google.firebase.auth.FirebaseAuth;
import com.tolaotesanya.healthpad.activities.MainActivity;
import com.tolaotesanya.healthpad.activities.accountsettings.AccountActivity;
import com.tolaotesanya.healthpad.activities.accountsettings.SettingsActivity;
import com.tolaotesanya.healthpad.activities.auth.AuthActivity;
import com.tolaotesanya.healthpad.activities.auth.DoctorRegisterActivity;
import com.tolaotesanya.healthpad.activities.auth.LoginActivity;
import com.tolaotesanya.healthpad.activities.auth.RegisterActivity;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;

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

    public void inject(AccountActivity activity){
        activity.configureWith(presenter, intentPresenter);
    }

    public void inject(LoginActivity activity){
        activity.configureWith(mAuth, intentPresenter);
    }

    public void inject(AuthActivity activity){
        activity.configureWith(presenter, intentPresenter);
    }

    public void inject(DoctorRegisterActivity activity){
        activity.configureWith(presenter, intentPresenter);
    }

    public void inject(RegisterActivity activity){
        activity.configureWith(mAuth, presenter, intentPresenter);
    }

    public void inject(MainActivity activity){
        activity.configureWith(presenter, intentPresenter);
    }


}
