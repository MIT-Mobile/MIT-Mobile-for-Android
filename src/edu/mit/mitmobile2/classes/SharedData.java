package edu.mit.mitmobile2.classes;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;

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
