package com.tolaotesanya.healthpad.helper;

import android.app.Application;
import android.content.Context;

public class GetTime extends Application {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTime(long time, Context context){
        if(time < 1000000000000L){
            time *= 1000;
        }
        long now = System.currentTimeMillis();
        if(time > now || time <= 0){
            return null;
        }

        //Localise
        final long diff = now - time;
        if(diff < MINUTE_MILLIS){
            return "now";
        } else if(diff < 2 * MINUTE_MILLIS) {
            return "a min ago";
        } else if(diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " min";
        } else if(diff < 90 * MINUTE_MILLIS) {
            return "1 hour";
        } else if(diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours";
        } else if(diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days";
        }

    }
}
