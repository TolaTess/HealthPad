package com.tolaotesanya.healthpad.fragment.chat;

import com.google.firebase.database.DatabaseReference;
import com.tolaotesanya.healthpad.coordinator.IntentPresenter;

public interface ChatPresenter {

    DatabaseReference getmConsultDatabase();
    DatabaseReference getmUserChatDatabase();
    DatabaseReference getmMessageDatabase();
    IntentPresenter getIntentPresenter();
}
