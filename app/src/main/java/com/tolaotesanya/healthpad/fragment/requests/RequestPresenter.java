package com.tolaotesanya.healthpad.fragment.requests;

import com.google.firebase.database.DatabaseReference;

public interface RequestPresenter {

    DatabaseReference getmUserReqDatabase();
    DatabaseReference getmConsultReqDatabase();
}
