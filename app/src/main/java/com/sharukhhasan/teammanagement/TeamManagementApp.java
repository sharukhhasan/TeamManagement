package com.sharukhhasan.teammanagement;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by sharukhhasan on 6/7/16.
 */
public class TeamManagementApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

        // TODO: enable Firebase debug if needed. In logcat select: 1) Debug , 2) filter with "Raising /"
        //Firebase.getDefaultConfig().setLogLevel(Logger.Level.DEBUG);
    }
}
