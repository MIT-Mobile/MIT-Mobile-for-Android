package edu.mit.mitmobile2.shuttles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.objs.RouteItem;
import edu.mit.mitmobile2.objs.RouteItem.Prediction;
import edu.mit.mitmobile2.objs.RouteItem.Stops;
import edu.mit.mitmobile2.objs.RouteItem.Vehicle;

public class RoutesParser {

	public RouteItem ri;
	
	public String ROUTES_BASE_URL = "http://" + Global.getMobileWebDomain() + "/api/shuttles/";
	
	public String getBaseUrl() {
		return ROUTES_BASE_URL;
	}
	
	
	static List<RouteItem> routesParser(JSONObject jsonArray) throws JSONException {
		ArrayList<RouteItem> items = new ArrayList<RouteItem>();
		
		JSONArray jRoutes = jsonArray.getJSONArray("routes");
		for(int index=0; index < jRoutes.length(); index++) {
			JSONObject jsonObject;
			try {
				jsonObject = jRoutes.getJSONObject(index);
			} catch (JSONException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			items.add(parseJSONRouteObject(jsonObject));
		}
		return items;
	}

	static RouteItem parseJSONRouteObject(JSONObject jItem) throws JSONException {
		RouteItem routeItem = new RouteItem();
		routeItem.id = jItem.getString("id");
		routeItem.title = jItem.getString("title");
		routeItem.url = jItem.getString("url");
		routeItem.description = jItem.getString("description");
		routeItem.group = jItem.getString("group");
		routeItem.active = jItem.getBoolean("active");
		routeItem.predictable = jItem.getBoolean("predictable");
		routeItem.interval = jItem.getInt("interval");
		
		if (jItem.has("stops")) {
			routeItem.stops = parseJSONStopsArray(jItem);
		}
		
		if (jItem.has("vehicles")) {
			routeItem.vehicles = parseJSONVehiclesArray(jItem.optJSONArray("vehicles"));
		}
		
		if (jItem.has("path")) {
			routeItem.path = parseJSONPath(jItem.getJSONObject("path"));
		}
		
		return routeItem;
	}
	
	
	static List<Vehicle> parseJSONVehiclesArray(JSONArray vehicleArray) throws JSONException {
		if (vehicleArray == null) return null;
		
		List<RouteItem.Vehicle> vehicles = new ArrayList<RouteItem.Vehicle>();
		
		for (int i = 0; i < vehicleArray.length(); i++) {
			JSONObject jVehicle = vehicleArray.getJSONObject(i);
			RouteItem.Vehicle vehicle = new RouteItem.Vehicle();
			vehicle.id = jVehicle.getString("id");
			vehicle.lat = jVehicle.getDouble("lat");
			vehicle.lon = jVehicle.getDouble("lon");
			vehicle.heading = jVehicle.getInt("heading");
			vehicle.speed = jVehicle.getDouble("speed");
			vehicle.lastReport = jVehicle.getInt("last_report");
			vehicles.add(vehicle);
		}
		
		return vehicles;
	}
	
	
	static RouteItem.Path parseJSONPath(JSONObject jPath) throws JSONException {
		if (jPath == null) return null;
		
		JSONArray segments = jPath.optJSONArray("segments");
		
		RouteItem.Path path = new RouteItem.Path();
		for (int index = 0; index < segments.length(); index++) {
			JSONArray segment = segments.getJSONArray(index);
			
			RouteItem.Loc location = new RouteItem.Loc();
			location.lat = (float) segment.getDouble(0);
			location.lon = (float) segment.getDouble(1);
			
			path.segments.add(location);
		}
		
		JSONArray bbox = jPath.getJSONArray("bbox");
		if (bbox.length() == 4) {
			path.minLat = (float) bbox.getDouble(0);
			path.minLon = (float) bbox.getDouble(1);
			path.maxLat = (float) bbox.getDouble(2);
			path.maxLon = (float) bbox.getDouble(3);
		}
		
		return path;
	}
	
	
	static List<Stops> parseJSONStopsArray(JSONObject jsonObject) throws JSONException {

		ArrayList<Stops> stops = new ArrayList<Stops>();
		JSONArray jStops = jsonObject.getJSONArray("stops");
		
		long now = new Date().getTime() / 1000;

		for (int s = 0; s < jStops.length(); s++) {
			JSONObject jStop = jStops.getJSONObject(s);

			Stops stopItem = new Stops();

			stopItem.id = jStop.getString("id");
			stopItem.url = jStop.getString("url");
			stopItem.title = jStop.getString("title");
			stopItem.lat = jStop.getDouble("lat");
			stopItem.lon = jStop.getDouble("lon");

			stopItem.upcoming = jStop.optBoolean("upcoming", false);

			stopItem.next = jStop.optInt("next", 0);
			stopItem.now = now;

			stopItem.route_id = jStop.optString("route_id", null);

			// predicted delays
			if (jStop.has("predictions")) {
				JSONArray predictions = jStop.optJSONArray("predictions");
				if (predictions != null) {
					for (int p = 0; p < predictions.length(); p++) {
						JSONObject jPrediction = predictions.getJSONObject(p);

						Prediction prediction = new Prediction();
						prediction.vehicleID = jPrediction
								.getString("vehicle_id");
						prediction.timestamp = jPrediction.getLong("timestamp");
						prediction.seconds = jPrediction.getInt("seconds");

						stopItem.predictions.add(prediction);
					}
				}
			}
			
			// Schedule
			if (jStop.has("schedule")) {
				JSONArray jSchedule = jStop.getJSONArray("schedule");
				List<Long> schedule = new ArrayList<Long>();
				for (int i = 0; i < jSchedule.length(); i++) {
					try {
						long value = Long.parseLong(jSchedule.getString(i));
						schedule.add(value);
					} catch (NumberFormatException e) {
						Log.e("", "EROR value: " + jSchedule.getString(i));
					}
					
//					schedule.add(jSchedule.getLong(i));
				}
				stopItem.setSchedule(schedule);
			}
			
			
			stops.add(stopItem);
		}

		return stops;
	}
	
	
}
