package edu.mit.mitmobile2.touchstone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TouchstonePrefsActivity;

//public class FacilitiesActivity extends ModuleActivity implements OnClickListener {
public class TouchstoneActivity extends ModuleActivity {
	

	private Context mContext;	

	TextView emergencyContactsTV;

	SharedPreferences pref;
	String user = "";
	String password = "";
	DefaultHttpClient client;
	HttpGet get;
	HttpResponse response;
	HttpEntity responseEntity;
	String responseString = "";
	String state = "ok";
	HttpPost post;
	WebView webview;
	public static final String INITIAL_STATE = "initial";
	public static final String WAYF_STATE = "wayf";
	public static final String IDP_STATE = "idp";
	public static final String AUTH_STATE = "auth";
	public static final String FINAL_STATE = "final";

	URI uri = null;
	String uriString = "";
	public static String targetUrl = "https://stellar.mit.edu/atstellar";
	//public static String targetUrl = "https://help.mit.edu/touchstone";
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

        // get user name and password from preferences file
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		user = prefs.getString("PREF_TOUCHSTONE_USERNAME", null);
		password = prefs.getString("PREF_TOUCHSTONE_PASSWORD", null);

		// open the preferences if the username and password are not defined
        if (user == null || password == null) {
        	startActivity( new Intent(this, TouchstonePrefsActivity.class) );	
        }
        
        try {
			createViews();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.d(TAG,e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(TAG,e.getMessage());
		}
	}
	
/*
 	private DefaultHttpClient createHttpClient() {

		SSLSocketFactory trustedFactory = null;
		try {
			KeyStore trusted = KeyStore.getInstance("BKS");
			InputStream in = this.mContext.getResources().openRawResource(R.raw.trusted_key_store);
			try {
				trusted.load(in, "testpass".toCharArray());
			} 
			catch (Exception e) {
				Log.d(TAG,e.getMessage());
			}
			finally {
				in.close();
			}
			
			trustedFactory = new SSLSocketFactory(trusted);
		} catch (Exception e) {
			Log.d(TAG,e.toString());
		}

		if (trustedFactory != null) {
			SchemeRegistry registry = new SchemeRegistry();
		    registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		    registry.register(new Scheme("https", trustedFactory, 443));
			
		    return new DefaultHttpClient(new ThreadSafeClientConnManager(null, registry), null);
		} else {
			return new DefaultHttpClient();
		}
	}
*/
	
	private void createViews() throws ClientProtocolException, IOException {
		setContentView(R.layout.touchstone_main);
		Log.d(TAG,"createViews()");
		 //request.setHeader("User-Agent", "set your desired User-Agent");

		webview = (WebView) findViewById(R.id.touchstoneWV);

		try {

		client = new DefaultHttpClient();
		
		client.addRequestInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {
				Log.d(TAG,request.toString());
				
			}
		});
		client.setRedirectHandler(new DefaultRedirectHandler() {
			public URI getLocationURI(HttpResponse response, HttpContext context) {
				Header[] locations = response.getHeaders("Location");
				
				if (locations.length > 0) {
					Header location = locations[0];
					String uriString = location.getValue();
					
					try {
						uri = new URI(uriString);
						
						if (uri.getHost().equalsIgnoreCase("wayf.mit.edu")) {
							Log.d(TAG, "Redirect to WAYF detected");
							Log.d(TAG,"rawquery = " + uri.getRawQuery());
							state = WAYF_STATE;
						}
						
						Log.d(TAG, "Redirect to '"+uri+"' detected");
					} catch (URISyntaxException use) {
						Log.e(TAG, "Invalid Location URI: "+uriString);
					}
					
				}
				
				return uri;
			}
		});
		
		initialGet();
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

		if (state == FINAL_STATE) {
			finalState();
		}

		//		Header[] headerArray = getResponse.getAllHeaders();
//		for (int h = 0; h < headerArray.length; h++) {
//			Header header = (Header)headerArray[h];
//			Log.d(TAG,"header = " + header.getName() + " value = " + header.getValue() + " " + header.toString());
//
//		}
		
  		      
		}
		catch (Exception e) {
			Log.d(TAG,e.toString());
			Log.d(TAG,Log.getStackTraceString(e));
		}
	}
	
	private void initialGet() {
		get = new HttpGet(targetUrl);

		// Execute the GET call and obtain the response		
		if (get == null) {
			Log.d(TAG, "This shouldn't be happening!");
		}
		
		try {
			response = client.execute(get);
			responseEntity = response.getEntity();
			responseString = this.responseContentToString(response);
			webview.loadData(responseString, "text/html", "utf-8");
			//Log.d(TAG, "state after initial get = " + state);
		}
		catch (IOException e) {
			Log.d(TAG,e.getMessage());
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
//		Log.d(TAG,"selected user_idp = " + user_idp);
//		HttpParams httpParams = post.getParams();
//		httpParams.setParameter("user_idp",user_idp);
//		post.setParams(httpParams);

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
			response = client.execute(post);
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
			webview.loadData(responseString, "text/html", "utf-8");
			
			Log.d(TAG,"state after WAYF post = " + state);
			
			List<Cookie> cookies = client.getCookieStore().getCookies();
			Iterator<Cookie> c = cookies.iterator();
			while (c.hasNext()) {
				Cookie cookie = c.next();
				Log.d(TAG,"cookie domain = " + cookie.getDomain() + " name = " + cookie.getName() + " value = " + cookie.getValue());
			}

			
		}
		catch (IOException e) {
			Log.d(TAG,e.getMessage());
		}
	}
	
	private void idp() {
		//get the form action from the html response
//		org.jsoup.nodes.Document doc = Jsoup.parse(responseString);
//		Elements forms = doc.select("form#loginform");
//		for (Element form : forms) {
//		    Log.d(TAG,"form = " + form.text());
//		}

		//DEBUG
		String formAction = "https://idp.mit.edu/idp/Authn/UsernamePassword";
		//DEBUG

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
			response = client.execute(post);
			Log.d(TAG,"status from IDP post = " + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
				Log.d(TAG, "200 code received from idp post");
				state = AUTH_STATE;
				responseString = responseContentToString(response);
				webview.loadData(responseString, "text/html", "utf-8");

				//Log.d(TAG,"final data = " + responseString);
				
				List<Cookie> cookies = client.getCookieStore().getCookies();
				Iterator<Cookie> c = cookies.iterator();
				while (c.hasNext()) {
					Cookie cookie = c.next();
					Log.d(TAG,"cookie domain = " + cookie.getDomain() + " name = " + cookie.getName() + " value = " + cookie.getValue());
				}

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
		Document document = Jsoup.parse(responseString);

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
			response = client.execute(post);
			Log.d(TAG,"status from IDP post = " + response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
				Log.d(TAG, "200 code received from auth post");
				state = FINAL_STATE;
				responseString = responseContentToString(response);
				webview.loadData(responseString, "text/html", "utf-8");
			}
		}
		catch (IOException e) {
			Log.d(TAG,e.getMessage());
		}
	}

	private void finalState() {
		get = new HttpGet(targetUrl);

		// Execute the GET call and obtain the response
		
		if (get == null) {
			Log.d(TAG, "This shouldn't be happening!");
		}
		try {
			response = client.execute(get);
			responseString = responseContentToString(response);
			webview.loadData(responseString, "text/html", "utf-8");
			//Log.d(TAG,"final data = " + responseString);
		}
		catch (IOException e) {
			Log.d(TAG,e.getMessage());
		}
	}
	
	@Override
	protected Module getModule() {
		return null;
	}

	@Override
	public boolean isModuleHomeActivity() {
		return true;
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
	
}
