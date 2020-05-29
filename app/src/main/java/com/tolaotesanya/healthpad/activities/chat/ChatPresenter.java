package com.tolaotesanya.healthpad.activities.chat;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public interface ChatPresenter {

    void createChatDatabase();
    void checkLastSeenOnline();
    void loadMessages();
    void sendMessages();
    void loadMoreMessages();
    /*SwipeRefreshLayout getmRefreshLayout();
    int getmCurrentPage();
    int getItemPos();*/

}
