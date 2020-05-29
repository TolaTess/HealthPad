package com.tolaotesanya.healthpad.fragment.chat;

import com.google.firebase.database.DatabaseReference;

public interface ChatPresenter {

    DatabaseReference getmConvDatabase();
    DatabaseReference getmUserChatDatabase();
    DatabaseReference getmMessageDatabase();
}
