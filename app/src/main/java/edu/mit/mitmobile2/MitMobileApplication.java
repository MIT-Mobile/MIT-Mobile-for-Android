package edu.mit.mitmobile2;

import android.accounts.Account;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import edu.mit.mitmobile2.shared.SharedIntentManager;
import edu.mit.mitmobile2.shared.logging.LoggingManager;
import edu.mit.mitmobile2.shared.logging.LoggingManager.Timber;
import edu.mit.mitmobile2.shared.runtime.DroidUtils;
import edu.mit.mitmobile2.shared.runtime.RuntimeUtils;

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

    private static DBAdapter dbAdapter;

    public static Bus bus;

    GlobalOttoListener listener;

    @Override
    public void onCreate() {
        super.onCreate();

        RuntimeUtils.addToRuntimePackages(DroidUtils.STANDARD_ANDROID_OS_PACKAGES);

        dbAdapter = new DBAdapter(this);
        bus = new Bus();
        listener = new GlobalOttoListener();

        SharedIntentManager.setContext(this);

        LoggingManager.setGlobalMinimumLogLevel(BuildConfig.DEBUG ? LoggingManager.VERBOSE : LoggingManager.ERROR);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private class GlobalOttoListener {
        public GlobalOttoListener() {
            bus.register(this);
        }

        @Subscribe
        public void onRetrofitError(OttoBusEvent.RetrofitFailureEvent event) {
            Timber.e("GLOBAL RETROFIT ERROR");
            String message = event.getError().getMessage();
            if (event.getError().isNetworkError()) {
                Toast.makeText(MitMobileApplication.this, "Network error, please check connection", Toast.LENGTH_SHORT).show();
            } else if (event.getError().getResponse() != null) {
                int status = event.getError().getResponse().getStatus();
                if (status == 500) {
                    Toast.makeText(MitMobileApplication.this, "Internal Server Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MitMobileApplication.this, "HTTP " + status + " Error", Toast.LENGTH_SHORT).show();
                }
            }
            if (!TextUtils.isEmpty(message)) {
                Timber.e(message);
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
