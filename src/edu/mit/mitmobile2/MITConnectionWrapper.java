package edu.mit.mitmobile2;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import edu.mit.mitmobile2.ConnectionWrapper.ConnectionInterface;
import edu.mit.mitmobile2.MobileWebApi.HttpClientType;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class MITConnectionWrapper extends ConnectionWrapper {

	public static final String TAG = "MITConnectionWrapper";

	static Context mContext;
	
	public MITConnectionWrapper(Context context, HttpClientType httpClientType) {
		super();
		Log.d(TAG,"MITConnectionWrapper");
		this.mContext = context;
	}
		
	public HttpResponse httpClientResponse(HttpGet httpGet) throws ClientProtocolException, IOException {
		Log.d(TAG,"httpClientResponse from MITConnectionWrapper");
		MITClient mitClient = new MITClient(mContext);
		HttpResponse response = null;
		Log.d(TAG,"httpGet = " + httpGet.getURI());
		try {
			response = mitClient.getResponse(httpGet);
			//Log.d(TAG,"response = " + response);
		}
		catch (Exception e) {
			Log.d(TAG,e.getMessage());
		}
		return response;
	}
}
