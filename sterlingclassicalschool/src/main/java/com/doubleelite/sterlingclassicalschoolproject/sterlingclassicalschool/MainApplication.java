package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

/**
 * This class is only here because in order to initialize parse you must do it before any receivers try and start.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("APP", "Application - Initialize Parse");
        // Setup Parse for push notifications
        Parse.initialize(this, "dbgMLg4QssgeHn6ogl9qcorJ74IYWBO29d731nqn", "QUNUh1yKcSBfIko45FDOVvPid7oakpmZzllNsnkF");
        // Set the icon in the status bar
        PushService.setDefaultPushCallback(this, MainActivity.class, R.drawable.ic_notification_sterling);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
