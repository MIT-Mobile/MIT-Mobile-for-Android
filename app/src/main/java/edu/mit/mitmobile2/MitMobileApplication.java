package edu.mit.mitmobile2;

import android.app.Application;

import timber.log.Timber;

public class MitMobileApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
