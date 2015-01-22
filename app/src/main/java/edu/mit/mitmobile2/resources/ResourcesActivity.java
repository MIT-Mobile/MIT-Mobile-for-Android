package edu.mit.mitmobile2.resources;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;

public class ResourcesActivity extends MITModuleActivity {
	private ListView resourceList;
	ArrayAdapter<String> resourceAdapter;
	MITAPIClient resource = new MITAPIClient(this.mContext);
	
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
		
		resource.getJson("resource",null,null,resourceHandler);
	}
	
	Handler resourceHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
	    	try {	    		
				JSONObject jObject = (JSONObject)msg.obj;
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
		}
		
	};
		
}
