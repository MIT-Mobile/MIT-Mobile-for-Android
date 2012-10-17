package edu.mit.mitmobile2.libraries;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.mitmobile2.MITClient;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.MobileWebApi.HttpClientType;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.touchstone.TouchstonePrefsActivity;

//public class FacilitiesActivity extends ModuleActivity implements OnClickListener {
public class LibrariesActivity extends ModuleActivity implements OnSharedPreferenceChangeListener {
	

	public static final String OK_STATE = "ok";
	public static final String WAYF_STATE = "wayf";
	public static final String IDP_STATE = "idp";
	public static final String AUTH_STATE = "auth";

	private Context mContext;	

	TextView emergencyContactsTV;

	SharedPreferences pref;
	String user;
	String password;
	MITClient mitClient;
	HttpGet get;
	HttpResponse response;
	HttpEntity responseEntity;
	String responseString = "";
	String state = OK_STATE;
	HttpPost post;
	WebView webview;
	Document document;

	URI uri = null;
	String uriString = "";
	public static String targetUrl = "https://mobile-dev.mit.edu/api/index.php";
	URI targetUri;
	public static SharedPreferences prefs;
	public static final String TAG = "TouchstoneActivity";
	private static final int MENU_INFO = 0;
	private static final int MENU_PREFS = 1;
	
	/**
	 * @throws IOException 
	 * @throws ClientProtocolException **************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate()");
		super.onCreate(savedInstanceState);
		mContext = this;
        Handler uiHandler = new Handler();
        
        mitClient = new MITClient(mContext);

        // Create local HTTP context
        HttpContext localContext = new BasicHttpContext();
        
        // get user name and password from preferences file
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		user = prefs.getString("PREF_TOUCHSTONE_USERNAME", null);
		password = prefs.getString("PREF_TOUCHSTONE_PASSWORD", null);

		// open the preferences if the username and password are not defined
        if (user == null || password == null) {
        	startActivity( new Intent(this, TouchstonePrefsActivity.class) );	
    		mitClient.setUser(prefs.getString("PREF_TOUCHSTONE_USERNAME", null));
    		mitClient.setPassword(prefs.getString("PREF_TOUCHSTONE_PASSWORD", null));
        }
        else {   
	        //responseString = mitClient.getResponse(targetUrl);

//        	MITClient client = null;
//        	HttpGet get = new HttpGet("https://mobile-dev.mit.edu/secure/api/index.php?module=libraries&command=loans");
//        	HttpResponse resp = client.getResponse(get);
//        	Log.d(TAG,responseContentToString(resp));
        	
        	//DEBUG
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("module", "libraries");
			params.put("command", "loans");

        	MobileWebApi api = new MobileWebApi(false, true, "Libraries", mContext, uiHandler,HttpClientType.MIT);
        	api.requestJSONObject(params, new MobileWebApi.JSONObjectResponseListener(
	                new MobileWebApi.DefaultErrorListener(uiHandler),
	                new MobileWebApi.DefaultCancelRequestListener(uiHandler)) {
				@Override
				public void onResponse(JSONObject obj) {

					Log.d(TAG,"on response");
					Log.d(TAG,"obj = " + obj);
					try {
						Log.d(TAG,"total = " + obj.getString("total"));
						Log.d(TAG,"start = " + obj.getString("start"));
						Log.d(TAG,"overdue = " + obj.getString("overdue"));
					}
					catch (JSONException e) {
						Log.d(TAG,"JSONException = " + e.getMessage());
					}
				}

				@Override
				public void onError() {
					// TODO Auto-generated method stub
					super.onError();
					Log.d(TAG,"on error: requestJSONObject");
				}
		});			

        	//	        	//DEBUG
        }
	}
		
	private void createViews() throws ClientProtocolException, IOException {
		Log.d(TAG,"createViews()");
		
	}
	
	@Override
	protected Module getModule() {
		return null;
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_INFO:
			//Intent intent = new Intent(mContext, FacilitiesInfoActivity.class);					
			//startActivity(intent);
			return true;
		case MENU_PREFS:
			//Intent intent = new Intent(mContext, FacilitiesInfoActivity.class);					
        	startActivity( new Intent(this, TouchstonePrefsActivity.class) );
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) { 
		menu.add(0, MENU_INFO, Menu.NONE, "Info")
		  .setIcon(R.drawable.menu_info);
		
		menu.add(1, MENU_PREFS, Menu.NONE, "Prefs")
		  .setIcon(R.drawable.main_repeat);

	}


	
	public static String responseContentToString(HttpResponse response) {
		try {
		InputStream inputStream = response.getEntity().getContent();
		ByteArrayOutputStream content = new ByteArrayOutputStream();
		// Read response into a buffered stream
		int readBytes = 0;
		byte[] sBuffer = new byte[512];
  		while ((readBytes = inputStream.read(sBuffer)) != -1) {
  			content.write(sBuffer, 0, readBytes);
  		}

  		// Return result from buffered stream
  		String dataAsString = new String(content.toByteArray());
  		return dataAsString;
		}
		catch (IOException e) {
			return null;
		}
	}
	
	public synchronized void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		Context mContext = this;
		Handler uiHandler = new Handler();
		if (key.equalsIgnoreCase("PREF_TOUCHSTONE_USERNAME")) {
			mitClient.setUser(prefs.getString("PREF_TOUCHSTONE_USERNAME", null));
		}
		
		if (key.equalsIgnoreCase("PREF_TOUCHSTONE_PASSWORD")) {
			mitClient.setPassword(prefs.getString("PREF_TOUCHSTONE_PASSWORD", null));
		}
		
		Toast.makeText(this, "user set to " + mitClient.getUser(), Toast.LENGTH_SHORT).show();
	}

}
