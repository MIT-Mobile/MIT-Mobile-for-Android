package edu.mit.mitmobile2;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
    public static SharedPreferences getDefaultSharedPreferencesMultiProcess(
            Context context) {
        return context.getSharedPreferences(
                Constants.SHARED_PREFS_KEY,
                Context.MODE_MULTI_PROCESS);
    }
}
