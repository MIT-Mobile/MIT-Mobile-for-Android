package edu.mit.mitmobile2.classes;

import java.util.HashMap;
import java.util.Map;

public class SharedData {

	private Map data = new HashMap();
	
	
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
