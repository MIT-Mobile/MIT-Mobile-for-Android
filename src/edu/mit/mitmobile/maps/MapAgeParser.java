package edu.mit.mitmobile.maps;

import org.json.JSONException;

import edu.mit.mitmobile.JSONParser;
import edu.mit.mitmobile.about.BuildSettings;

public class MapAgeParser extends JSONParser {
	
	String last_updated;
	
	String params = ""; 
	
	public MapAgeParser() {
		expectingObject = true;
	}

	@Override
	public String getBaseUrl() {
		return "http://" + BuildSettings.MOBILE_WEB_DOMAIN + "/api/map/";
	}
	
	@Override
	public void parseObj() {
        try {
	        last_updated = jItem.getString("last_updated");
    	} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	
}
