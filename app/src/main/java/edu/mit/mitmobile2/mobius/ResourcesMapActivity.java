package edu.mit.mitmobile2.mobius;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.mit.mitmobile2.APIJsonResponse;
import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MapsActivity;

public class ResourcesMapActivity extends MapsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        this.hasSearch = true;
        super.onCreate(savedInstanceState);
        Log.d("ZZZ","tag = " + TAG);
    }

    @Override
    protected Handler getMapItemHandler() {
        Log.d(TAG, "getMapItemHandler()");
        this.handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                try {
                    APIJsonResponse response = (APIJsonResponse) msg.obj;
                    JSONObject collection = response.jsonArray.getJSONObject(0).getJSONObject("collection");
                    JSONArray items = collection.getJSONArray("items");

                    mapItems = new ArrayList<ResourceItem>();
                    String previousBuilding = "";

                    if (items.length() > 0) {
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            ResourceItem r = new ResourceItem(mContext, (ViewGroup) findViewById(R.id.map));
                            r.setNumber(i + 1);
                            //r.setMarkerText(r.getNumber() + "");
                            r.setName(item.getString("name"));
                            r.setRoom(item.getString("room"));
                            String[] parts = r.getRoom().split("-");
                            r.setBuilding(parts[0]);

                            // add a building header before the first resource in a building
                            if (!r.getBuilding().equals(previousBuilding)) {
                                ResourceItem rh = new ResourceHeader();
                                rh.setBuildingHeader(true);
                                rh.setMapItemType(0); // not a map item
                                rh.setBuilding(parts[0]);
                                mapItems.add(rh);
                                previousBuilding = r.getBuilding();
                            }

                            // store the index of the map item in the mapItems list
                            r.setMapItemIndex(mapItems.size());

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

                            mapItems.add(r);
                        }

                        displayMapItems();

                    } else {
                        Toast.makeText(mContext, "No resources were found for your search criteria", Toast.LENGTH_SHORT).show();
                    }
                    hideProgressBar();

                    //findViewById(R.id.resourceInfoText).setVisibility(View.GONE);
                    mapView.show();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        };

        return handler;
    }


    @Override
    protected void getMapItems(Map params) {
        Log.d(TAG, "getMapItems()");
        this.showProgressBar();
        this.apiClient.getJson(Constants.RESOURCES,null,params,this.getMapItemHandler());
    }

    @Override
    protected ArrayAdapter getMapItemAdapter() {
        return new ResourceRowAdapter(mContext, R.id.row_resource, this.mapItems);
    }

    @Override
    protected AdapterView.OnItemClickListener getOnItemClickListener() {

        return new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                viewMapItem( (int)id);
            }
        };
    }

    @Override
    protected GoogleMap.InfoWindowAdapter getInfoWindowAdapter() {
        Log.d(TAG, "getInfoWindowAdapter()");
        return new ResourceItemInfoWindow(this.mContext);
    }

    protected GoogleMap.OnInfoWindowClickListener getOnInfoWindowClickListener() {
        return new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.d(TAG,"info window clicked");
                Log.d(TAG,"snippet =" + marker.getSnippet());
                try {
                    JSONObject data = new JSONObject(marker.getSnippet());
                    int index = data.getInt("mapItemIndex");
                    viewMapItem(index);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    @Override
    protected boolean handleSearch(String query) {
        Log.d(TAG, "handleSearch()");
        if (query != null) {
            Map params = new HashMap<String, String>();
            params.put("q", query);
            this.getMapItems(params);
        }
        return true;
    }

    protected void viewMapItem(int mapItemIndex) {
        Log.d(TAG,"map item index = " + mapItemIndex);
        ResourceItem r = (ResourceItem) mapItems.get(mapItemIndex);

        // If the item is a building header, do nothing
        if (!r.getBuildingHeader()) {
            Intent i = new Intent(mContext, ResourceViewActivity.class);
            i.putExtra("resourceItem", r);
            startActivity(i);
        }
    }
}
