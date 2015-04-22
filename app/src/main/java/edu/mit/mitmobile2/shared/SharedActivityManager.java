package edu.mit.mitmobile2.shared;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import edu.mit.mitmobile2.emergency.activity.EmergencyContactsActivity;

/**
 * Internal Shared Activity Manager.
 */
public final class SharedActivityManager extends SharedIntentManager {
    public static Intent createEmergencyContactsIntent(@NonNull Context packageContext) {
        Intent intent = new Intent(packageContext, EmergencyContactsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }
}
