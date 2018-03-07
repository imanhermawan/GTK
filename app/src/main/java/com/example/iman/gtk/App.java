package com.example.iman.gtk;

import android.app.Application;

/**
 * Created by Iman on 28/02/2018.
 */

public class App extends Application {
    private static boolean activityVisible;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }
}
