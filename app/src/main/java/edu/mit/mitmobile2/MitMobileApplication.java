package edu.mit.mitmobile2;

import android.app.Application;

import timber.log.Timber;

public class MitMobileApplication extends Application {

    public static DBAdapter dbAdapter;

    @Override
    public void onCreate() {
        super.onCreate();

        dbAdapter = new DBAdapter(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
