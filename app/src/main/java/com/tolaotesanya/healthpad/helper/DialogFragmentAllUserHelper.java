package com.tolaotesanya.healthpad.helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.tolaotesanya.healthpad.activities.doctors.AllDoctorPresenter;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.fragment.requests.RequestPresenter;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.enums.ClassName;
import com.tolaotesanya.healthpad.modellayer.enums.State;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogFragmentAllUserHelper extends DialogFragment {

    private FirebasePresenter presenter;
    private IntentPresenter intentPresenter;
    private String user_id;
    private String username;
    private String doctorDetails;
    private String doctorImage;
    private String monline;
    private LoadFirebaseDatabase loadDatabase;

    public DialogFragmentAllUserHelper(IntentPresenter intentPresenter, FirebasePresenter presenter,
                                       String user_id, String username, String doctorDetails,
                                       String doctorImage, String monline) {
        this.intentPresenter = intentPresenter;
        this.user_id = user_id;
        this.username = username;
        this.doctorDetails = doctorDetails;
        this.doctorImage = doctorImage;
        this.monline = monline;
        loadDatabase = new LoadFirebaseDatabase(presenter, user_id);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return getDialogAllDoctor();
    }

    public Dialog getDialogAllDoctor() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                if(!monline.equals("true")) {
                    builder.setTitle("Doctor Availability Check");
                    builder.setMessage(username + " is not Available at the moment.");
                    builder.setPositiveButton("Request Chat Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadDatabase.loadDatabase(State.not_consul);
                        }
                    });
                    builder.setNeutralButton("View Profile", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            intentPresenter.doctorProfileIntent(getContext(), user_id, username, doctorDetails, doctorImage);
                        }
                    });
                } else {
                    intentPresenter.doctorProfileIntent(getContext(), user_id, username, doctorDetails, doctorImage);
                }
        return builder.create();
    }

}
