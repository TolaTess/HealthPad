package com.tolaotesanya.healthpad.activities.chat;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public interface ChatActivityPresenter {

    void createChatDatabase();
    void checkLastSeenOnline();
    void loadMessages();
    void sendMessages();
    void loadMoreMessages();
    void attachUI();
    /*SwipeRefreshLayout getmRefreshLayout();
    int getmCurrentPage();
    int getItemPos();*/

}
