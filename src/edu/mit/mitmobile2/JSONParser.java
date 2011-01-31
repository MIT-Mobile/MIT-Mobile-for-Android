package edu.mit.mitmobile2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.about.BuildSettings;

import android.os.Handler;
import android.util.Log;

public abstract class JSONParser {
	
	private static final String HTTP_USER_AGENT = 
		"MIT Mobile " + BuildSettings.VERSION_NUMBER + " for Android";
	
	public boolean expectingObject = false;
	
	public abstract String getBaseUrl();
	
	protected static String result;
	
	public List items;
	
	public static JSONObject jItem;
	
    public static String convertStreamToString(InputStream is) {
      
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    /***********************************************************/
    public void getJSONThread(final String params, final Handler h) {


    	Thread thread = new Thread() {
    		@Override
    		public void run() {
    			Log.d("JSONParser","url: "+getBaseUrl()+params);
    			getJSON(getBaseUrl()+params,expectingObject);
    			saveData();  // callback...
    			if (h!=null) h.sendEmptyMessage(0);
    		}
    	};
    	thread.start();
    }

    /***********************************************************/
    public void saveData() {
    	
    }
    /***********************************************************/
    public  void getJSON(String url, boolean expectObj)
    {
 
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url); 
        httpget.setHeader("User-Agent", HTTP_USER_AGENT);
        HttpResponse response;
        
        try {
            response = httpclient.execute(httpget);
            
            // Response status
            Log.i("JSONParser",response.getStatusLine().toString());
 
            HttpEntity entity = response.getEntity();
 
            if (entity != null) {
 
                InputStream is = entity.getContent();
                
                parse(is,expectObj);
                
            }
 
 
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
	public List parse(InputStream is, boolean expectObj) throws IOException, JSONException {

        JSONArray jArray = null;
        
    	result = convertStreamToString(is);
        Log.d("JSONParser","JSONParser: " + result);

        // self:  array = [] and each {} within is object
      
        // jItem is used by subclasses
        
        if (expectObj) {
        	
        	jItem = new JSONObject(result);
        	 parseObj();
        	 
        } else {
            
            jArray = new JSONArray(result);
            for(int i=0; i<jArray.length(); i++)
            {
            	jItem = jArray.getJSONObject(i);
                parseObj();
            }

        }

        is.close();
        
		return items;
        
    }
    
    protected abstract void parseObj();
    
    
}


	

