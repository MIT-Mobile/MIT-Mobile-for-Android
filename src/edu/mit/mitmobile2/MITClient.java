package edu.mit.mitmobile2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebView;

public class MITClient extends DefaultHttpClient {

	private static final String TAG = "MITClient";
	public static final String OK_STATE = "ok";
	public static final String WAYF_STATE = "wayf";
	public static final String IDP_STATE = "idp";
	public static final String AUTH_STATE = "auth";
	
	protected Context mContext;
	SharedPreferences prefs;
	String user;
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	String password;
	URI uri;
	URI targetUri;
	String uriString;
	HttpGet get;
	HttpResponse response;
	HttpEntity responseEntity;
	HttpPost post;
	WebView webview;
	Document document;
	String responseString = "";
	String state;
	
	public MITClient(Context context) {
		super();		
		this.mContext = context;

		// get user name and password from preferences file
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		user = prefs.getString("PREF_TOUCHSTONE_USERNAME", null);
		password = prefs.getString("PREF_TOUCHSTONE_PASSWORD", null);

		this.setRedirectHandler(new DefaultRedirectHandler() {
			String host;
			public URI getLocationURI(HttpResponse response, HttpContext context) {
				Header[] locations = response.getHeaders("Location");
				
				if (locations.length > 0) {
					Header location = locations[0];
					String uriString = location.getValue();
					Log.d(TAG,"uriString from redirect = " + uriString);
					try {
						uri = new URI(uriString);
						host = uri.getHost();
						if (host.equalsIgnoreCase("wayf.mit.edu")) {
							Log.d(TAG, "Redirect to WAYF detected");
							Log.d(TAG,"rawquery = " + uri.getRawQuery());
							state = WAYF_STATE;
						}
						else if (host.equalsIgnoreCase(targetUri.getHost())) {
							state = OK_STATE;
						}
						Log.d(TAG, "Redirect to '"+uri+"' detected");
					} catch (URISyntaxException use) {
						Log.e(TAG, "Invalid Location URI: "+uriString);
					}
					
				}
				
				return uri;
			}
		});
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

	public String getResponse(String targetUrl) {
		try {
			this.targetUri = new URI(targetUrl);
		}
		catch (URISyntaxException e) {
			Log.d(TAG,e.getMessage());
		}
		
		get = new HttpGet(targetUrl);
		try {
			response = this.execute(get);
			responseEntity = response.getEntity();
			if (state == WAYF_STATE ) {
				wayf();
			}
				
			if (state == IDP_STATE) {
				Log.d(TAG,"\nstate = idp");
				idp();
			}			
	
			if (state == AUTH_STATE) {
				authState();
			}
			
			return responseString;
		}
		catch (IOException e) {
			Log.d(TAG,e.getMessage());
			return null;
		}
		
	}
	
	private void wayf() {
		Log.d(TAG, "wayf()");
		Log.d(TAG,"post to wayf");
		Log.d(TAG,"uri = " + uri);
		post = new HttpPost();
	
		post.setURI(uri);
		Log.d(TAG,"post uri in wayf = " + uri.toString() + " "  + uri.getHost() + " " + uri.getRawQuery());

		String user_idp;

		if (user.contains("@")) {
			user_idp = "https://idp.touchstonenetwork.net/shibboleth-idp";
		}
		else {
			user_idp = "https://idp.mit.edu/shibboleth";				
		}
	
		// Add your data
		List nameValuePairs = new ArrayList(1);
		nameValuePairs.add(new BasicNameValuePair("user_idp", user_idp));
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
		
		try {
			response = this.execute(post);
			Log.d(TAG,"status from post = " + response.getStatusLine().getStatusCode());
			
			Header[] locations = response.getHeaders("Location");
			if (locations.length > 0) {
				Header location = locations[0];
				uriString = location.getValue();				
				try {
					uri = new URI(uriString);
				}
				catch (URISyntaxException e) {
					
				}				
			}
			
			if (response.getStatusLine().getStatusCode() == 200 && !uri.getHost().equalsIgnoreCase("wayf.mit.edu")) {
				state = IDP_STATE;
			}
	
			responseString = responseContentToString(response);
			//webview.loadData(responseString, "text/html", "utf-8");
			
			Log.d(TAG,"state after WAYF post = " + state);
			
	//		List<Cookie> cookies = client.getCookieStore().getCookies();
	//		Iterator<Cookie> c = cookies.iterator();
	//		while (c.hasNext()) {
	//			Cookie cookie = c.next();
	//			Log.d(TAG,"cookie domain = " + cookie.getDomain() + " name = " + cookie.getName() + " value = " + cookie.getValue());
	//		}
		}
		catch (IOException e) {
			Log.d(TAG,e.getMessage());
		}
	}

	private void idp() {
		Elements elements;
		Element form;
		
		String formAction = "";
	
		// parse response string to html document
		document = Jsoup.parse(responseString);
	
		// get form action
		elements = document.getElementsByTag("form");
		Log.d(TAG,"number of forms in idp response = " + elements.size());
		
		for (int e = 0; e < elements.size(); e++) {
			form = elements.get(e);
			String tmpAction = form.attr("action");
			if (tmpAction.contains("Username")) {
				formAction = tmpAction;
			}
			Log.d(TAG,"action " + e + " = " + formAction);
		}
				
		post = new HttpPost();
		try {
			uri = new URI(formAction);
		}
		catch (URISyntaxException e) {
			Log.d(TAG,"idp exception = " + e.getMessage());
		}
		
		post.setURI(uri);
	
		// Add post data
		List nameValuePairs = new ArrayList(2);
		nameValuePairs.add(new BasicNameValuePair("j_username", user));
		nameValuePairs.add(new BasicNameValuePair("j_password", password));
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
	
		try {
			response = this.execute(post);
			Log.d(TAG,"status from IDP post = " + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
				Log.d(TAG, "200 code received from idp post");
				state = AUTH_STATE;
				responseString = responseContentToString(response);
				//webview.loadData(responseString, "text/html", "utf-8");
			}
		}
		catch (IOException e) {
			Log.d(TAG,e.getMessage());
		}
	}
	
	private void authState() {
		Elements elements;
		Element form;
		Element input;
		
		String formAction = "";
		String SAMLResponse = "";
		String TARGET = "";
		//Log.d(TAG,"auth state data = " + responseString);
		
		// parse response string to html document
		document = Jsoup.parse(responseString);
	
		// get form action
		elements = document.getElementsByTag("form");
		form = elements.get(0);
		formAction = form.attr("action");
	
		// get SAMLResponse
		elements = document.getElementsByTag("input");
		for (int e = 0; e < elements.size(); e++) {
			input = elements.get(e);
			if (input.attr("name").equalsIgnoreCase("SAMLResponse")) {
				SAMLResponse = input.attr("value");
			}
			if (input.attr("name").equalsIgnoreCase("TARGET")) {
				TARGET = input.attr("value");				
			}
		}
	
		Log.d(TAG,"formAction = " + formAction);
		Log.d(TAG,"SAMLResponse = " + SAMLResponse);
		Log.d(TAG,"TARGET = " + TARGET);
	
		post = new HttpPost();
		try {
			uri = new URI(formAction);
		}
		catch (URISyntaxException e) {
			Log.d(TAG,"idp exception = " + e.getMessage());
		}
		
		post.setURI(uri);
	
		// Add post data
		List nameValuePairs = new ArrayList(2);
		nameValuePairs.add(new BasicNameValuePair("SAMLResponse",SAMLResponse));
		nameValuePairs.add(new BasicNameValuePair("TARGET", TARGET));
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
	
		try {
			response = this.execute(post);
			Log.d(TAG,"status from IDP post = " + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
				Log.d(TAG, "200 code received from auth post");
				Log.d(TAG,"state after auth post = " + state);
				responseString = responseContentToString(response);
				//webview.loadData(responseString, "text/html", "utf-8");
			}
		}
		catch (IOException e) {
			Log.d(TAG,e.getMessage());
		}		
	}

}
