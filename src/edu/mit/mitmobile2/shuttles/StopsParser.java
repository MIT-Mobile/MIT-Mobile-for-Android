package edu.mit.mitmobile2.shuttles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.JSONParser;
import edu.mit.mitmobile2.objs.RouteItem.Prediction;
import edu.mit.mitmobile2.objs.RouteItem.Stops;

public class StopsParser extends JSONParser {

	public String ROUTES_BASE_URL = "http://" + Global.getMobileWebDomain() + "/api/shuttles/";
	
	public String getBaseUrl() {
		return ROUTES_BASE_URL;
	}
	
	
	public StopsParser()
	{
		items = new ArrayList<Stops>();
		//items = new ArrayList<RouteItem>();
	}

	/****************************************/
	/*
	@Override
	protected void parseObj(){
	
        try {
           
            JSONArray jStops = jItem.optJSONArray("stops");
            int now = jItem.getInt("now");
            
            if (jStops!=null) {
            	
                for(int s=0; s<jStops.length(); s++)
                {
                	JSONObject jStop = jStops.getJSONObject(s);
                	
                	Stops si = new Stops();
                	
                	si.id = jStop.getString("id");
                	//si.title = jStop.getString("title");  // no such?
                	si.lat = jStop.getDouble("lat");
                	si.lon = jStop.getDouble("lon");
                	si.next = jStop.getInt("next");  // TODO long?
                	si.now = now;

                	//si.path = jStop.optJSONArray("path");
//                	si.direction = jStop.optString("direction");
                	si.route_id = jStop.optString("route_id");
//                	si.gps = jStop.getBoolean("gps");
                	
                	
                	// predicted delays
                	JSONArray predictions = jStop.optJSONArray("predictions");
                	if (predictions!=null) {
                		int delay;
                        for(int p=0; p<predictions.length(); p++)
                        {
                        	/*
                        	delay = predictions.getInt(p);
                        	si.predictions.add(delay);
                        	*/
	
	/*
                        }
                	}
                	
                	// FIXME add Array<Array> 
                	//r.stops.add(si);
                	items.add(si);
                	
                }
            }
	        
	        //JSONArray jCoords = jItem.optJSONArray("coordinate");
	        
    	} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    */
	
	
	@Override
	protected void parseObj() {
		try {
			
			JSONArray jStops = jItem.optJSONArray("stops");
			
			long now = new Date().getTime() / 1000;
			
			if (jStops == null) return;
			
			for (int s = 0; s < jStops.length(); s++) {
				JSONObject jStop = jStops.getJSONObject(s);
				
				Stops stopItem = new Stops();
				
				stopItem.id = jStop.getString("id");
				stopItem.url = jStop.getString("url");
				stopItem.title = jStop.getString("title");
				stopItem.lat = jStop.getDouble("lat");
				stopItem.lon = jStop.getDouble("lon");
				
				// TODO non exist in :stop item
				stopItem.upcoming = jStop.optBoolean("upcoming", false);
				stopItem.next = jStop.optInt("next", 0);
				stopItem.now = now;
				if (jItem.has("id")) {
					stopItem.route_id = jItem.getString("id");
				}
				
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
							Log.e("", "EROR value: " + jSchedule.get(i));
						}
					}
					stopItem.setSchedule(schedule);
				}
				
				
				items.add(stopItem);
			}
		
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	
}
