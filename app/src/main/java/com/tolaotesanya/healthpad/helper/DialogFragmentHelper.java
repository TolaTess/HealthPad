package com.tolaotesanya.healthpad.helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.tolaotesanya.healthpad.fragment.requests.RequestPresenter;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.enums.ClassName;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogFragmentHelper extends DialogFragment {

    private RequestPresenter presenter;
    private FirebasePresenter firebasePresenter;
    private String user_id;
    private String username;
    private ClassName dialog;

    public DialogFragmentHelper(RequestPresenter presenter, FirebasePresenter firebasePresenter, String user_id, String username, ClassName dialog) {
        this.presenter = presenter;
        this.firebasePresenter = firebasePresenter;
        this.user_id = user_id;
        this.username = username;
        this.dialog = dialog;
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
                    presenter.getIntentPresenter().presentIntent(ClassName.Chats, user_id, username);
                }
            });
            builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    Toast.makeText(getContext(), "Decline ", Toast.LENGTH_SHORT).show();
                    presenter.loadDatabase(getContext(), user_id, State.request_sent);
                }
            });
            break;
            case AllDoctors:
                builder.setTitle("Doctor Profile");
                builder.setMessage("Do you want to Request a Consultation?");
                builder.setPositiveButton("Request Chat Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebasePresenter.getIntentPresenter().presentIntent(ClassName.Profile, user_id, username);
                    }
                });
                break;


        }
        return builder.create();
    }

}
