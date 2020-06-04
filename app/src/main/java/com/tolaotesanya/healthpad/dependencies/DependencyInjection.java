package com.tolaotesanya.healthpad.dependencies;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.tolaotesanya.healthpad.activities.doctors.AllDoctorPresenter;
import com.tolaotesanya.healthpad.activities.doctors.AllDoctorPresenterImpl;
import com.tolaotesanya.healthpad.activities.doctors.AllDoctorsActivity;
import com.tolaotesanya.healthpad.activities.doctorsprofile.DoctorProfileActivity;
import com.tolaotesanya.healthpad.activities.doctorsprofile.DoctorProfilePresenter;
import com.tolaotesanya.healthpad.activities.doctorsprofile.DoctorProfilePresenterImpl;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.fragment.chat.ChatFragment;
import com.tolaotesanya.healthpad.fragment.chat.ChatPresenter;
import com.tolaotesanya.healthpad.fragment.chat.ChatPresenterImpl;
import com.tolaotesanya.healthpad.modellayer.database.FirebaseDatabaseLayer;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.enums.Constants;

import java.util.NoSuchElementException;

import androidx.fragment.app.FragmentManager;

public class DependencyInjection {

    public static DependencyInjection shared = new DependencyInjection();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private IntentPresenter intentPresenter = new IntentPresenter();

    public FirebasePresenter presenter = createFirebasePresenter();

    private FirebasePresenter createFirebasePresenter() {
        return new FirebaseDatabaseLayer(mAuth);
    }


    public void inject(AllDoctorsActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        AllDoctorPresenter allDoctorPresenter = new AllDoctorPresenterImpl(activity, presenter, fragmentManager);
        activity.configureWith(allDoctorPresenter);
    }

    public void inject(DoctorProfileActivity activity, Bundle bundle) {
        String doctorid = idFromBundleDoctor(bundle);

        DoctorProfilePresenter doctorProfilePresenter = new DoctorProfilePresenterImpl(presenter, doctorid);
        activity.configureWith(doctorProfilePresenter);
    }

    public void inject(ChatFragment fragment) {
        Context context = fragment.getContext();
        ChatPresenter chatPresenter = new ChatPresenterImpl(context, presenter, intentPresenter);
        fragment.configureWith(chatPresenter);
    }


    private String idFromBundleDoctor(Bundle bundle) {
        if (bundle == null) throw new NoSuchElementException("Unable to get user_id from bundle");

        String doctor_id = bundle.getString(Constants.doctorId);

        if (doctor_id == null)
            throw new NoSuchElementException("Unable to get user_id from bundle");
        return doctor_id;
    }

    private String idFromBundleUser(Bundle bundle) {
        if (bundle == null) throw new NoSuchElementException("Unable to get user_id from bundle");

        String userid = bundle.getString(Constants.userId);

        if (userid == null) throw new NoSuchElementException("Unable to get user_id from bundle");
        return userid;
    }


}
