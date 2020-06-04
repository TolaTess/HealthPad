package com.tolaotesanya.healthpad.helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.tolaotesanya.healthpad.coordinator.IntentPresenter;
import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;
import com.tolaotesanya.healthpad.modellayer.enums.ClassName;
import com.tolaotesanya.healthpad.modellayer.enums.State;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogFragmentHelper extends DialogFragment {

    private IntentPresenter intentPresenter;
    private String user_id;
    private String username;
    private LoadFirebaseDatabase loadDatabase;


    public DialogFragmentHelper(IntentPresenter intentPresenter, FirebasePresenter presenter,
                                String user_id, String username) {
        this.intentPresenter = intentPresenter;
        this.user_id = user_id;
        this.username = username;
        loadDatabase = new LoadFirebaseDatabase(presenter, user_id);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return getDialog();
    }

    public Dialog getDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Requests");
            builder.setMessage("Please action request");
            builder.setPositiveButton("Chat Now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadDatabase.loadDatabase(State.request_received);
                    intentPresenter.presentIntent(getContext(), ClassName.Chats, user_id, username);
                }
            });
            builder.setNeutralButton("Decline", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    Toast.makeText(getContext(), "Decline ", Toast.LENGTH_SHORT).show();
                    loadDatabase.loadDatabase(State.request_sent);
                }
            });

        return builder.create();
    }

}
