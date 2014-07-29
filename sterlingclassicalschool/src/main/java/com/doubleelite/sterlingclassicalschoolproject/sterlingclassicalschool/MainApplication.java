package com.doubleelite.sterlingclassicalschoolproject.sterlingclassicalschool;

import android.app.Application;
import android.provider.Settings;
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

        // Set the icon in the status bar and the activity to launch when you tap the notification.
        PushService.setDefaultPushCallback(this, MainActivity.class, R.drawable.ic_notification_sterling);

        // Get the Android_ID of this device
        String  android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("LOG","android id >>" + android_id);

        // Add the id to the installation, this should help prevent multiple notifications if the user uninstalls
        // and reinstalls the app.
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("UniqueId", android_id);

        // If the user doesn’t have a network connection, the object will be stored
        // safely on the device until a new connection has been established.
        // If the app is closed before the connection is back, Parse will try to
        // save it again the next time the app is opened.
        installation.saveEventually();
    }
}
