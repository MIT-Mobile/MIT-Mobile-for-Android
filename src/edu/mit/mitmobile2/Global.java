package edu.mit.mitmobile2;

import java.util.ArrayList;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import edu.mit.mitmobile2.about.BuildSettings;
import edu.mit.mitmobile2.objs.CourseItem;
import edu.mit.mitmobile2.objs.EventDetailsItem;
import edu.mit.mitmobile2.objs.MapCatItem;
import edu.mit.mitmobile2.objs.NewsItem;

public class Global extends Application {

	public static final boolean DEBUG = false;

	// Shared preferences MUST use separate entries (docs say otherwise but seen failures to commit edits)
	public static final String PREFS = "prefs";
	public static final String PREFS_SHUTTLES = "prefs_shuttles";
	public static final String PREFS_STELLAR = "prefs_stellar";
	public static final String PREFS_MAP = "prefs_map";
	
	public static final String PREF_KEY_STOPS = "pref_stops";
	public static final String PREF_KEY_EMERGENCY_VERSION = "pref_version";
	
	public static EventDetailsItem curEvent;
	public static CourseItem curCourse;
	public static NewsItem curNews;
	
	private static final String TAG = "Global";
	public static final String MIT_MOBILE_SERVER_KEY = "mit_mobile_server"; // key for server variable in the preferences file
	public static final String DEFAULT_MIT_MOBILE_SERVER = BuildSettings.MOBILE_WEB_DOMAIN; // key for server variable in the preferences file
	private static String mobileWebDomain = DEFAULT_MIT_MOBILE_SERVER;

	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG,"onCreate()");
		// load Mobile Web Domain preferences
		try {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			Global.setMobileWebDomain(prefs.getString(Global.MIT_MOBILE_SERVER_KEY, null));
		}
		catch (RuntimeException e) {
			Log.d(TAG,"error getting prefs: " + e.getMessage() + "\n" + e.getStackTrace());
		}
		// if the mobile server is not defined in the prefernces, default it to the value in the BuildSettings.java file
		if (Global.getMobileWebDomain() == null) {
			Global.setMobileWebDomain(Global.DEFAULT_MIT_MOBILE_SERVER);
		}
		
		//Log.d(TAG,"mobile web domain = " + Global.getMobileWebDomain());
	}

	// Maps related:
	public static ArrayList<MapCatItem> curSubCats;

	public static String getMobileWebDomain() {
		Log.d(TAG,"mobileWebDomain from get = " + mobileWebDomain);
		return Global.mobileWebDomain;
	}
	
	public static void setMobileWebDomain(String mobileWebDomain) {
		Global.mobileWebDomain = mobileWebDomain;
	}
	
	
}
