package edu.mit.mitmobile.shuttles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import edu.mit.mitmobile.MobileWebApi;
import edu.mit.mitmobile.objs.RouteItem;
import edu.mit.mitmobile.objs.RouteItem.Stops;

public class ShuttleModel {
	

	public static final String KEY_STOP_TITLE = "stop_title";
	public static final String KEY_STOP_ID = "stop_id";
	public static final String KEY_ROUTE_ID = "route_id";
	public static final String KEY_TIME = "time";
	
	
	
	static final String BASE_PATH = "/shuttles";
	
	static private long lastRouteFetchTime = -1;
	static private long ROUTE_CACHE_TIMEOUT = 20 * 60 * 1000; // 20 minutes
	static private ArrayList<RouteItem> routes = null;
	
	static final int ALERT_EXPIRE_TIME = 30 * 60 * 1000; // 30 minutes
	
	static private HashMap<String, List<Stops>> stops = new HashMap<String, List<Stops>>();
	
	private static List<RouteItem> getRoutes() {
		if(routes != null) {
			return routes;
		} else {
			return Collections.<RouteItem>emptyList();
		}
	}
	
	public static List<RouteItem> getRoutes(boolean isSafeRide) {
		ArrayList<RouteItem> routes = new ArrayList<RouteItem>();
		
		for (RouteItem aRouteItem : getRoutes()) {	
			if (aRouteItem.isSafeRide == isSafeRide) {
				routes.add(aRouteItem);
			}
		}
		return routes;
	}
	
	public static List<RouteItem> getSortedRoutes() {
		// reorder the routes 
		// so that day time shuttles proceed night time saferides
		ArrayList<RouteItem> reorderedRoutes = new ArrayList<RouteItem>();
		reorderedRoutes.addAll(getRoutes(false));
		reorderedRoutes.addAll(getRoutes(true));
		return reorderedRoutes;
	}
	
	public static RouteItem getRoute(String routeId) {
		for(RouteItem routeItem : getRoutes()) {
			if(routeItem.route_id.equals(routeId)) {
				return routeItem;
			}
		}
		return null;
	}
	
	public static RouteItem getUpdatedRoute(RouteItem routeItem) {
		return getRoute(routeItem.route_id);
	}
	
	public static List<Stops> getStops(String stopId) {
		return stops.get(stopId);
	}
	
	public static Stops getStops(String stopId, String routeId) {
		for(Stops stops : getStops(stopId)) {
			if(stops.route_id.equals(routeId)) {
				return stops;
			}
		}
		return null;
	}
	
	public static int getStopPosition(List<Stops> stops, String stopId) {
		for(int position = 0; position < stops.size(); position++) {
			Stops stop = stops.get(position);
			if(stop.id.equals(stopId)) {
				return position;
			}
		}
		return -1;
	}
	
	public static void addRoute(RouteItem routeItem) {
		if(routes == null) {
			routes = new ArrayList<RouteItem>(); 
		}
		
		for(int index=0; index < routes.size(); index++) {
			if(routes.get(index).route_id.equals(routeItem.route_id)) {
				routes.set(index, routeItem);
				return;
			}
		}
		routes.add(routeItem);
	}
	
	/*
	 * This function only updates the route data, if the route currently
	 * exists in the routes list
	 */
	private static void updateRouteItem(RouteItem routeItem) {
		if(routes != null) {
			for(int index=0; index < routes.size(); index++) {
				if(routes.get(index).route_id.equals(routeItem.route_id)) {
					routes.set(index, routeItem);
					return;
				}
			}
		}
	}
	
	public static void fetchRoutes(Context context, final Handler uiHandler, boolean forceRefresh) {
		if(!forceRefresh && 
			(System.currentTimeMillis() - lastRouteFetchTime) < ROUTE_CACHE_TIMEOUT ) {
			
				MobileWebApi.sendSuccessMessage(uiHandler);
				return;
		}
		
		HashMap<String, String> routesParameters = new HashMap<String, String>();
		routesParameters.put("command", "routes");
		routesParameters.put("compact", "true");
		
		MobileWebApi webApi = new MobileWebApi(false, true, "Shuttle Routes", context, uiHandler);
		webApi.requestJSONArray(BASE_PATH, routesParameters,
			new MobileWebApi.JSONArrayResponseListener(new MobileWebApi.DefaultErrorListener(uiHandler), null) {			
				@Override
				public void onResponse(JSONArray array) {
					routes = new ArrayList<RouteItem>();
					routes.addAll(RoutesParser.routesParser(array));
					lastRouteFetchTime = System.currentTimeMillis();
					MobileWebApi.sendSuccessMessage(uiHandler);
				}
			}
		);
	}
	
