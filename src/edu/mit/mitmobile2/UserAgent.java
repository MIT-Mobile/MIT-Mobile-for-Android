package edu.mit.mitmobile2;

import android.os.Build;
import edu.mit.mitmobile2.about.BuildSettings;

public class UserAgent {
	
	public static String get() {
		return "MIT Mobile " + BuildSettings.VERSION_NAME + " for Android " + Build.VERSION.RELEASE + " (CPU: " + Build.CPU_ABI + " - Device: " + Build.MANUFACTURER + " " + Build.MODEL + ")";
	}
}
