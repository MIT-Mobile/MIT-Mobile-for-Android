package edu.mit.mitmobile2;

import java.util.HashMap;
import java.util.Map;

import edu.mit.mitmobile2.facilities.FacilitiesData;

public class SharedData {

	private Map<String, FacilitiesData> data = new HashMap<String, FacilitiesData>();
	
	
	public SharedData() {
		super();
		data.put("facilities", new FacilitiesData());
	}


	public FacilitiesData getFacilitiesData() {
		if (data.get("facilities") != null) {
			return (FacilitiesData)data.get("facilities");
		}
		else {
			return null;
		}
	}
}
