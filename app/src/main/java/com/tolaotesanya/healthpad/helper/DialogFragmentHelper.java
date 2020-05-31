package com.tolaotesanya.healthpad.helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.tolaotesanya.healthpad.activities.doctorsprofile.DoctorProfilePresenter;
import com.tolaotesanya.healthpad.activities.doctorsprofile.DoctorProfilePresenterImpl;
import com.tolaotesanya.healthpad.fragment.requests.RequestPresenter;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.enums.ClassName;
import com.tolaotesanya.healthpad.modellayer.enums.State;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogFragmentHelper extends DialogFragment {

    private RequestPresenter presenter;
    private FirebasePresenter firebasePresenter;
    private DoctorProfilePresenter profilePresenter;
    private String user_id;
    private String username;
    private String doctorDetails;
    private String doctorImage;
    private ClassName dialog;
    private String monline;

    public DialogFragmentHelper(RequestPresenter presenter, FirebasePresenter firebasePresenter,
                                String user_id, String username, String doctorDetails,
                                String doctorImage, ClassName dialog, String monline) {
        this.presenter = presenter;
        this.firebasePresenter = firebasePresenter;
        this.user_id = user_id;
        this.username = username;
        this.doctorDetails = doctorDetails;
        this.doctorImage = doctorImage;
        this.dialog = dialog;
        this.monline = monline;
        profilePresenter = new DoctorProfilePresenterImpl(getContext(), user_id);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return getDialog();
    }

    public Dialog getDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch (dialog) {
            case Request:
            builder.setTitle("Requests");
            builder.setMessage("Please action request");
            builder.setPositiveButton("Chat Now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    presenter.loadDatabase(getContext(), user_id, State.request_received);
                    presenter.getIntentPresenter().presentIntent(ClassName.Chats, user_id, username);
                }
            });
            builder.setNeutralButton("Decline", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    Toast.makeText(getContext(), "Decline ", Toast.LENGTH_SHORT).show();
                    presenter.loadDatabase(getContext(), user_id, State.request_sent);
                }
            });
            break;
            case AllDoctors:
                if(!monline.equals("true")) {
                    builder.setTitle("Doctor Availability Check");
                    builder.setMessage(username + " is not Available at the moment.");
                    builder.setPositiveButton("Request for Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            profilePresenter.loadDatabase(getContext(), State.not_consul);
                        }
                    });
                    builder.setNeutralButton("view Profile", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            firebasePresenter.getIntentPresenter().doctorProfileIntent(user_id, username, doctorDetails, doctorImage);
                        }
                    });
                } else {
                    firebasePresenter.getIntentPresenter().doctorProfileIntent(user_id, username, doctorDetails, doctorImage);
                }
                break;
        }
        return builder.create();
    }

}
