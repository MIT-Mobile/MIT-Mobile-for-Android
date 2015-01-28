package edu.mit.mitmobile2.resources;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                String previousBuilding = "";

                //test = new String[items.length()];
				for (int i = 0; i < items.length(); i++) {
					JSONObject item = items.getJSONObject(i);
                    ResourceItem r = new ResourceItem();
                    r.setNumber(i + 1);
                    r.setName(item.getString("name"));
                    r.setRoom(item.getString("room"));
                    String[] parts = r.getRoom().split("-");
                    r.setBuilding(parts[0]);

                    // show the building header for the first resource in a building

                    if (!r.getBuilding().equals(previousBuilding)) {
                        r.setShowBuilding(true);
                        previousBuilding = r.getBuilding();
                    }

                    if (item.has("latitude")) {
                        r.setLatitude(item.getDouble("latitude"));
                    }
                    if (item.has("longitude")) {
                        r.setLongitude(item.getDouble("longitude"));
                    }
                    r.setStatus(item.getString("status"));

                    JSONArray jAttributes = item.getJSONObject("_template").getJSONArray("attributes");
                    r.setAttributes(new ArrayList());
                    for (int a = 0; a < jAttributes.length(); a++) {
                        JSONObject jAttribute = jAttributes.getJSONObject(a);
                        ResourceAttribute attribute = new ResourceAttribute();
                        attribute.set_attribute(jAttribute.getString("_id"));
                        attribute.setLabel(jAttribute.getString("label"));
                        JSONArray jValue = jAttribute.getJSONArray("value");
                        attribute.setValue(new ArrayList<String>());
                        for (int v = 0; v < jValue.length(); v++) {
                            attribute.getValue().add(jValue.getString(v));
                        }
                        attribute.setValue_id(jAttribute.getString("value_id"));
                        r.getAttributes().add(attribute);
                    }

                    resourceList.add(r);
				}

				resourceAdapter = new ResourceRowAdapter(mContext, R.id.row_resource, resourceList);
				resourceListView.setAdapter(resourceAdapter);


                resourceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        Log.d("ZZZ","Items " +  arg2);
                        ResourceItem r = (ResourceItem)resourceList.get(arg2);
                        Intent i = new Intent(mContext,ResourceViewActivity.class);
                        i.putExtra("resourceItem",r);
                        startActivity(i);
                    }
                });



                //resourceListView.setAdapter(arrayAdapter);
                findViewById(R.id.resourceInfo).setVisibility(View.GONE);
                resourceListView.setVisibility(View.VISIBLE);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	};
		
}
