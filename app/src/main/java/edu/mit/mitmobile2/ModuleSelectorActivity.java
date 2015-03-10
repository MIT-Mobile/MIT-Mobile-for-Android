/*
 * This  activity is parses all intents with scheme "mitmobile2" and launches the appropriate activity
 * THe URI format is mitmobile2://<module name>/key1/value1 ... key n/value n
 */
package edu.mit.mitmobile2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

public class ModuleSelectorActivity extends Activity {

    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "edu.mit.mitmobile2.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "m.mit.edu";
    // The account name
    public static final String ACCOUNT = "mitdummyaccount";

    public static final int INTERVAL_SECS = 10;
    // Instance fields
    private Account mAccount;
    // A content resolver for accessing the provider
    ContentResolver mResolver;

	private String module; // name of module
	private static final String DEFAULT_MODULE = "news";
	private String[] params;
	private NavItem navItem;
	private Context mContext;
	public static List<NavItem> navigationTitles = new ArrayList<NavItem>();
    public  static Map<String, NavItem> navMap = null;
    public static Map<String,String> moduleMap = null;
    private NavItem mNavItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setTheme(R.style.Theme_MyTheme);
		loadNavigation(mContext);
        MITAPIClient.init(mContext);

        mAccount = createSyncAccount(this);
        mResolver = getContentResolver();
        ContentResolver.addPeriodicSync(mAccount, AUTHORITY, Bundle.EMPTY, INTERVAL_SECS);
	}

	@Override
	protected void onStart() {
		super.onStart();

		Intent intent = getIntent();
		Log.d("ZZZ", "Intent: " + intent.getDataString());
		Log.d("ZZZ", "Scheme: " + intent.getScheme());

        //If the intent has come from a URL
        //Could swap activities w/fragments, make the call to show fragment in here
		if (intent.getScheme() != null && intent.getScheme().equals("mitmobile2")) {
			String intentString = intent.getDataString();
			String[] data = intentString.split("://");
			if (data != null && data.length == 2) {
				params = data[1].split("/");
				this.module = params[0];
				Log.d("ZZZ","module = " + this.module);
				// Get the long_name of the module
			}
		}
		else {
            //If it's coming from app startup
			this.module = ModuleSelectorActivity.DEFAULT_MODULE;
		}

		String long_name = ModuleSelectorActivity.moduleMap.get(this.module);

		// use the long_name to get the navItem object
		navItem = ModuleSelectorActivity.navMap.get(long_name);
		Log.d("ZZZ","navItem = " + navItem.toString());
		if (navItem != null) {
	    	Class<?> c = null;
	    	if(navItem.getIntent() != null) {
	    	    try {
	    	        c = Class.forName(navItem.getIntent());
	    	    } catch (ClassNotFoundException e) {
	    	    	Log.d("ZZZ","CLASS NOT FOUND " + e.getMessage());
	    	        e.printStackTrace();
	    	    }
		    	Intent i = new Intent(mContext,c);
		    	Bundle extras = new Bundle();
		    	extras.putString("long_name", long_name);
		    	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
		    	i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		    	Log.d("ZZZ","starting activity " + navItem.getLong_name());
				startActivity(i);
	    	 }
		}

	}

    public static Account createSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

    private void  loadNavigation(Context mContext) {
    	Resources resources = mContext.getResources();
        JSONObject navigation;
    	if (ModuleSelectorActivity.navMap == null) {
    		ModuleSelectorActivity.navMap = new HashMap<String, NavItem>(); // this maps long_name to navItem, since the long_name is stored in the nav drawer
    		ModuleSelectorActivity.moduleMap = new HashMap<String, String>(); // this maps module name to long_name for resolving intents

	    	String json = null;

	        try {
	        	InputStream is = getAssets().open("navigation.json");
	        	int size = is.available();
	        	byte[] buffer = new byte[size];
	        	is.read(buffer);
	        	is.close();
	        	json = new String(buffer, "UTF-8");
	        	Log.d("ZZZ",json);
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	        try {
	        	navigation = new JSONObject(json);

	        	Iterator n = navigation.keys();
	        	List<String> keysList = new ArrayList<String>();
	              while (n.hasNext()) {
	              	try {
	              		String key = (String)n.next();
	              		JSONObject module = navigation.getJSONObject(key);
	              		String long_name = module.getString("long_name");
	              		Log.d("ZZZ",long_name);
	              		keysList.add(long_name);
	              		NavItem navItem = new NavItem();
	              		navItem.setLong_name(long_name);
	              		navItem.setShort_name(module.getString("short_name"));

	              	   	// Get Home Icon
	              		int resourceId = resources.getIdentifier(module.getString("home_icon"), "drawable", mContext.getPackageName());
	              		navItem.setHome_icon(resourceId);

	              		// Get Menu Icon
	              		resourceId = resources.getIdentifier(module.getString("menu_icon"), "drawable", mContext.getPackageName());
	              		navItem.setMenu_icon(resourceId);

	              		navItem.setIntent(module.getString("intent"));
	              		navItem.setUrl(module.getString("url"));

	              		ModuleSelectorActivity.navMap.put(long_name, navItem);
	              		ModuleSelectorActivity.moduleMap.put(key,long_name);
	              		ModuleSelectorActivity.navigationTitles.add(navItem);

	              	}
	              	catch (JSONException e) {
	    	        	Log.d("ZZZ",e.getMessage().toString());
	              	}
	              }


	        }
	        catch (Exception e) {
	        	Log.d("ZZZ",e.getMessage().toString());
	        }
    	}
    }

}
