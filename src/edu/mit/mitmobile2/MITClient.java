package edu.mit.mitmobile2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.mit.mitmobile2.touchstone.TouchstoneActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebView;

public class MITClient extends DefaultHttpClient {

	private static final String TAG = "MITClient";
	public static final String PREFS_STATE = "prefs";
	public static final String OK_STATE = "ok";
	public static final String WAYF_STATE = "wayf";
	public static final String IDP_STATE = "idp";
	public static final String AUTH_STATE = "auth";
	public static final String ERROR_STATE = "error";	
	public static final String AUTH_ERROR_STATE = "auth_error";
	public static final String CANCELLED_STATE = "cancelled";	
	public static final String AUTH_ERROR_KERBEROS = "Error: Please enter a valid username and password"; // error message from invalid kerberos login
	public static final String AUTH_ERROR_CAMS = "Error: Enter your email address and password"; // error message from invaid cams login
	
	public static SharedPreferences prefs;
	public static final String TOUCHSTONE_REQUEST = "TOUCHSTONE_REQUEST";
	public static final String TOUCHSTONE_LOGIN = "TOUCHSTONE_LOGIN";    
	public static final String TOUCHSTONE_CANCEL = "TOUCHSTONE_CANCEL";    
	public static final String TOUCHSTONE_DIALOG = "TOUCHSTONE_DIALOG";    
	final SharedPreferences.Editor prefsEditor;

	HttpGet mHttpGet;
	String requestKey;
	URI targetUri;
	
	// Hashmap for keeping track of the status of requests made by the HttpClient 
	// because this is not an activity, there is no context for startActivityForResult or UI handlers
	public static Map<String, MITClientData> requestMap = new HashMap<String, MITClientData>();
	
	// Cookies
	//public static List<Cookie> cookies = new ArrayList();
	public static CookieStore cookieStore;
	
	protected Context mContext;
	
	private static String user;
	private static String password;
	
