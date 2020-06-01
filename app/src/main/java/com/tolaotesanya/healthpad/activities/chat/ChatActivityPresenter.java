package com.tolaotesanya.healthpad.activities.chat;

import com.tolaotesanya.healthpad.modellayer.database.FirebasePresenter;

public interface ChatActivityPresenter {

    void createChatDatabase();
    void checkLastSeenOnline();
    void loadMessages();
    void sendMessages();
    void loadMoreMessages();
    void attachUI();
    FirebasePresenter getPresenter();
    /*SwipeRefreshLayout getmRefreshLayout();
    int getmCurrentPage();
    int getItemPos();*/

}
