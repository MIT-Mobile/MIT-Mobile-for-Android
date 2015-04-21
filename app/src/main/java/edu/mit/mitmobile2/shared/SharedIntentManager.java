package edu.mit.mitmobile2.shared;

import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Shared intent manager, this is often used for External intends but may also be used for partitioned data handling
 * internally in the future. For internal activity calling {@see SharedActivityManager}
 */
public class SharedIntentManager {
    private static Context context;

    public static void setContext(Context ctx) {
        if (context == null && ctx != null)
            context = ctx;
    }

    public static boolean canHandleIntent(@NonNull Intent intent) {
        List list = context.getPackageManager().queryIntentActivities(intent, 0);
        return (list != null && list.size() > 0);
    }

    public static Intent createTelephoneCallIntent(@NonNull Context packageContext, int stringId) {
        return createTelephoneCallIntent(packageContext.getString(stringId));
    }

    public static Intent createTelephoneCallIntent(@NonNull String number) {
        return new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
    }

    public static Intent createSendEmailIntent(String value) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, value);
        return intent;
    }

    public static Intent createMapIntent(String address) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.fromParts("geo", "0,0?q="+Uri.encode(address), ""));

        if (canHandleIntent(intent)) return intent;

        intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://maps.google.com/maps?q="+Uri.encode(address)));
        return intent;
    }

    public static Intent createBrowserIntent(String url) {
        Intent intent = null;
        if (url.startsWith("http://") || url.startsWith("https://")) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
        } else {
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, url);
        }
        return intent;
    }

}
