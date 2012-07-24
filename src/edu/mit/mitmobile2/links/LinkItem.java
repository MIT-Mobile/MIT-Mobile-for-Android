package edu.mit.mitmobile2.links;

import org.json.JSONException;
import org.json.JSONObject;

public class LinkItem {
	String url;
	String name;
	
	
	public LinkItem (JSONObject obj) throws JSONException {
		if (obj != null) {
			url 	= obj.getString("link");
			name 	= obj.getString("name");
		}
	}
}
