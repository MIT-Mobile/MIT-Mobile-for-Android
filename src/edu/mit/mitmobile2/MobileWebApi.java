package edu.mit.mitmobile2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.ConnectionWrapper.ConnectionInterface;
import edu.mit.mitmobile2.ConnectionWrapper.ErrorType;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class MobileWebApi {

	private static final String TAG = "MobileWebApi";
	public static final String BASE_PATH = "/api";

	public static enum HttpClientType {
		Default,
		MIT
	};
	
	public static enum ResponseType {
		JSONObject,
		JSONArray,
		Raw
	};
	
	public static enum LoadingDialogType {
		Generic,
		Search
	}
	
	public HttpClientType httpClientType;
	
	public static int ERROR = 0;
	public static int SUCCESS = 1;
	public static int CANCELLED = 2;
	
	public final static String NETWORK_ERROR = "There was a problem connecting to the network.";
	public final static String SERVER_ERROR = "There was a problem connecting to the server.";
	public final static String SEARCH_ERROR_MESSAGE = "There was a problem loading the search results. Please try again.";
	public final static String TOUCHSTONE_ERROR = "There was a problem logging on to Touchstone. Please try again with the correct username and password.";
	
	final static String GENERIC_MESSAGE =  "Loading...";
	final static String SEARCH_MESSAGE = "Searching...";
		
	public static interface ErrorResponseListener {
		public void onError();
	}
	
	public static abstract class CancelRequestListener {
		private boolean mRequestCancelled = false;
		
        private void notifyRequestCancelled() {
        	mRequestCancelled = true;
        	onCancel();
        }
		
        public boolean wasRequestCancelled() {
        	return mRequestCancelled;
        }
        
		public abstract void onCancel();
		
	}
	
	public static abstract class ResponseListener {
		private ErrorResponseListener mErrorResponseListener;
		private CancelRequestListener mCancelRequestListener;
		
		ResponseListener(ErrorResponseListener errorListener, CancelRequestListener cancelListener) {
			mErrorResponseListener = errorListener;
			mCancelRequestListener = cancelListener;
		}
		
		public void onError() {
			mErrorResponseListener.onError();
		}
		
		public CancelRequestListener getCancelRequestListener() {
			return mCancelRequestListener;
		}
		
		public boolean wasRequestCancelled() {
			if(mCancelRequestListener != null) {
				return mCancelRequestListener.wasRequestCancelled();
			}
			
			return false;
		}
	}
	
	@SuppressWarnings("serial")
	public static class ServerResponseException extends Exception {};
	
	public static abstract class JSONArrayResponseListener extends ResponseListener {
		public JSONArrayResponseListener(ErrorResponseListener errorListener, CancelRequestListener cancelListener) {
			super(errorListener, cancelListener);
		}

		public abstract void onResponse(JSONArray array) throws ServerResponseException, JSONException;
	}
	
	public static abstract class JSONObjectResponseListener extends ResponseListener {
		public JSONObjectResponseListener(ErrorResponseListener errorListener, CancelRequestListener cancelListener) {
			super(errorListener, cancelListener);
		}

		public abstract void onResponse(JSONObject object) throws ServerResponseException, JSONException;
	}
	
	public static abstract class RawResponseListener extends ResponseListener {
		public RawResponseListener(ErrorResponseListener errorListener, CancelRequestListener cancelListener) {
			super(errorListener, cancelListener);
		}

		public abstract void onResponse(InputStream stream);
	}
	
	
	// convenience functions useful to send messages
	// back into the the UI thread
	public static void sendErrorMessage(Handler uiHandler) {
		Message message = Message.obtain();
		message.arg1 = MobileWebApi.ERROR;
		uiHandler.sendMessage(message);
	}
	
	public static void sendCancelMessage(Handler uiHandler) {
		Message message = Message.obtain();
		message.arg1 = MobileWebApi.CANCELLED;
		uiHandler.sendMessage(message);
	}
	
	public static void sendSuccessMessage(Handler uiHandler) {
		sendSuccessMessage(uiHandler, null);
	}
	
	public static void sendSuccessMessage(Handler uiHandler, Object userData) {
		Message message = Message.obtain();
		message.arg1 = MobileWebApi.SUCCESS;
		message.obj = userData;
		uiHandler.sendMessage(message);
	}
	
	// the most common type of behavior need for handling network errors
	// either ignore it, or let the UI thread know about it
	public static class DefaultErrorListener implements ErrorResponseListener {
		Handler mUIHandler;
		public DefaultErrorListener(Handler uiHandler) {
			mUIHandler = uiHandler;
		}
		
		@Override
		public void onError() {
			sendErrorMessage(mUIHandler);
		}
	}
	
	public static class IgnoreErrorListener implements ErrorResponseListener {		
		@Override
		public void onError() {
			// do nothing
		}
	}
	
	public static class DefaultCancelRequestListener extends CancelRequestListener {
		Handler mUIHandler;
		public DefaultCancelRequestListener(Handler uiHandler) {
			mUIHandler = uiHandler;
		}
		
		@Override
		public void onCancel() {
			sendCancelMessage(mUIHandler);
		}		
	}
	
	private LoadingDialogType mLoadingDialogType;
	private boolean mShowErrors;
	private Context mContext = null;
	private Handler mUIHandler = null;
	private boolean mIsSearchQuery = false;
	
	public MobileWebApi(boolean showLoading, boolean showErrors, String errorTitle, Context context, Handler UIHandler) {
		mShowErrors = showErrors;
		mLoadingDialogType = LoadingDialogType.Generic;
		
		if(mShowErrors) {
			if(context == null) {
				throw new RuntimeException("Fatal error, context needs to be defined to show loading or errors");
			} 
		}	
		mContext = context;
		
		
		if(mShowErrors) {
			if(errorTitle == null) {
				throw new RuntimeException("Fatal error, errorTitle needs to be defined to show errors");
			}
			
			if(UIHandler == null) {
				throw new RuntimeException("Fatal error, uiHandler needs to be defined to show errors");
			}
			
			mUIHandler = UIHandler;
		}
		
		// set http client to DefaultHttpClient
		httpClientType = HttpClientType.Default;
	}
	
	public MobileWebApi(boolean showLoading, boolean showErrors, String errorTitle, Context context, Handler UIHandler, HttpClientType httpClientType) {
		mShowErrors = showErrors;
		mLoadingDialogType = LoadingDialogType.Generic;
		
		if(mShowErrors) {
			if(context == null) {
				throw new RuntimeException("Fatal error, context needs to be defined to show loading or errors");
			} 
		}	
		mContext = context;
		
		
		if(mShowErrors) {
			if(errorTitle == null) {
				throw new RuntimeException("Fatal error, errorTitle needs to be defined to show errors");
			}
			
			if(UIHandler == null) {
				throw new RuntimeException("Fatal error, uiHandler needs to be defined to show errors");
			}
			
			mUIHandler = UIHandler;
		}
		
		// set http client to DefaultHttpClient or MITClient
		this.httpClientType = httpClientType;
		Log.d(TAG,"httpClientType = " + this.httpClientType);

	}

	/*
	 * This will create a Mobile Web Api wrapper that is silent,
	 * i.e. does not display a loading message or error messages
	 */
	public MobileWebApi() {
		this(false, false, null, null, null);
	}
	
	public void setIsSearchQuery(boolean isSearchQuery) {
		mIsSearchQuery = true;
	}
	
	public void setLoadingDialogType(LoadingDialogType dialogType) {
		mLoadingDialogType = dialogType;
	}
	
	public static ProgressDialog MobileWebLoadingDialog(Context context, LoadingDialogType dialogType, final CancelRequestListener cancelListener) {
		ProgressDialog loadingDialog = new ProgressDialog(context);
		
		String loadingMessage;
		if(dialogType == LoadingDialogType.Generic) {
			loadingMessage = GENERIC_MESSAGE;
		} else if(dialogType == LoadingDialogType.Search) {
			loadingMessage = SEARCH_MESSAGE;
		} else {
			loadingMessage = GENERIC_MESSAGE;
		}
		
		loadingDialog.setMessage(loadingMessage);
		loadingDialog.setIndeterminate(true);
		if(cancelListener != null) {
			loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					cancelListener.notifyRequestCancelled();
					
				}
			});
		} else {
			loadingDialog.setCancelable(false);
		}
		
		return loadingDialog;
	}
	
	private boolean requestResponse(String path, Map<String, String> parameters, final ResponseType expectedType, final ResponseListener responseListener) {
		
		Log.d(TAG,"path = " + path);
		Log.d(TAG,"requestResponse");
		ConnectionInterface callback = new ConnectionInterface() {

			@Override
			public void onError(final ErrorType error) {
				Log.d(TAG,"requestResponse onError = " + error);
				if(responseListener.wasRequestCancelled()) {
					// nothing to handle request was cancelled
					return;
				}
				
				if(mShowErrors) {
					mUIHandler.post(new Runnable() {
						public void run () {
							String errorMessage = null;
							if(error == ErrorType.Network) {
								errorMessage = NETWORK_ERROR;
							} else if(error == ErrorType.Server) {
								errorMessage = SERVER_ERROR;
							}
							
							if(mIsSearchQuery) {
								errorMessage = SEARCH_ERROR_MESSAGE;
							}
							//Log.d("MobileWebApi","requestResponse() error");
							///Log.d("MobileWebApi",errorMessage);
							Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
						}
					});
				}
				
				responseListener.onError();
			}
			
			@Override
			public void onResponse(final InputStream stream) {
				if(responseListener.wasRequestCancelled()) {
					return;
				}
				
				if (Looper.myLooper() != null) {
					final Handler responseHandler = new Handler();
					new Thread() {
						
						@Override
						public void run() {
							String responseText = null;
							switch(expectedType) {
								case JSONObject: 
								case JSONArray:
									responseText = convertStreamToString(stream);
									break;
							}
							final String finalResponseText = responseText;
							responseHandler.post(new Runnable() {
								@Override
								public void run() {
									processResponse(finalResponseText, stream);
								}
							});
						}
					}.start();
				} else {
					String responseText = null;
					switch(expectedType) {
						case JSONObject: 
						case JSONArray:
							responseText = convertStreamToString(stream);
							break;
					}
					processResponse(responseText, stream);				
				}
			}
			
			private void processResponse(String responseText, InputStream stream) {
				try {
					switch(expectedType) {
					case JSONObject:
						JSONObjectResponseListener objectResponseListener = (JSONObjectResponseListener) responseListener;
						objectResponseListener.onResponse(new JSONObject(responseText));
						break;
						
					case JSONArray:
						JSONArrayResponseListener arrayResponseListener = (JSONArrayResponseListener) responseListener;
						arrayResponseListener.onResponse(new JSONArray(responseText));
						break;
						
					case Raw:
						RawResponseListener rawResponseListener = (RawResponseListener) responseListener;
						rawResponseListener.onResponse(stream);	
						break;
					}	
				} catch (JSONException e) {
					e.printStackTrace();
					onError(ErrorType.Server);										
				} catch (ServerResponseException e) {
					e.printStackTrace();
					onError(ErrorType.Server);										
				}
			}
		};
		
		ConnectionWrapper connection;
		if(mContext != null) {
			if (httpClientType == HttpClientType.MIT) {
				Log.d(TAG,"httpClientType == MIT");
				connection = new MITConnectionWrapper(mContext,httpClientType);
				Log.d(TAG,"class of connection = " +connection.getClass() );
			}
			else {
				connection = new ConnectionWrapper(mContext);
				Log.d(TAG,"httpClientType != MIT");
			}
		} else {
			Log.d(TAG,"mContext is null");
			connection = new ConnectionWrapper();
		}
		
		String urlString = "http://" + Global.getMobileWebDomain() + BASE_PATH + path + "/?" + query(parameters);
		Log.d(TAG, "requesting " + urlString);
		boolean isStarted = connection.openURL(urlString, callback);
		
		return isStarted;
	}
	
	public boolean requestJSONObject(String path, Map<String, String> parameters, JSONObjectResponseListener responseListener) {
		Log.d(TAG,"json 1 - path = " + path);
		return requestResponse(path, parameters, ResponseType.JSONObject, responseListener);
	}
	
	public boolean requestJSONObject(Map<String, String> parameters, JSONObjectResponseListener responseListener) {
		Log.d(TAG,"json 2 module = " + parameters.get("module"));
		return requestJSONObject("", parameters, responseListener);
	}
	
	public boolean requestJSONArray(String path, Map<String, String> parameters, JSONArrayResponseListener responseListener) {
		return requestResponse(path, parameters, ResponseType.JSONArray, responseListener);
	}
	
	public boolean requestJSONArray(Map<String, String> parameters, JSONArrayResponseListener responseListener) {
		return requestJSONArray("", parameters, responseListener);
	}	
	
	public boolean requestRaw(String path, Map<String, String> parameters, RawResponseListener responseListener) {
		return requestResponse(path, parameters, ResponseType.Raw, responseListener);
	}
	
	public static String query(Map<String, String> parameters) {
		String query = "";
		for(String key : parameters.keySet()) {
			try {
				if(query.length() > 0) {
					query += "&";
				}
				query += key + "=" + java.net.URLEncoder.encode(parameters.get(key), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return query;
	}
	
    public static String convertStreamToString(InputStream stream) {
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder stringBuilder = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }	
}
