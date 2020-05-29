package com.tolaotesanya.healthpad.modellayer.model;

public class ChatConversation {

    private boolean seen;
    private long timestamp;

    public ChatConversation() {
    }

    public ChatConversation(boolean seen, long timestamp) {
        this.seen = seen;
        this.timestamp = timestamp;
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
}
