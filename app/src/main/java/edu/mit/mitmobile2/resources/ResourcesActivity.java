package edu.mit.mitmobile2.resources;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;


public class ResourcesActivity extends MITModuleActivity {
	private ListView resourceListView;
    private List resourceList;
    private String[] test;
    private TextView resourceInfoText;
  	ResourceRowAdapter resourceAdapter;
    ArrayAdapter<String> arrayAdapter;
	MITAPIClient resource = new MITAPIClient(this.mContext);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setContentLayoutId(R.layout.content_resources);
        this.hasSearch = true;
		super.onCreate(savedInstanceState);
		Log.d("ZZZ","on create");

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("ZZZ","do search");
        }

        resourceInfoText = (TextView)findViewById(R.id.resourceInfoText);
        resourceListView = (ListView) findViewById(R.id.resourceListView);
	}

	@Override
	protected void onResume() {
	
		// TODO Auto-generated method stub
		super.onResume();
		Log.d("ZZZ","onResume");	
		// Initialize the resource proxy.
		Log.d("ZZZ","create client");
	}

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            handleSearch(query);
        }
    }

    protected boolean handleSearch(String query) {
        Map params = new HashMap<String,String>();
        params.put("q",query);
        resource.getJson("resource",null,params,resourceHandler);
        return true;
    }

    Handler resourceHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
	    	try {	    		
				JSONObject jObject = (JSONObject)msg.obj;
				JSONObject collection = jObject.getJSONObject("collection");
				JSONArray items = collection.getJSONArray("items");

                resourceList = new ArrayList<ResourceItem>();
                //test = new String[items.length()];
				for (int i = 0; i < items.length(); i++) {
					JSONObject item = items.getJSONObject(i);
                    ResourceItem r = new ResourceItem();
                    r.setNumber(i + 1);
                    r.setName(item.getString("name"));
                    //test[i] = item.getString("name");
                    r.setRoom(item.getString("room"));
                    r.setStatus(item.getString("status"));
                    resourceList.add(r);
				}

                //arrayAdapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1, android.R.id.text1, test);

				resourceAdapter = new ResourceRowAdapter(mContext, R.id.row_resource, resourceList);
				resourceListView.setAdapter(resourceAdapter);


                resourceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        Log.d("ZZZ","Items " +  arg2);
                    }
                });



                //resourceListView.setAdapter(arrayAdapter);
                resourceInfoText.setVisibility(View.GONE);
                resourceListView.setVisibility(View.VISIBLE);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	};
		
}
