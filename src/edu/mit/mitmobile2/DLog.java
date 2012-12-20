package edu.mit.mitmobile2;

import java.util.Date;

import android.util.Log;
import edu.mit.mitmobile2.about.Config;

public class DLog {
	
	long mLastTime = -1;
	String mTag;
	
	public DLog(String tag) {
		mTag = tag; 
	}

	public void out(String text) {
		Date date = new Date();
		long currentTime = date.getTime();
		String msg = date.toLocaleString() + ": " + text;
		
		if(mLastTime > 0) {
			long delta = date.getTime() - mLastTime;
			msg += " delta=" + Long.toString(delta);
		}
		
		mLastTime = currentTime;
		
		Log.d(mTag, msg);
	}
	
	public static void v(String tag, String msg) {
		if(Config.VERBOSE_LOGGING) {
			Log.v(tag, msg);
		}
	}
}
