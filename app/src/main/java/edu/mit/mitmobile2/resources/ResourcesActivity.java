package edu.mit.mitmobile2.resources;

import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import edu.mit.mitmobile2.maps.MITMapView;
import edu.mit.mitmobile2.maps.MapItem;
import edu.mit.mitmobile2.maps.MapsActivity;


public class ResourcesActivity extends MapsActivity {
	private ListView resourceListView;
    private ArrayList resourceList;
    private TextView resourceInfoText;
  	ResourceRowAdapter resourceAdapter;
    ArrayAdapter<String> arrayAdapter;
	MITAPIClient resource = new MITAPIClient(this.mContext);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        this.setMapContentLayoutId(R.layout.content_resources);
        this.hasSearch = true;
        super.onCreate(savedInstanceState);
        //slidingUpPanelLayout.expandPanel();
        resourceInfoText = (TextView)findViewById(R.id.resourceInfoText);
        resourceListView = (ListView) findViewById(R.id.resourceListView);
        //slidingUpPanelLayout.setDragView(resourceListView);

//        resourceListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//
//            private int mInitialScroll = 0;
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                Log.d("ZZZ", "first = " + firstVisibleItem);
//                Log.d("ZZZ", "visible = " + visibleItemCount);
//                Log.d("ZZZ", "total = " + totalItemCount);
//                if (firstVisibleItem > 0) {
//                    mInitialScroll = firstVisibleItem;
//
//                    contentViewStub.animate();
//
//                    mapView.hide();
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
//                    params.weight = 1;
//                    contentViewStub.setLayoutParams(params);
//                }
//            }
//
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//
//            }
//        });
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

                if (items.length() > 0) {
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        ResourceItem r = new ResourceItem();
                        r.setNumber(i + 1);
                        r.setMarkerText(r.getNumber() + "");
                        r.setName(item.getString("name"));
                        r.setRoom(item.getString("room"));
                        String[] parts = r.getRoom().split("-");
                        r.setBuilding(parts[0]);

                        // add a building header before the first resource in a building
                        if (!r.getBuilding().equals(previousBuilding)) {
                            ResourceItem rh = new ResourceItem();
                            rh.setBuildingHeader(true);
                            rh.setMapItemType(0); // not a map item
                            rh.setBuilding(parts[0]);
                            resourceList.add(rh);
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

                    if (mapView != null) {
                        mapView.addMapItemList(resourceList);
                        //mapView.fitMapItems();
                    }

                    resourceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            // TODO Auto-generated method stub
                            Log.d("ZZZ", "Items " + arg2);
                            ResourceItem r = (ResourceItem) resourceList.get(arg2);

                            // If the item is a building header, do nothing
                            if (!r.getBuildingHeader()) {
                                Intent i = new Intent(mContext, ResourceViewActivity.class);
                                i.putExtra("resourceItem", r);
                                startActivity(i);
                            }
                        }
                    });

                    resourceListView.setVisibility(View.VISIBLE);
                    //slidingUpPanelLayout.setPanelHeight(300);
                    //slidingUpPanelLayout.setAnchorPoint(0.5f);

                }
                else {
                    Toast.makeText(mContext, "No resources were found for your search criteria",Toast.LENGTH_SHORT).show();
                }

                findViewById(R.id.resourceInfoText).setVisibility(View.GONE);
                mapView.show();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	};
		
}
