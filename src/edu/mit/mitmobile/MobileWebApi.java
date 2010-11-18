package edu.mit.mitmobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile.ConnectionWrapper.ConnectionInterface;
import edu.mit.mitmobile.ConnectionWrapper.ErrorType;
import edu.mit.mitmobile.about.BuildSettings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class MobileWebApi {

	public static final String BASE_PATH = "/api";
	public static enum ResponseType {
		JSONObject,
		JSONArray,
		Raw
	};
	
	public static enum LoadingDialogType {
		Generic,
		Search
	}
	
	public static int ERROR = 0;
	public static int SUCCESS = 1;
	public static int CANCELLED = 2;
	
	public final static String NETWORK_ERROR = "There was a problem connecting to the network.";
	public final static String SERVER_ERROR = "There was a problem connecting to the server.";
	public final static String SEARCH_ERROR_MESSAGE = "There was a problem loading the search results. Please try again.";
	
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
	
	private boolean mShowLoading;
	private LoadingDialogType mLoadingDialogType;
	private boolean mShowErrors;
	private Context mContext = null;
	private Handler mUIHandler = null;
	private boolean mIsSearchQuery = false;
	
	public MobileWebApi(boolean showLoading, boolean showErrors, String errorTitle, Context context, Handler UIHandler) {
		mShowLoading = showLoading;
		mShowErrors = showErrors;
		mLoadingDialogType = LoadingDialogType.Generic;
		
		if(mShowLoading || mShowErrors) {
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
		final LoadingProgressDialog loadingDialog = new LoadingProgressDialog(responseListener.getCancelRequestListener());
		
		ConnectionInterface callback = new ConnectionInterface() {

			@Override
			public void onError(final ErrorType error) {
				if(responseListener.wasRequestCancelled()) {
					// nothing to handle request was cancelled
					return;
				}
				
				if(mShowLoading) {
					loadingDialog.dismiss();
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
							
							Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
						}
					});
				}
				
				responseListener.onError();
			}
			
			@Override
			public void onResponse(InputStream stream) {
				if(responseListener.wasRequestCancelled()) {
					return;
				}
				
				try {
					String responseText = null;
					switch(expectedType) {
						case JSONObject:
							responseText = convertStreamToString(stream);
							JSONObjectResponseListener objectResponseListener = (JSONObjectResponseListener) responseListener;
							objectResponseListener.onResponse(new JSONObject(responseText));
							break;
						case JSONArray:
							responseText = convertStreamToString(stream);
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
					responseListener.onError();
					onError(ErrorType.Server);
				} catch (ServerResponseException e) {
					e.printStackTrace();
					onError(ErrorType.Server);
				}
				
				if(mShowLoading) {
					loadingDialog.dismiss();
				}
			}
			
		};
		
		ConnectionWrapper connection;
		if(mContext != null) {
			connection = new ConnectionWrapper(mContext);
		} else {
			connection = new ConnectionWrapper();
		}
		
		if(mShowLoading) {
			loadingDialog.show();
		}
		
		String urlString = "http://" + BuildSettings.MOBILE_WEB_DOMAIN + BASE_PATH + path + "/?" + query(parameters);
		Log.d("MobileWebAPI", "requesting " + urlString);
		boolean isStarted = connection.openURL(urlString, callback);

		if(!isStarted && mShowLoading) {
			// this will prevent the dialog from being shown at
			// if connection did not succeed at starting
			loadingDialog.dismiss();
		}
		
		return isStarted;
	}
	
	public boolean requestJSONObject(String path, Map<String, String> parameters, JSONObjectResponseListener responseListener) {
		return requestResponse(path, parameters, ResponseType.JSONObject, responseListener);
	}
	
	public boolean requestJSONObject(Map<String, String> parameters, JSONObjectResponseListener responseListener) {
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
	
    private static String convertStreamToString(InputStream stream) {
        
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
    
    /*
     * A Wrapper around androids builtin Progress Dialog
     * this wrapper is needs to detach the progress dialog from the current thread
     * and to make sure it runs on the UI thread
     */
    private class LoadingProgressDialog {
    	private ProgressDialog mProgressDialog = null;
    	private boolean mDialogShouldDisplay = false;
    	private CancelRequestListener mCancelRequestListener;
    	
    	LoadingProgressDialog(CancelRequestListener cancelListener) {
    		mCancelRequestListener = cancelListener;
    	}
    	
    	
    	void show() {
    		mDialogShouldDisplay = true;
    		
    		mUIHandler.post(new Runnable() {
				@Override
				public void run() {	
					if(mDialogShouldDisplay) {
						mProgressDialog = MobileWebApi.MobileWebLoadingDialog(mContext, mLoadingDialogType, mCancelRequestListener);
						mProgressDialog.show();
					}
				}});
    	}
    	
    	void dismiss() {
    		mDialogShouldDisplay = false;
    		
    		mUIHandler.post(new Runnable() {
    			@Override
    			public void run() {
    				if(mProgressDialog != null) {
    					mProgressDialog.dismiss();
    				}
    			}
    		});
    	}
    }
}
