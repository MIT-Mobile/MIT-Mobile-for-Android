package edu.mit.mitmobile2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import edu.mit.mitmobile2.about.Config;
import edu.mit.mitmobile2.alerts.C2DMReceiver;
import edu.mit.mitmobile2.objs.EventDetailsItem;
import edu.mit.mitmobile2.objs.MapCatItem;
import edu.mit.mitmobile2.objs.NewsItem;

public class Global extends Application {

	public static final boolean DEBUG = false;
	public static Context mContext;
	
	// Shared preferences MUST use separate entries (docs say otherwise but seen failures to commit edits)
	public static final String PREFS = "prefs";
	public static final String PREFS_SHUTTLES = "prefs_shuttles";
	public static final String PREFS_MAP = "prefs_map";
	
	public static final String PREF_KEY_STOPS = "pref_stops";
	public static final String PREF_KEY_EMERGENCY_VERSION = "pref_version";
	
	public static EventDetailsItem curEvent;
	public static NewsItem curNews;

	// Mobile Server 
	private static final String TAG = "Global";
	public static final String MIT_MOBILE_SERVER_KEY = "mit_mobile_server"; // key for server variable in the preferences file
	public static final String DEFAULT_MIT_MOBILE_SERVER = Config.MOBILE_WEB_DOMAIN; // key for server variable in the preferences file
	private static String mobileWebDomain = DEFAULT_MIT_MOBILE_SERVER;

	// Shared Data
	public static SharedPreferences prefs;
	public static SharedData sharedData = new SharedData();
	
	// Facilities 
	private static String problemType;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.d(TAG,"onCreate()");
		mContext = this;
		
		C2DMReceiver.registerForNotifications(this);
		
		// load Mobile Web Domain preferences
		try {
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
			Global.setMobileWebDomain(prefs.getString(Global.MIT_MOBILE_SERVER_KEY, null));
		}
		catch (RuntimeException e) {
			Log.d(TAG,"error getting prefs: " + e.getMessage() + "\n" + e.getStackTrace());
		}
		// if the mobile server is not defined in the preferences, default it to the value in the Config.java file
		if (Global.getMobileWebDomain() == null) {
			Global.setMobileWebDomain(Global.DEFAULT_MIT_MOBILE_SERVER);
		}
		
		Handler uiHandler = new Handler();

		// Read in version information for data files on mobile server
		// this version info is used to determine if the local database is out of date with the server and needs to be updated
		Global.getVersionInfo(mContext, uiHandler);

	}

	// Maps related:
	public static ArrayList<MapCatItem> curSubCats;

	public static String getMobileWebDomain() {
		Log.d(TAG,"mobileWebDomain from get = " + mobileWebDomain);
		return Global.mobileWebDomain;
	}
	
	public static void setMobileWebDomain(String mobileWebDomain) {
		Global.mobileWebDomain = mobileWebDomain;
		C2DMReceiver.clearDeviceRegistration(mContext);
		C2DMReceiver.registerForNotifications(mContext);
	}
	
	// Facilities Related
	public static void setProblemType(String problemType) {
		Global.problemType = problemType;
		Log.d(TAG,problemType + " selected");
	}

	public static String getProblemType() {
		return Global.problemType;
	}
	
	public static class URLReader {
	    public static String get(String urlString) throws Exception {
	    	String contents = "";
	    	Log.d(TAG,"urlString = " + urlString);
	    	URL url = new URL(urlString);
	    	BufferedReader in = new BufferedReader(
						new InputStreamReader(url.openStream()));
	    	String inputLine;

	    	while ((inputLine = in.readLine()) != null)
	    		contents += inputLine;

	    	in.close();
	    	return contents;
	    }
	}

	
	public static void getVersionInfo(final Context mContext,final Handler uiHandler) {
		// uses the version api to get a json string of all databases and their version numbers and returns them as a shared preference string
		// these values can be used to determine if the local copy of the database is out of date and needs to be updated
    	Log.d(TAG,"getVersionInfo()");

    	MobileWebApi api = new MobileWebApi(false, true, "Version", mContext, uiHandler);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("module", "version");
		params.put("command", "list");
		Date date = new Date();
		params.put("key", date.getTime() + "");
		Log.d(TAG,"before request json");
		
		api.requestJSONObject(params, new MobileWebApi.JSONObjectResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler),
                new MobileWebApi.DefaultCancelRequestListener(uiHandler)) {
			@Override
			public void onResponse(JSONObject obj) {
				String module;
				String key;
				String versionKey; // contenation of the module and key strings, e.g. facilities_room
				String version;
			    
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
				SharedPreferences.Editor prefsEditor = prefs.edit();
				try {
					@SuppressWarnings("unchecked")
					Iterator<Object> m =  obj.keys();
					while (m.hasNext()) {

						module = (String)m.next();
						Log.d(TAG,"module = " + module);
	
						JSONObject data = (JSONObject) obj.get(module);
						@SuppressWarnings("unchecked")
						Iterator<Object> d =  data.keys();
						while (d.hasNext()) {
							key = (String) d.next();
							versionKey = "remote_" + module + "_" + key;
							version = (String)data.getString(key);
							Log.d(TAG,"key = " + key);
							Log.d(TAG,"version = " + version);
							prefsEditor.putString(versionKey, version);
						}
					}
				}
				catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				prefsEditor.commit();
				MobileWebApi.sendSuccessMessage(uiHandler);
			}
			
			@SuppressWarnings("unused")
			public void onError(JSONObject obj) {
				Log.d(TAG,"error");				
			}
		});
    	
	}
	
	public static boolean upToDate(String module, String key) {
		// compares the version of the local data against the version on the mobile server.
		// returns true if the local version is greater than or equal to the server version

		Integer remoteVersion = Global.getVersion("remote",module,key);
		Integer localVersion = Global.getVersion("local",module,key);

		return (localVersion >= remoteVersion);
	}
		
	public static Integer getVersion(String type, String module,String key) {
		// returns version information for specified module, key and type
		// version keys are in the form of <type>_<module>_<key>
		// type can be "local" or "remote"
		String versionKey = type + "_" + module + "_" + key;
		int version = Integer.parseInt(Global.prefs.getString(versionKey, "0"));
		Log.d(TAG,"version for " + module + " " + key + " " + version);
		return version;
	}
		
	public static void setVersion(String type, String module,String key, String value, Context mContext) {
		// sets the version information for specified module, key and type
		// version keys are in the form of <type>_<module>_<key>
		// type can be "local" or "remote"
		try {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
			SharedPreferences.Editor prefsEditor = prefs.edit();
			String versionKey = type + "_" + module + "_" + key;
			prefsEditor.putString(versionKey, value);
			prefsEditor.commit();
		}
		catch (Exception e) {
			Log.d(TAG,"exception for module " + module + " key " + key + " = " + e.getMessage()+ " " + e.getStackTrace() + e.getLocalizedMessage());
		}
	}

	public static void onDeviceRegisterCompleted() {
		
	}
}