	boolean rememberLogin;
	URI uri;
	//URI targetUri;
	String uriString;
	HttpGet get;
	HttpResponse response;
	HttpEntity responseEntity;
	HttpPost post;
	WebView webview;
	Document document;
	String responseString = "";
	String state;
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@SuppressWarnings("static-access")
	public MITClient(Context context) {
		super();		
		Log.d(TAG,"MITClient()");
		this.mContext = context;
		
		//Log.d(TAG,"MITClient.cookieStore = " + MITClient.cookieStore);
		if (MITClient.cookieStore == null) {
			MITClient.cookieStore = this.getCookieStore();
		}

		this.setCookieStore(this.cookieStore);
		
		// get user name and password from preferences file
		prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		prefsEditor = prefs.edit();
		MITClient.user = prefs.getString("PREF_TOUCHSTONE_USERNAME", null);
		MITClient.password = prefs.getString("PREF_TOUCHSTONE_PASSWORD", null);
		rememberLogin = prefs.getBoolean("PREF_TOUCHSTONE_REMEMBER_LOGIN", false);
		Log.d(TAG,"user = " + user);
				
		this.setRedirectHandler(new DefaultRedirectHandler() {
			String host;
			@Override
			public URI getLocationURI(HttpResponse response, HttpContext context) {
				//Log.d(TAG,"redirectHandler");
								
				Header[] locations = response.getHeaders("Location");
				
				if (locations.length > 0) {
					Header location = locations[0];
					String uriString = location.getValue();
					//Log.d(TAG,"uriString from redirect = " + uriString);
					try {
						uri = new URI(uriString);
						host = uri.getHost();
						if (host.equalsIgnoreCase("wayf.mit.edu")) {
							state = WAYF_STATE;
							Log.d(TAG,"state = " + state);
						}
						else {
							state = OK_STATE;
						}
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

	
	public HttpResponse getResponse(HttpGet httpGet) {
		try {
			this.mHttpGet = httpGet;
			this.targetUri = httpGet.getURI();
			MITClientData clientData = new MITClientData();
			clientData.setTargetUri(this.targetUri);
			requestKey = System.currentTimeMillis()/1000 + "";
			Log.d(TAG,"requestKey " + requestKey + " created");
			requestMap.put(requestKey, clientData);
			response = this.execute(httpGet);
			responseEntity = response.getEntity();
			
			if (state == OK_STATE) {
				saveLogin();
				return response;
			}
			
			if (state == WAYF_STATE ) {
				Log.d(TAG,"wayf state");
				wayf();
			}
				
			if (state == IDP_STATE) {
				Log.d(TAG,"idp state");
				idp();
			}			
	
			if (state == AUTH_STATE) {
				Log.d(TAG,"auth state");
				authState();
			}
		
			if (state == AUTH_ERROR_STATE) {
				Log.d(TAG,"auth error state");
				authError();
			}

			if (state == CANCELLED_STATE) {
				Log.d(TAG,"status in cancelled state = " + response.getStatusLine().getStatusCode());
				MITHttpEntity entity = new MITHttpEntity();
				entity.setContent(MITHttpEntity.JSON_CANCEL);
				response.setStatusCode(200);
				response.setEntity(entity);
				return response;
			}

			if (state == OK_STATE) {
				saveLogin();
				return response;
			}

			return null;
		}
		catch (IOException e) {
			Log.d(TAG,"get response exception = " + e.getMessage());
			return null;
		}
		
	}

	private void wayf() {
		
		// Launch preferences activity if user or password are not set
		if (MITClient.user == null || MITClient.user.length() == 0 || MITClient.password == null || MITClient.password.length() == 0) {
			//requestKey = System.currentTimeMillis()/1000 + "";
			Log.d(TAG,"requestKey = " + requestKey);
			((MITClientData)requestMap.get(requestKey)).setTouchstoneState(TOUCHSTONE_REQUEST);
			Intent touchstoneIntent = new Intent(mContext, TouchstoneActivity.class);
			touchstoneIntent.putExtra("requestKey",requestKey);
			((Activity) mContext).startActivity(touchstoneIntent);
		
			Log.d(TAG,"requestKey " + requestKey + " value = " + MITClient.requestMap.get(requestKey));
			while( 	((MITClientData)MITClient.requestMap.get(requestKey)).getTouchstoneState().equalsIgnoreCase(TOUCHSTONE_REQUEST) ) {
				// do stuff
			    // don't burn the CPU
			    try {
			    	Log.d(TAG,"requestMap " + requestKey + " = " + requestMap.get(requestKey));
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
					
		}
		Log.d(TAG,"request key in wayf = " + requestKey);
		MITClientData clientData = (MITClientData)MITClient.requestMap.get(requestKey);
		Log.d(TAG,"touchstone state = " + clientData.getTouchstoneState());
		if ( clientData.getTouchstoneState() == null || clientData.getTouchstoneState().equalsIgnoreCase(TOUCHSTONE_LOGIN) ) {
			post = new HttpPost();
			
			post.setURI(uri);
			Log.d(TAG,"post uri in wayf = " + uri.toString() + " "  + uri.getHost() + " " + uri.getRawQuery());

			String user_idp;
			String tmpUser = user.toUpperCase();
			Log.d(TAG,"user = " + user);
			if (tmpUser.contains("@") && !tmpUser.contains("@MIT.EDU")) {
				user_idp = "https://idp.touchstonenetwork.net/shibboleth-idp";
			}
			else {
				user_idp = "https://idp.mit.edu/shibboleth";				
				// remove "@MIT.EDU from user name
				if (tmpUser.contains("@MIT.EDU")) {
					user = user.substring(0,user.length() - 8);
					Log.d(TAG,"user = " + user);
				}
			}

			Log.d(TAG,"user_idp = " + user_idp);
			
			// Add your data
			List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("user_idp", user_idp));
			try {
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}  
			
			try {
				response = this.execute(post);
				
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
				else {
					responseString = responseContentToString(response);
				}
				
				//Log.d(TAG,"response string at end of wayf = " + responseString);
				//Log.d(TAG,"state after WAYF post = " + state);
			}
			catch (IOException e) {
				Log.d(TAG,"WAYF error " + e.getMessage());
			}
			
		}
		
		else {
			state = CANCELLED_STATE;
		}
	}
	
	private void idp() {
		Log.d(TAG,"idp");
		Elements elements;
		Element form;
		
		String formAction = "";
	
		// parse response string to html document
		String responseString = responseContentToString(response);
		
		//Log.d(TAG,"response string in idp = " + responseString);
		document = Jsoup.parse(responseString);
	
		// get form action
		elements = document.getElementsByTag("form");

		for (int e = 0; e < elements.size(); e++) {
			form = elements.get(e);
			String tmpAction = form.attr("action");
			if (tmpAction.contains("Username")) {
				formAction = tmpAction;
			}
			if (tmpAction.contains("UserPassword")) {
				formAction = "https://idp.touchstonenetwork.net" + tmpAction;
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
		List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("j_username", user));
		nameValuePairs.add(new BasicNameValuePair("j_password", password));
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			Log.d(TAG,e1.getMessage());
			//e1.printStackTrace();
		}  
	
		try {
			Log.d(TAG,"before post");
			response = this.execute(post);
			Log.d(TAG,"after post");
			Log.d(TAG,"idp status code = " + response.getStatusLine().getStatusCode());
			//this.saveCookies();
			if (response.getStatusLine().getStatusCode() == 200) {
				state = AUTH_STATE;
			}
		}
		catch (Exception e) {
			Log.d(TAG,e.getMessage());
		}
	}
	
	private void authState() {
		Log.d(TAG,"authState");
		Elements elements;
		Element form;
		Element input;
		
		String formAction = "";
		String SAMLResponse = "";
		String TARGET = "";
		String RelayState = "";
		// parse response string to html document
		String responseString = responseContentToString(response);
		

		// Check for an error message in the response string
		if (responseString.contains(AUTH_ERROR_CAMS) || responseString.contains(AUTH_ERROR_KERBEROS)) {
			state = AUTH_ERROR_STATE;
			Log.d(TAG,"login error");
		}
		else {
			document = Jsoup.parse(responseString);
		
			// get form action
			elements = document.getElementsByTag("form");
			form = elements.get(0);
			formAction = form.attr("action");
		
			// get SAMLResponse
			elements = document.getElementsByTag("input");
			for (int e = 0; e < elements.size(); e++) {
				input = elements.get(e);
				Log.d(TAG,"element name " + e + " = " + input.attr("name"));
				if (input.attr("name").equalsIgnoreCase("SAMLResponse")) {
					SAMLResponse = input.attr("value");
				}
				if (input.attr("name").equalsIgnoreCase("TARGET")) {
					TARGET = input.attr("value");				
				}
	
				if (input.attr("name").equalsIgnoreCase("RelayState")) {
					RelayState = input.attr("value");				
				}
	
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
			List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("SAMLResponse",SAMLResponse));
			nameValuePairs.add(new BasicNameValuePair("TARGET", TARGET));
			nameValuePairs.add(new BasicNameValuePair("RelayState", RelayState));
			try {
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}  
		
			try {
				response = this.execute(post);
				//this.saveCookies();
				//Log.d(TAG,"status from IDP post = " + response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == 200) {
					//responseString = responseContentToString(response);
					//Log.d(TAG,responseString);
					//tmpResponse.
					state = OK_STATE;
					//Log.d(TAG,"ok state");
					//ok();
				}
			}
			catch (IOException e) {
				Log.d(TAG,""+e.getMessage());
			}
		}
		
		// Clear user name and password is rememberLogin is false
		Log.d(TAG,"state for rememberLogin = " + state);
		if (!rememberLogin) {
			prefsEditor.putString("PREF_TOUCHSTONE_USERNAME", null);
			prefsEditor.putString("PREF_TOUCHSTONE_PASSWORD", null);
			prefsEditor.putBoolean("PREF_TOUCHSTONE_REMEMBER_LOGIN", false);	
		}
	}
	
	
	public void debugCookies() {
		Log.d(TAG,"debugCookies()");
		Log.d(TAG,"cookieStore = " + this.getCookieStore());
		List<Cookie> cookies = this.getCookieStore().getCookies();
		Iterator<Cookie> c = cookies.iterator();
		while (c.hasNext()) {
			Cookie cookie = c.next();
			Log.d(TAG,"cookie domain = " + cookie.getDomain() + " name = " + cookie.getName() + " value = " + cookie.getValue() + " expires = " + cookie.getExpiryDate());
		}
	}

	class MITRequestInterceptor implements HttpRequestInterceptor {

		@Override
		public void process(HttpRequest arg0, HttpContext arg1)
				throws HttpException, IOException {
			// TODO Auto-generated method stub
			Log.d(TAG,"request intercept");
			Log.d(TAG,arg0.getRequestLine().getUri());
		}
		
	}
	
	class MITInterceptor implements HttpResponseInterceptor {
		
		public MITInterceptor() {
			super();
			Log.d(TAG,"intercept");
			// TODO Auto-generated constructor stub
		}

		@Override
		public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
			// TODO Auto-generated method stub
			Header[] headers = response.getHeaders("Set-Cookie");
			for (int h = 0; h < headers.length; h++) {
				Header header = (Header)headers[h];
				HeaderElement[] headerElements = header.getElements();
				for (int e = 0; e < headerElements.length; e++) {
					HeaderElement headerElement = headerElements[e];
					Log.d(TAG,"Header Element " + e);
					Log.d(TAG,"name = " + headerElement.getName());
					Log.d(TAG,"value = " + headerElement.getValue());
					NameValuePair[] parameters = headerElement.getParameters();
					for (int p = 0; p < parameters.length; p++) {
						NameValuePair parameter = parameters[p];
						Log.d(TAG,"parameter " + p + " " + parameter.getName() + " = " + parameter.getValue());
					}
				}
			}
		}
	}
	
	public static void clearCookies() {
		cookieStore = null;
	}

	private void authError() {
		Log.d(TAG,"authError()");

		MITClientData clientData = (MITClientData)MITClient.requestMap.get(requestKey);
		clientData.setTouchstoneState(TOUCHSTONE_REQUEST);
		Intent touchstoneIntent = new Intent(mContext, TouchstoneActivity.class);
		touchstoneIntent.putExtra("requestKey",requestKey);
		touchstoneIntent.putExtra("touchstoneState",AUTH_ERROR_STATE);

		((Activity) mContext).startActivity(touchstoneIntent);
	
		Log.d(TAG,"Sending auth error state to touchstone" + requestKey);
		//while (MITClient.requestMap.get(requestKey) == TOUCHSTONE_REQUEST) {
		while (	clientData.getTouchstoneState().equalsIgnoreCase(TOUCHSTONE_REQUEST)) {
			// do stuff
		    // don't burn the CPU
		    try {
		    	Log.d(TAG,"requestMap " + requestKey + " = " + requestMap.get(requestKey));
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// If the touchstone request was not cancelled, retry the login
		if (!clientData.getTouchstoneState().equals(TOUCHSTONE_CANCEL)) {
	        // retry login
			response = getResponse(new HttpGet(targetUri));
		}
		else {
			state = CANCELLED_STATE;
		}
		
	}

	public static String getUser() {
		return user;
	}

	public static void setUser(String user) {
		MITClient.user = user;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		MITClient.password = password;
	}

	public void saveLogin() {
		rememberLogin = prefs.getBoolean("PREF_TOUCHSTONE_REMEMBER_LOGIN", false);
		if (rememberLogin) {
			prefsEditor.putString("PREF_TOUCHSTONE_USERNAME", MITClient.getUser());
			prefsEditor.putString("PREF_TOUCHSTONE_PASSWORD", MITClient.getPassword());
			prefsEditor.commit();
		}
	}
}
