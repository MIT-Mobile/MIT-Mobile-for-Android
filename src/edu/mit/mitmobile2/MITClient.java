package edu.mit.mitmobile2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
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

	// Cookies
	//public static List<Cookie> cookies = new ArrayList();
	public static CookieStore cookieStore;
	
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
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public MITClient(Context context) {
		super();		
		Log.d(TAG,"MITClient()");
		this.mContext = context;

		//Log.d(TAG,"MITClient.cookieStore = " + MITClient.cookieStore);
		if (MITClient.cookieStore == null) {
			MITClient.cookieStore = this.getCookieStore();
		}

		this.setCookieStore(this.cookieStore);

		//Log.d(TAG,"after setting cookie store");
		//Log.d(TAG,"cookieStore = " + this.cookieStore);
		
		// get user name and password from preferences file
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		user = prefs.getString("PREF_TOUCHSTONE_USERNAME", null);
		password = prefs.getString("PREF_TOUCHSTONE_PASSWORD", null);

		
		this.setRedirectHandler(new DefaultRedirectHandler() {
			String host;
			public URI getLocationURI(HttpResponse response, HttpContext context) {
				Log.d(TAG,"redirectHandler");
								
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
							Log.d(TAG,"state = " + state);
							////////////////////////////////////////////////////////////
							// DEBUG COOKIES
							Header[] headers = response.getHeaders("Set-Cookie");
							for (int h = 0; h < headers.length; h++) {
								Header header = (Header)headers[h];
								HeaderElement[] headerElements = header.getElements();
								for (int e = 0; e < headerElements.length; e++) {
									HeaderElement headerElement = headerElements[e];
									Log.d(TAG,"Header Element " + e);
									Log.d(TAG,"name = " + headerElement.getName());
									Log.d(TAG,"value = " + headerElement.getValue());
								}
							}
							/////////////////////////////////////////////////////////////
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
		
		//this.addResponseInterceptor(new MITInterceptor());
		//this.addRequestInterceptor(new MITRequestInterceptor());
		
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
//		try {
//			this.targetUri = new URI(targetUrl);
//		}
//		catch (URISyntaxException e) {
//			Log.d(TAG,e.getMessage());
//		}
//		
//		get = new HttpGet(targetUrl);
		try {
			response = this.execute(httpGet);

			responseEntity = response.getEntity();
			if (state == OK_STATE) {
				Log.d(TAG,"ok state");
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
		
			if (state == OK_STATE) {
				ok();
				//return this.execute(httpGet);		
			}
			
			//Log.d(TAG,"reponseString = " + responseContentToString(response));
			return response;
		}
		catch (IOException e) {
			Log.d(TAG,"get response exception = " + e.getMessage());
			return null;
		}
		
	}

	private void wayf() {
		Log.d(TAG, "wayf()");

		Log.d(TAG,"post to wayf");
		Log.d(TAG,"uri = " + uri);
		post = new HttpPost();
	
		post.setURI(uri);
		//Log.d(TAG,"post uri in wayf = " + uri.toString() + " "  + uri.getHost() + " " + uri.getRawQuery());

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
			//this.saveCookies();
			//Log.d(TAG,"status from post = " + response.getStatusLine().getStatusCode());
			
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
			//this.saveCookies();
			if (response.getStatusLine().getStatusCode() == 200) {
				state = AUTH_STATE;
			}
		}
		catch (IOException e) {
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
		
		// parse response string to html document
		String responseString = responseContentToString(response);
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
	
		//Log.d(TAG,"formAction = " + formAction);
		//Log.d(TAG,"SAMLResponse = " + SAMLResponse);
		Log.d(TAG,"debug cookies in auth_state");
		debugCookies();
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
			//this.saveCookies();
			//Log.d(TAG,"status from IDP post = " + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
				state = OK_STATE;
				Log.d(TAG,"ok state");
				ok();
				responseString = responseContentToString(response);
				Log.d(TAG,"response string from autstate = " + responseString);
			}
		}
		catch (IOException e) {
			Log.d(TAG,e.getMessage());
		}
	}
	
	
	// This method is a kludge. For some reason, the redirect is losing the module and command parameters even though they are visible in the shibstate cookie. For now, we'll use that cookie to redirect to the correct url
	private void ok() {
		List<Cookie> cookies = this.getCookieStore().getCookies();
		Iterator<Cookie> c = cookies.iterator();
		while (c.hasNext()) {
			Cookie cookie = c.next();
			String cookieName =  cookie.getName();
			if (cookieName.contains("_shibstate")) {
				String cookieValue = cookie.getValue();
				Log.d(TAG,"go to " + cookieValue);
				HttpGet httpGet = new HttpGet(URLDecoder.decode(cookieValue));
				Log.d(TAG, "url from get = " + httpGet.getURI().toString());
				try {
					response = this.execute(httpGet);
				}
				catch (Exception e) {
					Log.d(TAG,"execute httpGet exception = " + e.getMessage());
				}
			}
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

//	public void saveCookies() {
//		List<Cookie> cookies = this.getCookieStore().getCookies();
//		Iterator<Cookie> c = cookies.iterator();
//		while (c.hasNext()) {
//			Cookie cookie = c.next();
//			BasicClientCookie tmpCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
//			tmpCookie.setDomain(cookie.getDomain());
//			tmpCookie.setComment(cookie.getComment());
//			tmpCookie.setExpiryDate(cookie.getExpiryDate());
//			tmpCookie.setPath(cookie.getPath());
//			tmpCookie.setVersion(cookie.getVersion());
//			this.cookies.add(tmpCookie);
//		}		
//	}
//
//	public void restoreCookies() {
//		if (this.cookies != null) {
//			for (int c = 0; c < this.cookies.size(); c++) {
//				BasicClientCookie tmpCookie = (BasicClientCookie)this.cookies.get(c);
//				this.getCookieStore().addCookie(tmpCookie);
//			}
//		}		
//	}

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
	
}
