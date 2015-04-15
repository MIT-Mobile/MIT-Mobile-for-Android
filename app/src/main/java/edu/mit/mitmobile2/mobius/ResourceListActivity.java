package edu.mit.mitmobile2.mobius;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.Clock;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import edu.mit.mitmobile2.APIJsonResponse;
import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MapItem;
import edu.mit.mitmobile2.mobius.model.DisplayHours;
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
    HashMap<String,ArrayList<RoomsetHours>> hoursMap = new HashMap<>();
    LinkedHashMap<String, ResourceRoom> roomMap = new LinkedHashMap<String, ResourceRoom>();

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
        else if (intent.hasExtra("params")) {
            String params  = intent.getExtras().getString("params");
            getMapItems(params);
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
        if (search != null) {
            HashMap<String,String> paramsMap = new HashMap();
            paramsMap.put("q",search);
            this.getMapItems(paramsMap);
            return true;
        }
        else {
            return false;
        }
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

    protected void getMapItems(HashMap<String,String> paramsMap) {
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


                    String roomKey = "";

                    // Get today's date to get today's shop hours
                    Clock clock = Clock.systemUTC();

                    ZonedDateTime zdt = ZonedDateTime.now(clock);


                    if (items.length() > 0) {

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);

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
                                    resourceRoom.setRoomset_id(item.getJSONObject("roomset").optString("_id"));
                                    resourceRoom.setRoomset_name(item.getJSONObject("roomset").optString("roomset_name"));

                                    resourceRoom.setRoom_label(resourceRoom.getRoomsetShortName() + " (" + resourceRoom.getRoom() + ")");

                                    resourceRoom.setResources(new ArrayList<ResourceItem>());

                                    roomMap.put(roomKey,resourceRoom);

                                    // call getRoomsetHours after we add the resourceRoom to the room map so we can set the open/closed value
                                    JSONArray hoursArray = item.getJSONArray("hours");
                                    getRoomsetHours(resourceRoom,hoursArray);


                                }

                                // resource
                                ResourceItem r = new ResourceItem();
                                ResourceRoom rr = roomMap.get(roomKey);
                                r.set_id(item.getString("_id"));
                                r.setName(item.getString("name") + "");
                                r.setRoomset_name(rr.getRoomsetShortName());
                                r.setRoom(rr.getRoom());
                                r.setLongitude(rr.getLongitude());
                                r.setLatitude(rr.getLatitude());
                                r.set_category(item.getJSONObject("_category").optString("_id"));
                                r.setCategory(item.getJSONObject("_category").optString("category"));
                                r.set_type(item.getJSONObject("_type").optString("_id"));
                                r.setType(item.getJSONObject("_type").optString("type"));
                                r.set_template(item.optString("_template"));
                                r.setStatus(item.optString("status"));

                                // Attributes
                                r.setAttributes(new ArrayList<ResourceAttribute>());
                                JSONArray jAttributes = item.getJSONArray("attribute_values");
                                for (int a = 0; a < jAttributes.length(); a++) {
                                    r.getAttributes().add(new ResourceAttribute(jAttributes.getJSONObject(a)));
                                }

                                // Images
                                if (item.has("_image") && !item.isNull("_image")) {
                                    try {
                                        JSONArray jImages = item.getJSONArray("_image");
                                        r.setImages(new String[jImages.length()]);
                                        for (int j = 0; j < jImages.length(); j++) {
                                            JSONObject metadata = jImages.getJSONObject(j).getJSONObject("metadata");
                                            r.getImages()[j] = metadata.getString("raw_id");
                                        }
                                    }
                                    catch (org.json.JSONException e) {
                                        Timber.d(e.getMessage());
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
                                rr.getResources().get(i).setHours(rr.getHours());
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

    private void getRoomsetHours(ResourceRoom resourceRoom,JSONArray hoursArray) {

        // get roomset id from roomKey
        String roomset = resourceRoom.getRoomset_id();

        if (hoursMap.containsKey(roomset)) {
            ArrayList<RoomsetHours> rh = hoursMap.get(roomset);
            roomMap.get(resourceRoom.getRoom()).setHours(rh);
        }
        else {
            ArrayList<RoomsetHours> hours = new ArrayList<RoomsetHours>();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat dayFormat = new SimpleDateFormat("E");
            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
            Date now = new Date();

            df.setTimeZone(TimeZone.getTimeZone("Z"));

            if (hoursArray != null) {
                for (int i = 0; i < hoursArray.length(); i++) {
                    try {
                        JSONObject hoursEntry = hoursArray.getJSONObject(i);
                        String start_date = hoursEntry.getString("start_date").substring(0, 19);
                        String end_date = hoursEntry.getString("end_date").substring(0, 19);
                        try {
                            Date sdate = df.parse(start_date);
                            Date edate = df.parse(end_date);
                            RoomsetHours roomsetHours = new RoomsetHours(dayFormat.format(sdate),timeFormat.format(sdate),timeFormat.format(edate));
                            if (sdate.toString().compareTo(now.toString())  <= 0 && now.toString().compareTo(edate.toString()) <= 0) {
                                roomsetHours.setStatus(RoomsetHours.OPEN);
                                roomMap.get(resourceRoom.getRoom()).setOpen(true);
                            }
                            else {
                                roomsetHours.setStatus(RoomsetHours.CLOSED);
                            }

                            Timber.d(roomsetHours.getDay() + " " + roomsetHours.getStart_time() + " - " + roomsetHours.getEnd_time());
                            hours.add(roomsetHours);
                        } catch (ParseException p) {
                            Timber.d(p.getMessage());
                        }
                    } catch (JSONException e) {
                        Timber.d(e.getMessage());
                    }
                }
            }

            hoursMap.put(roomset,hours);
            roomMap.get(resourceRoom.getRoom()).setHours(hours);

        }
    }
}