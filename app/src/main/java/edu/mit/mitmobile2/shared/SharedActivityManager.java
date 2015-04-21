package edu.mit.mitmobile2.shared;

import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import edu.mit.mitmobile2.emergency.activity.EmergencyContactsActivity;

/**
 * Created by grmartin on 4/16/15.
 */
public final class SharedActivityManager {
    private static Context context;

    public static Intent createTelephoneCallIntent(@NonNull Context packageContext, int stringId) {
        return createTelephoneCallIntent(packageContext.getString(stringId));
    }

    public static Intent createTelephoneCallIntent(@NonNull String number) {
        return new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
    }

    public static Intent createEmergencyContactsIntent(@NonNull Context packageContext) {
        Intent intent = new Intent(packageContext, EmergencyContactsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
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

        List list = context.getPackageManager().queryIntentActivities(intent, 0);

        if (list != null && list.size() > 0) return intent;

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

    public static void setContext(Context ctx) {
        if (context == null && ctx != null)
            context = ctx;
    }
}
