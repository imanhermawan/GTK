package com.example.iman.gtk.models;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Iman on 01/03/2018.
 */

public class ChatModel {
    private String mName;
    private String mMessage;
    private long userId;
    private long timestamp;
    private String formattedTime;

    public ChatModel() {

        mName = "";
        mMessage = "";
        userId = 2;
        timestamp = 0;


    }

    public ChatModel(String mMessage, String mName, long uid,  long time, String formattedTime) {
        this.mName = mName;
        this.mMessage = mMessage;
        this.userId = uid;
        this.timestamp = time;
        this.formattedTime = formattedTime;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmName() {
        return mName;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTime(long time) {
        this.timestamp = time;

        long oneDayInMillis = 24 * 60 * 60 * 1000;
        long timeDifference = System.currentTimeMillis() - time;

        if(timeDifference < oneDayInMillis){
            formattedTime = DateFormat.format("hh:mm a", time).toString();
        }else{
            formattedTime = DateFormat.format("dd MMM - hh:mm a", time).toString();
        }
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public String getFormattedTime(){
        long timeDifference = System.currentTimeMillis() - timestamp;

        if (timeDifference > 1000 * 60 * 60 * 24) {
            Date date = new Date();
            date.setTime(timestamp);
            return new SimpleDateFormat("dd MMM - hh:mm a", Locale.getDefault()).format(date);
        } else if (timeDifference > 1000 * 60 * 60) {
            return Long.toString(timeDifference / (1000 * 60 * 60)) + " hours ago";
        } else if (timeDifference > 1000 * 60) {
            return Long.toString(timeDifference / (1000 * 60)) + " minutes ago";
        } else {
            return "seconds ago";
        }
    }
}
