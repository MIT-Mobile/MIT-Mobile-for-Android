package edu.mit.mitmobile2.maps;

import org.json.JSONException;

import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.JSONParser;
import edu.mit.mitmobile2.about.BuildSettings;

public class MapAgeParser extends JSONParser {
	
	String last_updated;
	
	String params = ""; 
	
	public MapAgeParser() {
		expectingObject = true;
	}

	@Override
	public String getBaseUrl() {
		return "http://" + Global.getMobileWebDomain() + "/api/map/";
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
