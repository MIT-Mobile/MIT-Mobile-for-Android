package edu.mit.mitmobile2.links;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LinkListItem {
	
	String title;
	ArrayList<LinkItem> links;
	
	public LinkListItem(JSONObject obj) throws JSONException {
		if (obj != null) {
			title = obj.getString("title");
			links = new ArrayList<LinkItem>();
			JSONArray arr = obj.getJSONArray("links");
			for (int i = 0; i < arr.length(); i++) {
				links.add(new LinkItem(arr.getJSONObject(i)));
			}
		}
	}
	


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

	
}