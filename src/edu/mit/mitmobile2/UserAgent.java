package edu.mit.mitmobile2;

import android.os.Build;
import edu.mit.mitmobile2.about.BuildSettings;

public class UserAgent {
	
	public static String get() {
		return "MIT Mobile/" + BuildSettings.VERSION_NAME + 
			" (" + BuildSettings.BUILD_GIT_DESCRIBE + ";)" +
			" Android/" + Build.VERSION.RELEASE + 
				" (" + Build.CPU_ABI  + "; " +  Build.MANUFACTURER + " " + Build.MODEL + ";)";
	}
}
