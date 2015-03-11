package edu.mit.mitmobile2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class MITAPIClient {

	protected static Map<String,APIEntry> api;
	public static final String DEV = "dev";
	public static final String TEST = "test";
	public static final String PROD = "prod";
	public static final String DEFAULT_ENVIRONMENT = "dev";
	public static String environment = MITAPIClient.DEFAULT_ENVIRONMENT;
	public static int API_SUCCESS = 1;
	public static int API_ERROR = 0;
    protected static final int FORMAT_DEFAULT = 0;
    protected static final int FORMAT_HTML = 1;
    protected static final int FORMAT_JSON = 2;

	protected Context mContext;
	protected AsyncHttpClient client ;

	public MITAPIClient(Context mContext) {
		super();
		this.mContext = mContext;
		// TODO Auto-generated constructor stub
	}

    public static void init(Context mContext) {

		String json = "";
		// read the api.json file to populate the api map the first time this class is initiated
		if (MITAPIClient.api == null) {
			MITAPIClient.api = new HashMap<String,APIEntry>();

	        try {
	        	InputStream is = mContext.getAssets().open("api.json");
	        	int size = is.available();
	        	byte[] buffer = new byte[size];
	        	is.read(buffer);
	        	is.close();
	        	json = new String(buffer, "UTF-8");
	        	Log.d("API",json);
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }

	        try {
	        	JSONObject jObject = new JSONObject(json);

	        	Iterator n = jObject.keys();
	        	List<String> keysList = new ArrayList<String>();
	              while (n.hasNext()) {
	              	try {
	              		String key = (String)n.next();
	              		JSONObject api = jObject.getJSONObject(key);
	              		APIEntry apiEntry = new APIEntry();
	              		apiEntry.setDev(api.getString("dev"));
	              		apiEntry.setTest(api.getString("test"));
	              		apiEntry.setProd(api.getString("prod"));
	              		Log.d("API","add api entry for " + key);
	              		MITAPIClient.api.put(key, apiEntry);
	              	}
	              	catch (JSONException e) {
	    	        	Log.d("API",e.getMessage().toString());
	              	}
	              }
	        }
	        catch (Exception e) {
	        	Log.d("API",e.getMessage().toString());
	        }


		}
	}

	public void setEnvironment(String env) {
		MITAPIClient.environment = env;
	}

    public void get(String api,String path,Map<String,String> params, final Handler apiHandler, final int format) {
		this.client = new AsyncHttpClient();
        client.setEnableRedirects(true);

		// get the url of the api from the api key and the environment
		APIEntry apiEntry = MITAPIClient.api.get(api);
		String apiUrl = apiEntry.getBaseUrl(MITAPIClient.environment);
		if (path != null) {
			apiUrl += path;
		}

		if (params != null) {
			String queryString = "";
			Iterator it = params.entrySet().iterator();
			while (it.hasNext()) {
				 Map.Entry pairs = (Map.Entry)it.next();
				 if (queryString.equals("")) {
					 apiUrl += "?";
				 }
				 else {
					 queryString += "&";
				 }
				 queryString += pairs.getKey() + "=" + pairs.getValue();
			}
			apiUrl += queryString;
            Log.d("API","api url = " + apiUrl);
		}

		// Client GET
		client.get(apiUrl, new AsyncHttpResponseHandler() {

		    @Override
		    public void onStart() {
		        // called before request is started
		    	Log.d("ZZZ","start");
		    }

		    @Override
		    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				JSONObject jObject = new JSONObject();
		        // called when response HTTP status is "200 OK"
		    	Log.d("API","success");
		    	// Code to convert byte arr to str:
		    	String res = new String(response);
		    	try {
					jObject = new JSONObject(res);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


		    	Message apiMessage = new Message();
		    	apiMessage.arg1 = MITAPIClient.API_SUCCESS;
		    	apiMessage.arg2 = statusCode;
		    	apiMessage.obj = buildResponse(statusCode,headers,response,format);
		    	apiHandler.sendMessage(apiMessage);
		    	Log.d("API",res);
		    }

		    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
		        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
		    	Log.d("API","failure");
		    	Log.d("API","status = " + statusCode);
		    	Message apiMessage = new Message();
		    	apiMessage.arg1 = MITAPIClient.API_ERROR;
		    	apiMessage.arg2 = statusCode;
		    	apiMessage.obj = null;
		    	apiHandler.sendMessage(apiMessage);
		    }

		    public void onRetry(int retryNo) {
		        // called when request is retried
		    	Log.d("API","retry");
			}
		});

	}

    public void getJson(String api,String path,Map<String,String> params, final Handler apiHandler) {
        get(api, path, params, apiHandler, FORMAT_JSON);
    }

    private Object buildResponse(int statusCode, Header[] headers, byte[] response, int format) {

        APIResponse r  = null;

        Message apiMessage = new Message();
        apiMessage.arg1 = MITAPIClient.API_SUCCESS;
        apiMessage.arg2 = statusCode;

        switch(format) {

            case FORMAT_HTML:
                r = new APITextResponse(statusCode,headers,response);
            break;

            case FORMAT_JSON:
                r = new APIJsonResponse(statusCode,headers,response);
            break;

            case FORMAT_DEFAULT:
                r = new APIResponse();
                r.statusCode = statusCode;
                r.headers = headers;
                r.response = response;
            break;
        }

        return r;
    }

    public void get(String api, String path, HashMap<String,String> pathParams, HashMap<String,String> queryParams, Object callback) {
        APIEntry apiEntry = MITAPIClient.api.get(api);
        String apiUrl = apiEntry.getBaseUrl(MITAPIClient.environment);
        RetrofitManager.changeEndpoint(apiUrl);
        RetrofitManager.makeHttpCall(api, path, pathParams, queryParams, callback);
    }

    public Object get(String api, String path, HashMap<String,String> pathParams, HashMap<String,String> queryParams) {
        APIEntry apiEntry = MITAPIClient.api.get(api);
        String apiUrl = apiEntry.getBaseUrl(MITAPIClient.environment);
        RetrofitManager.changeEndpoint(apiUrl);
        Object o = RetrofitManager.makeHttpCall(api, path, pathParams, queryParams);
        return o;
    }

}
