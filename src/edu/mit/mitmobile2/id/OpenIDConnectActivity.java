package edu.mit.mitmobile2.id;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;

public class OpenIDConnectActivity extends NewModuleActivity implements
		OnSharedPreferenceChangeListener {

	
	public static final String TAG = OpenIDConnectActivity.class.getSimpleName();
	
	// TODO: these are all specific to the MIT server
	private String issuer = "https://oidc.mit.edu/";
	private String authorizationEndpoint = issuer + "authorize";
	private String tokenEndpoint = issuer + "token";
	private String userInfoEndpoint = issuer + "userinfo";
	private String revocationEndpoint = issuer + "revoke";
	private String returnActivity;
	private Bundle extras;
	private Activity mActivity;
	private String clientId = "05a3f7f5-c6db-4b1b-aca6-cc2f3c163b78";
	
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
		
		mActivity = this;
		setContentView(R.layout.openidconnect_home);

		Button loginButton = (Button)findViewById(R.id.openid_login);
		
		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				// create a randomized State string and save it
				
				String state = UUID.randomUUID().toString();
				state += ":" + returnActivity;
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenIDConnectActivity.this);
				Editor edit = prefs.edit();
				edit.putString("oidc_state", state);
				edit.commit();

				
				// TODO: use a real URL builder
				String url = authorizationEndpoint;
				
				url = url + "?client_id=" + clientId;
				url = url + "&response_type=code";
				url = url + "&state=" + state;
				url = url + "&scope=openid+email+profile+address+phone+offline_access+techcash+libraries";
				
				Log.d(TAG, "Loading authorization URL: " + url);
				
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(intent);
			}
		});
		
		Button logoutButton = (Button)findViewById(R.id.openid_logout);
		
		logoutButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				revokeTokens();
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenIDConnectActivity.this);
				Editor edit = prefs.edit();
				edit.putString("oidc_accesstoken", "");
				edit.putString("oidc_refreshtoken", "");
				edit.putString("oidc_idtoken", "");
				edit.putString("oidc_subject", "");
				edit.putString("oidc_username", "");
				edit.putString("oidc_email", "");
				edit.commit();
				updateDisplay();
			}
		});
		
		Button profileButton = (Button)findViewById(R.id.openid_profile);
		
		profileButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loadProfile();
				updateDisplay();				
			}
		});
		
	}
	
	private void revokeTokens() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenIDConnectActivity.this);
		String idToken = getIdToken(prefs);
		String accessToken = getAccesstoken(prefs);
		String refreshToken = getRefreshToken(prefs);

		revokeSingleToken(idToken);
		revokeSingleToken(accessToken);
		revokeSingleToken(refreshToken);
		
	}
	
	private void revokeSingleToken(String tokenValue) {
		
		try {
		
			HttpClient client = new DefaultHttpClient();
			String url = revocationEndpoint;
			
			HttpPost post = new HttpPost(url);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("client_id", clientId));
			params.add(new BasicNameValuePair("token", tokenValue));
			post.setEntity(new UrlEncodedFormEntity(params));
			
			HttpResponse response = client.execute(post);
			
			StatusLine result = response.getStatusLine();
			
			Log.d(TAG, "Revoked token, result was: " + result.getStatusCode());

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getRefreshToken(SharedPreferences prefs) {
		return prefs.getString("oidc_refreshtoken", "");
	}

	private String getAccesstoken(SharedPreferences prefs) {
		return prefs.getString("oidc_accesstoken", "");
	}

	private String getIdToken(SharedPreferences prefs) {
		return prefs.getString("oidc_idtoken", "");
	}

	private void updateDisplay() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		String idToken = getIdToken(prefs);
		String accessToken = getAccesstoken(prefs);
		String refreshToken = getRefreshToken(prefs);
		
		idToken = checkExpiredToken(idToken);
		accessToken = checkExpiredToken(accessToken);
		refreshToken = checkExpiredToken(refreshToken);
		// save the checked values
		Editor edit = prefs.edit();
		edit.putString("oidc_accesstoken", accessToken);
		edit.putString("oidc_idtoken", idToken);
		edit.putString("oidc_refreshtoken", refreshToken);
		edit.commit();
		
		
		String subjectVal = prefs.getString("oidc_subject", "");
		String usernameVal = prefs.getString("oidc_username", "");
		String emailVal = prefs.getString("oidc_email", "");
		
		TextView subjectView = (TextView)findViewById(R.id.subjectVal);
		TextView usernameView = (TextView)findViewById(R.id.usernameVal);
		TextView emailView = (TextView)findViewById(R.id.emailVal);
		
		subjectView.setText(subjectVal);
		usernameView.setText(usernameVal);
		emailView.setText(emailVal);
		
		ToggleButton idToggle = (ToggleButton)findViewById(R.id.openid_idtoken);
		ToggleButton accessToggle = (ToggleButton)findViewById(R.id.openid_access);
		ToggleButton refreshToggle = (ToggleButton)findViewById(R.id.openid_refresh);
		
		if (idToken != null && !idToken.isEmpty()) {
			idToggle.setChecked(true);
		} else {
			idToggle.setChecked(false);
		}
		
		if (accessToken != null && !accessToken.isEmpty()) {
			accessToggle.setChecked(true);
		} else {
			accessToggle.setChecked(false);
		}
		
		if (refreshToken != null && !refreshToken.isEmpty()) {
			refreshToggle.setChecked(true);
		} else {
			refreshToggle.setChecked(false);
		}
		
	}

	private String checkExpiredToken(String token) {
		if (token == null || token.equals("")) {
			return "";
		}
		
		try {
			JWT jwt = JWTParser.parse(token);
			
			if (jwt.getJWTClaimsSet().getExpirationTime() != null) {
				Date now = new Date();
				if (jwt.getJWTClaimsSet().getExpirationTime().before(now)) {
					// token is expired!
					return "";					
				} else {
					return token;
				}
			} else {
				return token;
			}
			
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "onStart()");

		super.onStart();
		
		Intent intent = getIntent();
		Log.d(TAG, "Intent: " + intent.getDataString());
		Log.d(TAG, "Scheme: " + intent.getScheme());
		
		this.extras = this.getIntent().getExtras();		
		if (this.extras != null) {
			this.returnActivity = this.extras.get("returnActivity").toString();
		}
		Log.d(TAG,"returnActivity: " + returnActivity);
		
		//Log.d(TAG, "Data.Scheme: " + intent.getData().getScheme());		
		//Log.d(TAG, "Query: " + intent.getData().getQuery());
		
		if (intent.getScheme() != null && intent.getScheme().equals("mitmobile2")) {
			// processing a callback URL of some type			
			authorizationCallback(intent);
		}
		
		updateDisplay();
	}

	
	private void authorizationCallback(Intent intent) {
		Log.d(TAG,"returnActivity = " + this.returnActivity);
		String code = intent.getData().getQueryParameter("code");
		String state = intent.getData().getQueryParameter("state");
		
		Log.d(TAG, "Code: " + code);
		Log.d(TAG, "State: " + state);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenIDConnectActivity.this);
		String savedState = prefs.getString("oidc_state", "");
		
		// TODO: check "state" for consistency
		if (state.equals(savedState)) {
			Log.d(TAG, "States match!");
		} else {
			Log.d(TAG, "States don't match :(");
		}
		
		// Fetch a token
		HttpClient client = new DefaultHttpClient();
		String url = tokenEndpoint;
		
		try {
			
			HttpPost post = new HttpPost(url);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("client_id", clientId));
			//params.add(new BasicNameValuePair("client_secret", clientSecret));
			params.add(new BasicNameValuePair("grant_type", "authorization_code"));
			params.add(new BasicNameValuePair("code", code));
			post.setEntity(new UrlEncodedFormEntity(params));
			
			post.setHeader("Accept", "application/json");
			
			HttpResponse response = client.execute(post);
			
			StatusLine result = response.getStatusLine();
			
			if (result.getStatusCode() == HttpStatus.SC_OK) {
				// get the body, parse as JSON
				HttpEntity entity = response.getEntity();
				
				JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
				JSONObject obj = (JSONObject) parser.parse(entity.getContent());

				String idTokenTxt = JSONObjectUtils.getString(obj, "id_token");
				String accessToken = JSONObjectUtils.getString(obj, "access_token");
				String refreshToken = JSONObjectUtils.getString(obj, "refresh_token");

				Log.d(TAG, "Got ID Token: " + idTokenTxt);
				Log.d(TAG, "Got Access Token: " + accessToken);
				Log.d(TAG, "Got Refresh Token: " + refreshToken);
				
				// parse the ID Token
				JWT jwt = JWTParser.parse(idTokenTxt);
				String subject = jwt.getJWTClaimsSet().getStringClaim("sub");

				Log.d(TAG, "Loaded subject: " + subject);
				
				Editor edit = prefs.edit();
				edit.putString("oidc_accesstoken", accessToken);
				edit.putString("oidc_idtoken", idTokenTxt);
				edit.putString("oidc_refreshtoken", refreshToken);
				edit.putString("oidc_subject", subject);
				edit.commit();
				
			    // start returnActivity
			    this.returnActivity = (state.split(":"))[1];
			    try {
			    	Class<?> returnActivityClass = Class.forName(this.returnActivity);
					Intent i = new Intent(mActivity,returnActivityClass);
					startActivity(i);
				} catch (ClassNotFoundException e) {
					Log.d(TAG,"class not found for " + this.returnActivity);
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadProfile() {
		// Fetch a token
		HttpClient client = new DefaultHttpClient();
		String url = userInfoEndpoint;
		
		try {
			
			
			
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenIDConnectActivity.this);
			
			String accessToken = getAccesstoken(prefs);
			accessToken = checkExpiredToken(accessToken);
			
			if (accessToken == null || accessToken.isEmpty()) {
				// if we don't have an access token, try to refresh it
				refreshAccessToken();
				accessToken = getAccesstoken(prefs);
			
				if (accessToken == null || accessToken.isEmpty()) {
					// if we still don't have one, bail
					Log.d(TAG, "Couldn't load profile: no access token");
					updateDisplay();
					return;
				}
			}

			//url = url + "?access_token=" + accessToken;
			
			//Log.d(TAG, "Fetching profile: " + url);
			
			HttpGet get = new HttpGet(url);

			get.setHeader("Authorization", "Bearer " + accessToken);
			get.setHeader("Accept", "application/json");
			
			HttpResponse response = client.execute(get);
			
			StatusLine result = response.getStatusLine();
			
			if (result.getStatusCode() == HttpStatus.SC_OK) {
				// get the body, parse as JSON
				HttpEntity entity = response.getEntity();
				
				JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
				JSONObject obj = (JSONObject) parser.parse(entity.getContent());
				
				String username = JSONObjectUtils.getString(obj, "preferred_username");
				String email = JSONObjectUtils.getString(obj, "email");

				Log.d(TAG, "Username: " + username);
				Log.d(TAG, "Email: " + email);
				
				// parse the ID Token
				Editor edit = prefs.edit();
				edit.putString("oidc_username", username);
				edit.putString("oidc_email", email);
				edit.commit();
			} else {
				// something went wrong, chuck the token
				
				revokeSingleToken(accessToken);
				
				Editor edit = prefs.edit();
				edit.putString("oidc_accesstoken", "");
				edit.commit();				
				
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		updateDisplay();
	}

	private void refreshAccessToken() {
		// Fetch a token
		HttpClient client = new DefaultHttpClient();
		String url = tokenEndpoint;
		
		try {
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenIDConnectActivity.this);
			
			String refreshToken = getRefreshToken(prefs);
			
			HttpPost post = new HttpPost(url);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("client_id", clientId));
			//params.add(new BasicNameValuePair("client_secret", clientSecret));
			params.add(new BasicNameValuePair("grant_type", "refresh_token"));
			params.add(new BasicNameValuePair("refresh_token", refreshToken));
			post.setEntity(new UrlEncodedFormEntity(params));
			
			post.setHeader("Accept", "application/json");
			
			HttpResponse response = client.execute(post);
			
			StatusLine result = response.getStatusLine();
			
			if (result.getStatusCode() == HttpStatus.SC_OK) {
				// get the body, parse as JSON
				HttpEntity entity = response.getEntity();
				
				JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
				JSONObject obj = (JSONObject) parser.parse(entity.getContent());

				String idTokenTxt = JSONObjectUtils.getString(obj, "id_token");
				String accessToken = JSONObjectUtils.getString(obj, "access_token");
				refreshToken = JSONObjectUtils.getString(obj, "refresh_token");

				Log.d(TAG, "Got ID Token: " + idTokenTxt);
				Log.d(TAG, "Got Access Token: " + accessToken);
				Log.d(TAG, "Got Refresh Token: " + refreshToken);
				
				// parse the ID Token
				JWT jwt = JWTParser.parse(idTokenTxt);
				String subject = jwt.getJWTClaimsSet().getStringClaim("sub");

				Log.d(TAG, "Loaded subject: " + subject);
				
				Editor edit = prefs.edit();
				edit.putString("oidc_accesstoken", accessToken);
				edit.putString("oidc_idtoken", idTokenTxt);
				edit.putString("oidc_refreshtoken", refreshToken);
				edit.putString("oidc_subject", subject);
				edit.commit();

				
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	
	
}