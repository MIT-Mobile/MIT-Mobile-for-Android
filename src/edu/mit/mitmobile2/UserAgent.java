package edu.mit.mitmobile2;

import android.os.Build;
import edu.mit.mitmobile2.about.Config;

public class UserAgent {
	
	public static String get() {
		return "MIT Mobile/" + Config.VERSION_NAME + 
			" (" + Config.BUILD_GIT_DESCRIBE + ";)" +
			" Android/" + Build.VERSION.RELEASE + 
				" (" + Build.CPU_ABI  + "; " +  Build.MANUFACTURER + " " + Build.MODEL + ";)";
	}
}
