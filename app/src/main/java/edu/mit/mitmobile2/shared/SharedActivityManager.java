package edu.mit.mitmobile2.shared;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import edu.mit.mitmobile2.emergency.activity.EmergencyContactsActivity;

/**
 * Created by grmartin on 4/16/15.
 */
public final class SharedActivityManager {

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
}
