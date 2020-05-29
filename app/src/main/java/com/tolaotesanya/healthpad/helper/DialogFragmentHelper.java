package com.tolaotesanya.healthpad.helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.tolaotesanya.healthpad.fragment.requests.RequestPresenter;
import com.tolaotesanya.healthpad.modellayer.enums.ClassName;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogFragmentHelper extends DialogFragment {

    private RequestPresenter presenter;
    private String user_id;
    private String username;

    public DialogFragmentHelper(RequestPresenter presenter, String user_id, String username) {
        this.presenter = presenter;
        this.user_id = user_id;
        this.username = username;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        CharSequence options[] = new CharSequence[]{"Consultation"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

        return builder.create();
    }

}
