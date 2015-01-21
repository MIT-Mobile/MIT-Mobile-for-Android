package edu.mit.mitmobile2.resources;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;

public class ResourcesActivity extends MITModuleActivity {
	ListView resourceList;
	ArrayAdapter<String> resourceAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setContentLayoutId(R.layout.content_resources);
		super.onCreate(savedInstanceState);
		Log.d("ZZZ","on create");
		resourceList = (ListView) findViewById(R.id.resourceList);
	}

	@Override
	protected void onResume() {
	
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("ZZZ","onResume");	
		// Initialize the resource proxy.
		Log.d("ZZZ","create client");
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://kairos-dev.mit.edu:3000/resource/", new AsyncHttpResponseHandler() {

		    @Override
		    public void onStart() {
		        // called before request is started
		    	Log.d("ZZZ","start");
		    }

		    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
		        // called when response HTTP status is "200 OK"
		    	Log.d("ZZZ","success");
		    	// Code to convert byte arr to str:
		    	String str1 = new String(response);
		    	try {
					JSONObject jObject = new JSONObject(str1);
					JSONObject collection = jObject.getJSONObject("collection");
					JSONArray items = collection.getJSONArray("items");
					
					String[] tmp = new String[items.length()];
					for (int i = 0; i < items.length(); i++) {
						JSONObject item = items.getJSONObject(i);
						tmp[i] = item.getString("name");
					}
					
					ArrayAdapter<String> resourceAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, tmp);
					resourceList.setAdapter(resourceAdapter);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	Log.d("ZZZ",str1);
		    }

		    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
		        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
		    	Log.d("ZZZ","failure");
		    	Log.d("ZZZ","status = " + statusCode);
		    }

		    public void onRetry(int retryNo) {
		        // called when request is retried
		    	Log.d("ZZZ","retry");
			}
		});

	}
		
}
