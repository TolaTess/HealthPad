package com.tolaotesanya.healthpad.modellayer.model;

public class ChatConversation {

    private boolean seen;
    private long timestamp;
    private String user_type;

    public ChatConversation() {
    }

    public ChatConversation(boolean seen, long timestamp, String user_type) {
        this.seen = seen;
        this.timestamp = timestamp;
        this.user_type = user_type;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }
}
