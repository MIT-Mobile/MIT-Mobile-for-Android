package edu.mit.mitmobile2;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import edu.mit.mitmobile2.MobileWebApi.HttpClientType;
import edu.mit.mitmobile2.about.BuildSettings;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ConnectionWrapper {
	public static enum ErrorType {
		Network,
		Server,
		Timeout
	};
	
	private static final String HTTP_USER_AGENT = 
		"MIT Mobile " + BuildSettings.VERSION_NAME + " for Android";
	
	private static final int CONNECTION_ERROR = 0;
	private static final int CONNECTION_RESPONSE = 1;
	
	static final String TAG = "ConnectionWrapper";
	
	public HttpClientType httpClientType;

	public static interface ConnectionInterface {
		void onResponse(InputStream stream);
		
		void onError(ErrorType error);
	}
	
	private ConnectivityManager mConnectivityManager;
	
	public ConnectionWrapper(Context context) {
		mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		this.httpClientType = HttpClientType.Default;
	}
	
	/*
	 * This will create a connection wrapper that does not attempt to check
	 * if it has a network conenction before making the network request
	 */
	public ConnectionWrapper() {
		this.httpClientType = HttpClientType.Default;
		mConnectivityManager = null;
	}
	
	/*
	 * @return if attempted to begin network connection
	 */
	public boolean openURL(final String url, final ConnectionInterface callback) {
		Log.d(TAG, "starting request: " + url);
		
		if(mConnectivityManager != null) {
			NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
			if(networkInfo == null || !networkInfo.isAvailable()) {
				// network not available call error handler
				callback.onError(ErrorType.Network);
				return false;
			}
		}
		
		// we want to insure any response/error is posted back
		// on the same thread as openURL was initiated
		
		// if there is no Looper, no way to do the connection asynchronously
		// also, probably no need to do asynchronous connection 
		// (since the thread probably does not block anything else)
		if(Looper.myLooper() != null) {
			asynchronous(url, new Handler() {
				@Override
				public void handleMessage(Message msg) {
					Log.d(TAG,"openURL - asynchronous");

					if(msg.arg1 == CONNECTION_ERROR) {
						Log.d(TAG,"CONNECTION ERROR");
						callback.onError((ErrorType) msg.obj);
					} else if(msg.arg1 == CONNECTION_RESPONSE) {
						Log.d(TAG,"ON RESPONSE");
						Log.d(TAG,"msg = " + msg.obj.getClass());
						InputStream i = (InputStream)msg.obj;
						//Log.d(TAG,"stream = " + MobileWebApi.convertStreamToString(i));						
						callback.onResponse((InputStream) msg.obj);
					}
				}
				
			});
		} else {
			synchronous(url, callback);
			Log.d(TAG,"openURL - synchronous");
		}
		
		return true;
	}
	
	public HttpResponse httpClientResponse(HttpGet httpGet) throws ClientProtocolException, IOException {
		Log.d(TAG,"httpClientResponse from ConnectionWrapper");
		HttpClient httpClient = new DefaultHttpClient();
		Log.d(TAG,"1");
		try {
			HttpResponse response = httpClient.execute(httpGet);
			return response;
		}
		catch (IOException e) {
			Log.d(TAG,"IOException = " + e.getMessage());
			return null;
		}
	}
	
	private void asynchronous(final String url, final Handler threadHandler) {
		new Thread() {
			@Override
			public void run() {
				Log.d(TAG,"asynchronous url = " + url);
				Message message = Message.obtain();
				
				HttpGet httpGet = new HttpGet(url);
				httpGet.setHeader("User-Agent", HTTP_USER_AGENT);
				try {
					//HttpResponse response = httpClient.execute(httpGet);
					HttpResponse response = httpClientResponse(httpGet);
					//Log.d(TAG,"response = " + response);
					//Log.d(TAG,"status code = " + response.getStatusLine().getStatusCode());
					//Log.d(TAG,"url = " + httpGet.getURI().toURL().toString());
					if (response != null) {
						Log.d(TAG,"response not null");
						Log.d(TAG,"status code == " +  response.getStatusLine().getStatusCode());
						if(response.getStatusLine().getStatusCode() == 200) {
							InputStream stream = response.getEntity().getContent();
							message.arg1 = CONNECTION_RESPONSE;
							message.obj = stream;						
						} else {
							Log.d(TAG,"status code before error = " + response.getStatusLine().getStatusCode());
							message.arg1 = CONNECTION_ERROR;
							message.obj = ErrorType.Server;
						}
					}
					else {
						//Log.d(TAG,"response is null");
					}
				} catch (IOException e) {
					message.arg1 = CONNECTION_ERROR;
					message.obj = ErrorType.Network;
				}
				
				threadHandler.sendMessage(message);
			}
		}.start();
	}
	
	private static void synchronous(final String url, final ConnectionInterface callback) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("User-Agent", HTTP_USER_AGENT);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			if(response.getStatusLine().getStatusCode() == 200) {
				InputStream stream = response.getEntity().getContent();
				callback.onResponse(stream);						
			} else {
				callback.onError(ErrorType.Server);
			}
		} catch (IOException e) {
			callback.onError(ErrorType.Network);
		}
	}
}
