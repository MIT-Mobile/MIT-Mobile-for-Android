package edu.mit.mitmobile2.touchstone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.jsoup.nodes.Document;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MITClient;
import edu.mit.mitmobile2.MITClientData;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.libraries.LibraryModel;
import edu.mit.mitmobile2.libraries.LibraryModel.UserIdentity;

public class TouchstoneActivity extends ModuleActivity implements OnSharedPreferenceChangeListener {
	
	private Context mContext;	

	SharedPreferences pref;
	String user;
	String password;
	Document document;
	EditText touchstoneUsername;
	EditText touchstonePassword;
	//Button cancelButton;
	Button loginButton;
	CheckBox rememberLoginCB; 
	TextView mError;
    private LinearLayout touchstoneContents;
	private FullScreenLoader touchstoneLoadingView;

    
	AlertDialog alert;
	public static SharedPreferences prefs;
	public static final String TAG = "TouchstoneActivity";
	private static final int MENU_INFO = 0;
	private static final int MENU_PREFS = 1;
	
	Bundle extras;
	
	/**
	 * @throws IOException 
	 * @throws ClientProtocolException **************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate()");
		super.onCreate(savedInstanceState);
		mContext = this;
        Handler uiHandler = new Handler();

        createViews(); 
	}
		
	private void createViews() {
		Log.d(TAG,"createViews()");
		setContentView(R.layout.touchstone_login);

		extras = getIntent().getExtras();
		String key = "";
		key = (String)extras.getString(key);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		final SharedPreferences.Editor prefsEditor = prefs.edit();

		touchstoneUsername = (EditText)findViewById(R.id.touchstoneUsername);
		touchstonePassword = (EditText)findViewById(R.id.touchstonePassword);

		// load existing pref values
		touchstoneUsername.setText(prefs.getString("PREF_TOUCHSTONE_USERNAME", ""));
		touchstonePassword.setText(prefs.getString("PREF_TOUCHSTONE_PASSWORD", ""));

		//cancelButton = (Button)findViewById(R.id.touchstoneCancelButton);
		loginButton = (Button)findViewById(R.id.touchstoneLoginButton);
		rememberLoginCB =(CheckBox)findViewById(R.id.rememberLoginCB);

	    touchstoneLoadingView = (FullScreenLoader)findViewById(R.id.touchstoneLoadingView);
	    mError = (TextView)touchstoneLoadingView.findViewById(R.id.fullScreenLoadingErrorTV); 
	    touchstoneContents = (LinearLayout)findViewById(R.id.touchstoneContents);
	    
	    loginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					Log.d(TAG,"username = " + touchstoneUsername.getEditableText().toString());
					Log.d(TAG,"password = " + touchstonePassword.getEditableText().toString());
					prefsEditor.putString("PREF_TOUCHSTONE_USERNAME", touchstoneUsername.getEditableText().toString());
					prefsEditor.putString("PREF_TOUCHSTONE_PASSWORD", touchstonePassword.getEditableText().toString());
					prefsEditor.putBoolean("PREF_TOUCHSTONE_REMEMBER_LOGIN", rememberLoginCB.isChecked());
					prefsEditor.commit();
				}
				catch (RuntimeException e) {
					Log.d(TAG,"error getting prefs: " + e.getMessage() + "\n" + e.getStackTrace());
				}
				
				String requestKey = extras.getString("requestKey");
				//MITClient.requestMap.put(requestKey, MITClient.TOUCHSTONE_LOGIN);
				MITClientData clientData = (MITClientData)MITClient.requestMap.get(requestKey);
				clientData.setTouchstoneState(MITClient.TOUCHSTONE_LOGIN);
				finish();
			}
		});
						
		// Dialog for invalid username or password
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Please enter a valid username and password")
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                Log.d(TAG,"finish");
		           }
		       });
		alert = builder.create();
		// End Create Dialog
		
		// display the alert dialog if in the auth_error_state
		String touchstoneState = extras.getString("touchstoneState");
		Log.d(TAG,"touchstoneState = " + touchstoneState);
		if (touchstoneState != null && touchstoneState.equalsIgnoreCase(MITClient.AUTH_ERROR_STATE)) {
			alert.show();
		}
		
	}
	
    private Handler loginUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Log.d(TAG,"handleMessage");
			touchstoneContents.setVisibility(View.VISIBLE);
        	touchstoneLoadingView.setVisibility(View.GONE);

            if (msg.arg1 == MobileWebApi.SUCCESS) {
            	Log.d(TAG,"MobileWebApi success");
                @SuppressWarnings("unchecked")
            	UserIdentity identity = (UserIdentity)msg.obj;
                Log.d(TAG,"identity = " + identity.getUsername());
            } 
            else if (msg.arg1 == MobileWebApi.ERROR) {
            	Log.d(TAG,"show login error");
            	mError.setText("Error logging into Touchstone");
            	touchstoneLoadingView.showError();
            } 
            else if (msg.arg1 == MobileWebApi.CANCELLED) {
            	touchstoneLoadingView.showError();
            }
        }
    };
 
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
		  .setIcon(R.drawable.menu_about);
		
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
	}

	
	@Override
	public void onBackPressed() {
		String requestKey = extras.getString("requestKey");
		((MITClientData)MITClient.requestMap.get(requestKey)).setTouchstoneState(MITClient.TOUCHSTONE_CANCEL);
		finish();
	}

}
