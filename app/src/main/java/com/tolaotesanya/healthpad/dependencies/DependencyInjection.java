package com.tolaotesanya.healthpad.dependencies;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.tolaotesanya.healthpad.activities.chat.ChatActivity;
import com.tolaotesanya.healthpad.activities.chat.ChatActivityPresenter;
import com.tolaotesanya.healthpad.activities.chat.ChatPresenterActivityImpl;
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
import com.tolaotesanya.healthpad.fragment.posts.PostsFragment;
import com.tolaotesanya.healthpad.fragment.posts.PostsPresenter;
import com.tolaotesanya.healthpad.fragment.posts.PostsPresenterImpl;
import com.tolaotesanya.healthpad.fragment.requests.RequestFragment;
import com.tolaotesanya.healthpad.fragment.requests.RequestPresenter;
import com.tolaotesanya.healthpad.fragment.requests.RequestPresenterImpl;
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
        AllDoctorPresenter allDoctorPresenter = new AllDoctorPresenterImpl(activity, presenter, fragmentManager, intentPresenter);
        activity.configureWith(allDoctorPresenter);
    }

    public void inject(DoctorProfileActivity activity, Bundle bundle) {
        String doctorid = idFromBundleDoctor(bundle);
        String name = bundle.getString(Constants.fullname);
        String image = bundle.getString(Constants.image);
        String details = bundle.getString(Constants.details);

        DoctorProfilePresenter doctorProfilePresenter = new DoctorProfilePresenterImpl(presenter, doctorid, name, image, details);
        activity.configureWith(doctorProfilePresenter);
    }

    public void inject(ChatFragment fragment) {
        Context context = fragment.getContext();
        ChatPresenter chatPresenter = new ChatPresenterImpl(context, presenter, intentPresenter);
        fragment.configureWith(chatPresenter);
    }

    public void inject(RequestFragment fragment) {
        FragmentManager fragmentManager = fragment.getFragmentManager();
        RequestPresenter requestPresenter = new RequestPresenterImpl(presenter, fragmentManager, intentPresenter);
        fragment.configureWith(requestPresenter);
    }

    public void inject(PostsFragment fragment) {
        Context context = fragment.getContext();
        PostsPresenter postsPresenter = new PostsPresenterImpl(context, presenter, intentPresenter);
        fragment.configureWith(postsPresenter);
    }

    public void inject(ChatActivity activity, Bundle bundle) {
        String doctorid = idFromBundleDoctor(bundle);
        String username = bundle.getString(Constants.username);

        ChatActivityPresenter chatActivityPresenter = new ChatPresenterActivityImpl(activity, presenter, doctorid, username);
        activity.configureWith(chatActivityPresenter);
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
