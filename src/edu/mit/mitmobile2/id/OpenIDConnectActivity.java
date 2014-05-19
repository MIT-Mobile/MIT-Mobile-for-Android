package edu.mit.mitmobile2.id;

import java.util.UUID;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;

public class OpenIDConnectActivity extends NewModuleActivity implements
		OnSharedPreferenceChangeListener {

	
	public static final String TAG = OpenIDConnectActivity.class.getSimpleName();
	
	// TODO: these are all specific to the MIT server
	private String issuer = "https://oidc.mit.edu/";
	private String authorizationEndpoint = issuer + "/authorize";
	private String tokenEndpoint = issuer + "/token";
	
	private String clientId = "05a3f7f5-c6db-4b1b-aca6-cc2f3c163b78";
	
	private String state;
	
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	}

	@Override
	protected NewModule getNewModule() {
		return new OpenIDConnectModule();
	}

	@Override
	protected boolean isScrollable() {
		return true;
	}

	@Override
	protected void onOptionSelected(String optionId) {
	}

	@Override
	protected boolean isModuleHomeActivity() {
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		String idToken = prefs.getString("oidc_idtoken", "");
		String subjectVal = prefs.getString("oidc_subject", "");
		String usernameVal = prefs.getString("oidc_username", "");
		String emailVal = prefs.getString("oidc_email", "");
		
		setContentView(R.layout.openidconnect_home);

		Button loginButton = (Button)findViewById(R.id.openid_login);
		
		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				state = UUID.randomUUID().toString();
				
				// TODO: use a real URL builder
				String url = authorizationEndpoint;
				
				url = url + "?client_id=" + clientId;
				url = url + "&response_type=code";
				url = url + "&state=" + state;
				url = url + "&scope=openid+email+profile+address+phone";
				
				Log.d(TAG, "Loading authorization URL: " + url);
				
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(intent);
			}
		});
		
		Button logoutButton = (Button)findViewById(R.id.openid_logout);
		
		logoutButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor edit = prefs.edit();
				edit.putString("oidc_idtoken", "");
				edit.putString("oidc_subject", "");
				edit.putString("oidc_username", "");
				edit.putString("oidc_email", "");
				edit.commit();
			}
		});
		
		TextView subjectView = (TextView)findViewById(R.id.subjectVal);
		TextView usernameView = (TextView)findViewById(R.id.usernameVal);
		TextView emailView = (TextView)findViewById(R.id.emailVal);
		
		subjectView.setText(subjectVal);
		usernameView.setText(usernameVal);
		emailView.setText(emailVal);
		
		
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "onStart()");

		super.onStart();
		
		Intent intent = getIntent();
		Log.d(TAG, "Intent: " + intent.getDataString());
		
		if (intent.getScheme() != null && intent.getScheme() == "mitmobile") {
			// processing a callback URL of some type
			
			
			
		}
		
	}
	
	
	
	
}