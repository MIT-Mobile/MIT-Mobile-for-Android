package edu.mit.mitmobile2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import edu.mit.mitmobile2.MobileWebApi.HttpClientType;

import android.content.Context;
import android.util.Log;

public class MITConnectionWrapper extends ConnectionWrapper {

	public static final String TAG = "MITConnectionWrapper";
	public static final String AUTH_ERROR_KERBEROS = "Error: Please enter a valid username and password"; // error message from invalid kerberos login
	public static final String AUTH_ERROR_CAMS = "Error: Enter your email address and password"; // error message from invaid cams login
	public static final String AUTH_ERROR_STATE = "auth_error";
	
	String state;
	static Context mContext;
	
	@SuppressWarnings("static-access")
	public MITConnectionWrapper(Context context, HttpClientType httpClientType) {
		super();
		Log.d(TAG,"MITConnectionWrapper");
		this.mContext = context;
	}
		
	@Override
	public HttpResponse httpClientResponse(HttpGet httpGet) throws ClientProtocolException, IOException {
		Log.d(TAG,"httpClientResponse from MITConnectionWrapper");
		MITClient mitClient = new MITClient(mContext);
		HttpResponse response = null;
		Log.d(TAG,"httpGet = " + httpGet.getURI());
		try {
			Log.d(TAG,"before get response");
			response = mitClient.getResponse(httpGet);
			Log.d(TAG,"after get response");
			//DEBUG
			//Log.d(TAG,"response = " + response);
		}
		catch (Exception e) {
			Log.d(TAG,e.getStackTrace().toString());
			//Log.d(TAG,e.getMessage());
		}
		return response;
	}
	
	@Override
	public HttpResponse httpClientResponse(HttpPost httpPost, List<BasicNameValuePair> nameValuePairs) throws ClientProtocolException, IOException {
		Log.d(TAG,"httpClientResponse from MITConnectionWrapper");
		MITClient mitClient = new MITClient(mContext);
		HttpResponse response = null;
		Log.d(TAG,"httpGet = " + httpPost.getURI());
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		try {
			Log.d(TAG,"before get response");
    		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			mitClient.getResponse(httpPost);
			response = mitClient.getResponseFromPost(httpPost);
			Log.d(TAG,"after get response");
		}
		catch (Exception e) {
			Log.d(TAG,e.getStackTrace().toString());
		}
		
		return response;
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
