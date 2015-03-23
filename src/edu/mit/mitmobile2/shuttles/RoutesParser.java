package edu.mit.mitmobile2.shuttles;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.objs.RouteItem;
import edu.mit.mitmobile2.objs.RouteItem.Loc;
import edu.mit.mitmobile2.objs.RouteItem.Stops;
import edu.mit.mitmobile2.objs.RouteItem.Vehicle;

public class RoutesParser {

	final static String TAG = "RoutesParser";
	public RouteItem ri;
	
	public String ROUTES_BASE_URL = "http://" + Global.getMobileWebDomain() + "/api/shuttles/";
	
	public String getBaseUrl() {
		return ROUTES_BASE_URL;
	}
	
	
	static List<RouteItem> routesParser(JSONArray jsonArray) throws JSONException
	{
		Log.d(TAG,"routesParser()");
		ArrayList<RouteItem> items = new ArrayList<RouteItem>();
		for(int index=0; index < jsonArray.length(); index++) {
			JSONObject jsonObject;
			try {
				jsonObject = jsonArray.getJSONObject(index);
			} catch (JSONException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			items.add(parseJSONRouteObject(jsonObject));
		}
		return items;
	}

	static RouteItem parseJSONRouteObject(JSONObject jItem) throws JSONException{
		Log.d(TAG,"parseJSONRouteObject()");
            RouteItem routeItem = new RouteItem();
            routeItem.route_id = jItem.getString("route_id");
            routeItem.title    = jItem.getString("title");
            routeItem.summary  = jItem.getString("summary");
            routeItem.interval = jItem.getInt("interval");
            routeItem.isSafeRide = jItem.getBoolean("isSafeRide");
            routeItem.isRunning = jItem.getBoolean("isRunning");
            routeItem.gpsActive = jItem.optBoolean("gpsActive", false);

            //JSONArray jStops = jItem.optJSONArray("stops");
            if (jItem.has("stops")) {
            	routeItem.stops = parseJSONStopsArray(jItem);
            }
	        
            JSONArray jVehicles = jItem.optJSONArray("vehicleLocations");
            if (jVehicles!=null) {
            	for(int s=0; s<jVehicles.length(); s++)
                {
                	JSONObject jVeh = jVehicles.getJSONObject(s);
                	Vehicle v = new RouteItem.Vehicle();
                	v.lat = jVeh.getDouble("lat");
                	v.lon = jVeh.getDouble("lon");
                	v.heading = jVeh.getInt("heading");
                	routeItem.vehicleLocations.add(v);
                }
            }
	        
	        
	        return routeItem;
	        
	}
	
	static List<Stops> parseJSONStopsArray(JSONObject jsonObject) throws JSONException {		
		Log.d(TAG,"parseJSONStopsArray()");
		
			ArrayList<Stops> stops = new ArrayList<Stops>();
			JSONArray jStops = jsonObject.getJSONArray("stops");
			int now = jsonObject.getInt("now");
			
			for(int s=0; s<jStops.length(); s++)
            {
            	JSONObject jStop = jStops.getJSONObject(s);
            	
            	Stops stopItem = new Stops();
            	
            	stopItem.id = jStop.getString("id");
            	stopItem.title = jStop.getString("title");
            	stopItem.lat = jStop.getString("lat");
            	stopItem.lon = jStop.getString("lon");

            	stopItem.upcoming = jStop.optBoolean("upcoming",false);
                
            	stopItem.next = jStop.optInt("next",0);
            	stopItem.now = now;
            	//if (jStop.isNull("next")) stopItem.next=0;
            	
            	stopItem.route_id = jStop.optString("route_id", null);
            	
            	// predicted delays
            	JSONArray predictions = jStop.optJSONArray("predictions");
            	if (predictions!=null) {
            		int delay;
                    for(int p=0; p<predictions.length(); p++)
                    {
                    	delay = predictions.getInt(p);
                    	stopItem.predictions.add(delay);
                    }
            	}
            	
            	JSONArray jPath = jStop.optJSONArray("path");
     	        if (jPath!=null) {
     	        	String lat,lon;

     				for(int p=0; p<jPath.length(); p++)
     	            {
     					JSONObject jGeo = jPath.getJSONObject(p);
     					Loc location = new RouteItem.Loc();
     					lat = jGeo.getString("lat");
     					location.lat = Float.valueOf(lat);
     					lon = jGeo.getString("lon");
     					location.lon = Float.valueOf(lon);
     					stopItem.path.add(location);
     	            }
                 	
                 }
     	        
            	stops.add(stopItem);
            }
			/*
			Log.d("RoutesParser","RoutesParser: " + result);
			if (!upcoming) {
		        Log.d("RoutesParser","RoutesParser: no upcoming");
			}
			*/
			return stops;
  
	}
}
