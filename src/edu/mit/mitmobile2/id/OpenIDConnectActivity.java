package edu.mit.mitmobile2.id;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;

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
	
	
	private String clientId = "05a3f7f5-c6db-4b1b-aca6-cc2f3c163b78";
	private String clientSecret = "APyV0tFxyDrvsdr7M0ZPaVdnLQ_tTicMdtkw7OvkePXOhv_MD1IOCSBjx1voJ7wWicGJl5LQj7I6SnCgni1wRGk";
	
	// TODO: figure out how to save/check this?
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
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenIDConnectActivity.this);
				Editor edit = prefs.edit();
				edit.putString("oidc_accesstoken", "");
				edit.putString("oidc_idtoken", "");
				edit.putString("oidc_subject", "");
				edit.putString("oidc_username", "");
				edit.putString("oidc_email", "");
				edit.commit();
				updateDisplay();
			}
		});
		
	}
	
	private void updateDisplay() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		String idToken = prefs.getString("oidc_idtoken", "");
		String subjectVal = prefs.getString("oidc_subject", "");
		String usernameVal = prefs.getString("oidc_username", "");
		String emailVal = prefs.getString("oidc_email", "");
		
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
		Log.d(TAG, "Scheme: " + intent.getScheme());
		//Log.d(TAG, "Data.Scheme: " + intent.getData().getScheme());		
		//Log.d(TAG, "Query: " + intent.getData().getQuery());
		
		if (intent.getScheme() != null && intent.getScheme().equals("mitmobile2")) {
			// processing a callback URL of some type
			
			String code = intent.getData().getQueryParameter("code");
			String state = intent.getData().getQueryParameter("state");
			
			Log.d(TAG, "Code: " + code);
			Log.d(TAG, "State: " + state);
			
			// TODO: check "state" for consistency
			if (state.equals(this.state)) {
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
				params.add(new BasicNameValuePair("client_secret", clientSecret));
				params.add(new BasicNameValuePair("grant_type", "authorization_code"));
				params.add(new BasicNameValuePair("code", code));
				post.setEntity(new UrlEncodedFormEntity(params));
				
				HttpResponse response = client.execute(post);
				
				StatusLine result = response.getStatusLine();
				
				if (result.getStatusCode() == HttpStatus.SC_OK) {
					// get the body, parse as JSON
					HttpEntity entity = response.getEntity();
					
					JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
					JSONObject obj = (JSONObject) parser.parse(entity.getContent());

					String idTokenTxt = JSONObjectUtils.getString(obj, "id_token");
					String accessToken = JSONObjectUtils.getString(obj, "access_token");

					// parse the ID Token
					JWT jwt = JWTParser.parse(idTokenTxt);
					String subject = jwt.getJWTClaimsSet().getStringClaim("sub");

					Log.d(TAG, "Loaded subject: " + subject);
					
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenIDConnectActivity.this);
					Editor edit = prefs.edit();
					edit.putString("oidc_accesstoken", accessToken);
					edit.putString("oidc_idtoken", idTokenTxt);
					edit.putString("oidc_subject", subject);
					edit.commit();

					
					// since we got an access token, pull the profile information
					
					loadProfile();
					
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
		
		updateDisplay();
	}

	private void loadProfile() {
		// Fetch a token
		HttpClient client = new DefaultHttpClient();
		String url = userInfoEndpoint;
		
		try {
			
			HttpPost post = new HttpPost(url);
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenIDConnectActivity.this);
			
			String accessToken = prefs.getString("oidc_accesstoken", "");
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("access_token", accessToken));
			post.setEntity(new UrlEncodedFormEntity(params));
			
			HttpResponse response = client.execute(post);
			
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