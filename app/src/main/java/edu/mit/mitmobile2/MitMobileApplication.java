package edu.mit.mitmobile2;

import android.accounts.Account;
import android.app.Application;

import timber.log.Timber;

public class MitMobileApplication extends Application {

    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "edu.mit.mitmobile2.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "m.mit.edu";
    // The account name
    public static final String ACCOUNT = "mitaccount";

    public static final int INTERVAL_SECS = 1200;
    // Instance fields
    public static Account mAccount;

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
