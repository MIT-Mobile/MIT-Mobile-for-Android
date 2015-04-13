package edu.mit.mitmobile2.mobius;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.Clock;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.mit.mitmobile2.APIJsonResponse;
import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MapItem;
import edu.mit.mitmobile2.mobius.model.QuickSearch;
import edu.mit.mitmobile2.mobius.model.ResourceAttribute;
import edu.mit.mitmobile2.mobius.model.ResourceItem;
import edu.mit.mitmobile2.mobius.model.ResourceRoom;
import edu.mit.mitmobile2.mobius.model.RoomsetHours;
import edu.mit.mitmobile2.shuttles.callbacks.MapFragmentCallback;
import timber.log.Timber;

public class ResourceListActivity extends MITActivity implements MapFragmentCallback {

    ResourceListFragment fragment = new ResourceListFragment();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_with_frame);
        hasSearch = true;
        getSupportActionBar().setTitle("Machines");
        getFragmentManager().beginTransaction().replace(R.id.fragment_frame, fragment).commit();

        Intent intent = getIntent();
        if (intent.hasExtra("quicksearch")) {
            QuickSearch qs = (QuickSearch) intent.getExtras().getParcelable("quicksearch");
            getMapItems(qs);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Timber.d("new intent");
        super.onNewIntent(intent);
        setIntent(intent);
        fragment = new ResourceListFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_frame, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (!fragment.isMapViewExpanded()) {
            super.onBackPressed();
        } else {
            fragment.showListView();
        }
    }

    @Override
    public void setActionBarTitle(String title) {
        setTitle(title);
    }

    @Override
    public void setActionBarSubtitle(String subtitle) {
        getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    protected boolean handleSearch(String search) {
        return super.handleSearch(search);
    }

    protected void getMapItems(QuickSearch qs) {

        // parse the quicksearch into a params string
        try {
            JSONObject params = new JSONObject("{'where':[]}");

            if (qs.getType().equalsIgnoreCase("ROOMSET")) {
                JSONObject criteria = new JSONObject();
                criteria.put("field","roomset");
                criteria.put("value",qs.getValue());
                params.getJSONArray("where").put(criteria);
            } else if (qs.getType().equalsIgnoreCase("TYPE")) {
                JSONObject criteria = new JSONObject();
                criteria.put("field","_type");
                criteria.put("value",qs.getValue());
                params.getJSONArray("where").put(criteria);
            }

            Timber.d("calling get map items with params " + params.toString());
            getMapItems(params.toString());
        } catch (JSONException e) {
            Timber.d(e.getMessage());
        }

    }

    protected void getMapItems(String params) {
        HashMap paramsMap = null;
        if (params != null && !params.equalsIgnoreCase("")) {
            paramsMap = new HashMap<String, String>();
            paramsMap.put("params", params);
        }

        apiClient.getJson("resource", paramsMap, getHandler());
    }

    protected Handler getHandler() {

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    APIJsonResponse response = (APIJsonResponse) msg.obj;
                    JSONArray items = response.jsonArray;

                    String previousBuilding = "";

                    ArrayList mapItems = new ArrayList<MapItem>();
                    LinkedHashMap<String, ResourceRoom> roomMap = new LinkedHashMap<String, ResourceRoom>();
                    String roomKey = "";

                    // Get today's date to get today's shop hours
                    Clock clock = Clock.systemUTC();

                    ZonedDateTime zdt = ZonedDateTime.now(clock);

                    if (items.length() > 0) {

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            Timber.d("ID = " + item.optString("_id"));

                            if (item.has("room")) {
                                roomKey = item.getString("room");

                                // add the room to the roomMap if it is not defined
                                if (!roomMap.containsKey(roomKey)) {
                                    ResourceRoom resourceRoom = new ResourceRoom();
                                    if (item.has("latitude")) {
                                        try {
                                            Double lat = Double.parseDouble(item.getString("latitude"));
                                            resourceRoom.setLatitude(lat);
                                        }
                                        catch (NumberFormatException e) {
                                            Timber.d("latitude not defined for resource");
                                        }
                                    }
                                    else {
                                        resourceRoom.setLatitude(0);
                                    }
                                    if (item.has("longitude")) {
                                        try {
                                            Double lon = Double.parseDouble(item.getString("longitude"));
                                            resourceRoom.setLongitude(lon);
                                        }
                                        catch (NumberFormatException e) {
                                            Timber.d("longitude not defined for resource");
                                        }
                                    }
                                    else {
                                        resourceRoom.setLongitude(0);
                                    }
                                    resourceRoom.setRoom(item.getString("room"));
                                    resourceRoom.setRoomset_name(item.getJSONObject("roomset").optString("roomset_name"));
                                    resourceRoom.setRoom_label(resourceRoom.getRoomset_name() + " (" + resourceRoom.getRoom() + ")");

                                    resourceRoom.setHours(new ArrayList<RoomsetHours>());
                                    JSONArray hoursArray = item.getJSONArray("hours");
                                    for (int h = 0; h < hoursArray.length(); h++) {
                                        JSONObject hoursEntry = hoursArray.getJSONObject(h);

                                        //"start_date":"2015-04-09T13:00:00.000Z"
                                        // Get the date from the first 10 characters to see if the hours are for today
                                        String start_date = hoursEntry.getString("start_date");
                                        String end_date = hoursEntry.getString("end_date");
                                        if (zdt.toString().substring(0, 10).equalsIgnoreCase(start_date.toString().substring(0, 10))) {
                                            RoomsetHours roomsetHours = new RoomsetHours(hoursEntry.getString("start_time"),hoursEntry.getString("end_time"));
                                            resourceRoom.getHours().add(roomsetHours);
                                        }
                                        if (start_date.compareTo(zdt.toString()) <= 0 && zdt.toString().compareTo(end_date) <= 0) {
                                            resourceRoom.setOpen(true);
                                        }
                                    }

                                    resourceRoom.setResources(new ArrayList<ResourceItem>());

                                    roomMap.put(roomKey,resourceRoom);

                                }

                                // resource
                                ResourceItem r = new ResourceItem();
                                ResourceRoom rr = roomMap.get(roomKey);
                                r.setName(item.getString("name") + "");
                                Timber.d("add new resource " + r.getName());

                                r.setRoom(rr.getRoom());
                                r.setLongitude(rr.getLongitude());
                                r.setLatitude(rr.getLatitude());
                                r.set_category(item.getJSONObject("_category").optString("_id"));
                                r.setCategory(item.getJSONObject("_category").optString("category"));
                                r.set_type(item.getJSONObject("_type").optString("_id"));
                                r.setType(item.getJSONObject("_type").optString("type"));
                                r.set_template(item.optString("_template"));
                                r.setStatus(item.optString("status"));

                                // Images
                                if (item.has("_image")) {
                                    try {
                                        JSONArray jImages = item.getJSONArray("_image");
                                        r.setImages(new String[jImages.length()]);
                                        for (int j = 0; j < jImages.length(); j++) {
                                            r.getImages()[j] = jImages.getJSONObject(j).optString("small_id");
                                        }
                                    }
                                    catch (org.json.JSONException e) {
                                        Timber.d(e.getMessage());
                                    }
                                }

                                // Attributes
                                JSONArray jAttributes = item.getJSONArray("attribute_values");
                                for (int a = 0; a < jAttributes.length(); a++) {
                                    JSONObject jAttribute = jAttributes.getJSONObject(a);

                                    // only add attributes with labels - missing labels should only occur in dev
                                    if (jAttribute.has("label")) {
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
                                }

                                roomMap.get(r.getRoom()).getResources().add(r);

                            }

                        }

                        // loop through the roomMap and create the mapItems list with rooms and resources
                        Iterator it = roomMap.entrySet().iterator();
                        int mapIndex = 1;
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            ResourceRoom rr = (ResourceRoom)pair.getValue();
                            // set the map index of the room
                            rr.setMapItemIndex(mapIndex);

                            // add the room to mapItems
                            mapItems.add(rr);

                            // add resources from this room to mapItems
                            for (int i = 0; i < rr.getResources().size(); i++) {
                                mapItems.add(rr.getResources().get(i));
                            }
                            it.remove(); // avoids a ConcurrentModificationException
                            mapIndex++;
                        }

                        fragment.updateAndShowMapItems(mapItems);
                    } else {
                        Toast.makeText(context, "No resources were found for your search criteria", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        return handler;
    }

}