	public static void fetchRouteDetails(Context context, RouteItem routeItem, final Handler uiHandler) {
		// determine if any predictions for route details data exists
		// if some predictions exists do a silent request (i.e. dont show error or loading messages);
		

		boolean silent = true;
		/*
		RouteItem cachedRouteItem = getRoute(routeItem.route_id);
		boolean silent = false;
		if(cachedRouteItem != null && !cachedRouteItem.stops.isEmpty()) {
			if(cachedRouteItem.stops.get(0).predictions.isEmpty()) {
				silent = true;
			}
		}
		*/
		HashMap<String, String> routeInfoParameters = new HashMap<String, String>();
		routeInfoParameters.put("command", "routeInfo");
		routeInfoParameters.put("full", "true");
		routeInfoParameters.put("id", routeItem.route_id);
		
		MobileWebApi webApi = new MobileWebApi(!silent, !silent, "Shuttle Route", context, uiHandler);
		webApi.requestJSONObject(BASE_PATH, routeInfoParameters, 
			new MobileWebApi.JSONObjectResponseListener(new MobileWebApi.DefaultErrorListener(uiHandler), null) {					
				@Override
				public void onResponse(JSONObject object) {
					updateRouteItem(RoutesParser.parseJSONRouteObject(object));
					MobileWebApi.sendSuccessMessage(uiHandler);
				}
			}
		);		
	}
	
	public static void fetchStopDetails(final String stopId, final Handler handler) {
		HashMap<String, String> stopInfoParameters = new HashMap<String, String>();
		stopInfoParameters.put("command", "stopInfo");
		stopInfoParameters.put("id", stopId);
		
		MobileWebApi webApi = new MobileWebApi();
		webApi.requestJSONObject(stopInfoParameters, new MobileWebApi.JSONObjectResponseListener(null, null) {
			
			@Override
			public void onResponse(JSONObject object) {
				stops.put(stopId, RoutesParser.parseJSONStopsArray(object));
				MobileWebApi.sendSuccessMessage(handler);
			}
		});

	}
	
	/****************************************************/
	public static HashMap<String, HashMap<String, Long>> getAlerts(SharedPreferences pref) {

		HashMap<String, HashMap<String, Long>> alertIdx = new HashMap<String, HashMap<String, Long>>();
		
		//SharedPreferences pref = ctx.getSharedPreferences(Global.PREFS,Context.MODE_WORLD_READABLE|Context.MODE_WORLD_READABLE);  
		
		String stop_id = pref.getString(ShuttleModel.KEY_STOP_ID, null); 
		String routes = pref.getString(ShuttleModel.KEY_ROUTE_ID, null); 
		String times = pref.getString(ShuttleModel.KEY_TIME, null); 

		Log.d("ShuttleModel", "shuttle-alerts: get-> stop_id: " + stop_id);
		Log.d("ShuttleModel", "shuttle-alerts: get-> route_id: " + routes);
		Log.d("ShuttleModel", "shuttle-alerts: get-> times: " + times);
		
		if ((stop_id!=null)&&(routes!=null)&&(times!=null)) {
			
			String[] stop_alarms = stop_id.split(",");
			String[] routes_alarms = routes.split(",");
			String[] times_alarms = times.split(",");
			
			if (stop_alarms.length<1) {
				return null;
			}
			if (stop_alarms.length!=routes_alarms.length) {
				Log.e("ShuttleModel", "shuttle-alerts: bad length 1");
				return null;
			}
			if (stop_alarms.length!=times_alarms.length) {
				Log.e("ShuttleModel", "shuttle-alerts: bad length 2");
				return null;
			}
			
			Long time;
			HashMap<String,Long> route_times;
			
			for (int x=0; x<stop_alarms.length; x++) {
				
				String s = stop_alarms[x];
				
				if ("".equals(s)) {
					Log.e("ShuttleModel", "shuttle-alerts: error?");
					continue;
				}
				
				route_times = alertIdx.get(s);
				if (route_times==null) {
					route_times = new HashMap<String,Long>();
				} 

				if ("".equals(times_alarms[x])) continue;
				
				time = Long.valueOf(times_alarms[x]);
				route_times.put(routes_alarms[x], time);

				alertIdx.put(s, route_times);  
				
			}

			
		}
		
		return alertIdx;
		
	}
	/**
	 * @param alertIdx **************************************************/
	//public static void saveAlerts(Context ctx, HashMap<String, HashMap<String, Long>> alertIdx ) {
    public static void saveAlerts(SharedPreferences pref, HashMap<String, HashMap<String, Long>> alertIdx ) {
				
		//SharedPreferences pref = ctx.getSharedPreferences(Global.PREFS,Context.MODE_PRIVATE); 
		//SharedPreferences pref = ctx.getSharedPreferences(Global.PREFS,Context.MODE_WORLD_READABLE|Context.MODE_WORLD_READABLE); 
		
		SharedPreferences.Editor editor = pref.edit();
		
		String concat1 = "";
		String concat2 = "";
		String concat3 = "";

		HashMap<String,Long> route_times;
		
		for (String stop : alertIdx.keySet()) {
			
			route_times = alertIdx.get(stop);
			
			for (String r : route_times.keySet()) {
				Long t = route_times.get(r);
				concat1 += stop + ",";
				concat2 += r + ",";
				concat3 += t + ",";
			}
			
		}

		editor.putString(ShuttleModel.KEY_STOP_ID, concat1);  
		editor.putString(ShuttleModel.KEY_ROUTE_ID, concat2);  
		editor.putString(ShuttleModel.KEY_TIME, concat3);  

		Log.d("ShuttleModel", "shuttle-alerts: set-> stop_id: " + concat1);
		Log.d("ShuttleModel", "shuttle-alerts: set-> route_id: " + concat2);
		Log.d("ShuttleModel", "shuttle-alerts: set-> times: " + concat3);
		
		boolean success = editor.commit();
		if (!success) {
			Log.e("ShuttleModel", "shuttle-alerts: failed shuttle commit");
		}
	}
	
}
