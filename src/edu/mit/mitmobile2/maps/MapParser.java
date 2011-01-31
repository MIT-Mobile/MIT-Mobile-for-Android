package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.objs.MapItem;

public class MapParser {
	
	public static List<MapItem> parseMapItems(JSONArray jArray) throws JSONException {
	
		ArrayList<MapItem> mapItems = new ArrayList<MapItem>();
		for(int i = 0; i < jArray.length(); i++) {
			MapItem mapItem = parseMapItem(jArray.getJSONObject(i));
			mapItems.add(mapItem);
		}
		
		return mapItems;
	}
	
	public static MapItem parseMapItem(JSONObject jItem) throws JSONException {
		
		
        	MapItem mi = new MapItem();

	        JSONArray temp = jItem.optJSONArray("category");
	        if (temp!=null) {
	        	for (int index=0; index<temp.length(); index++) {
	        		String it = temp.getString(index);
	    	        mi.category.add(it);
	        	}
	        }
	        

	        temp = jItem.optJSONArray("altname");
	        if (temp!=null) {
	        	mi.alts = new ArrayList<String>();
	        	for (int index=0; index<temp.length(); index++) {
	        		String it = temp.getString(index);
	    	        mi.alts.add(it);
	        	}
	        }

	        temp = jItem.optJSONArray("contents");
	        if (temp!=null) {
	        	JSONObject j;
	        	String it;
	        	for (int index=0; index<temp.length(); index++) {
	        		j = temp.getJSONObject(index);
	        		it = j.getString("name");
	    	        mi.contents.add(it);
	        	}
	        }

	        mi.displayName = jItem.optString("displayName","");
	        mi.name = jItem.getString("name");
	        mi.id = jItem.getString("id");
	        mi.street = jItem.optString("street","");
	        mi.viewangle = jItem.optString("viewangle","");
	        mi.bldgimg = jItem.optString("bldgimg","");
	        mi.bldgnum = jItem.optString("bldgnum","");

	        temp = jItem.optJSONArray("snippets");
	        if (temp!=null) {
	        	mi.snippets = temp.getString(0);
	        	if (mi.snippets.equalsIgnoreCase(mi.name)) mi.snippets = "";
	        	if (!mi.snippets.startsWith("Building ")) mi.snippets = "Building " + mi.snippets;
	        }

	        mi.long_wgs84 = jItem.getDouble("long_wgs84");
	        mi.lat_wgs84 = jItem.getDouble("lat_wgs84");
	        
	        return mi;	        
	}

	
}
/*
[
{"category":["building"],
"long_wgs84":-71.083463199999997,
"name":"Badger Building",
"lat_wgs84":42.362749229999999,
"id":"object-E70",
"bldgimg":"http://web.mit.edu/campus-map/objimgs/object-E70.jpg",
"snippets":["Laboratory for Financial Engineering"],
"street":"1 Broadway, (8th FL)",
"bldgnum":"E70",
"floorplans":["8"],
"mailing":"77 Massachusetts Avenue",
"viewangle":"west side",
"contents":[
{"url":"http://lfe.mit.edu/","name":"Laboratory for Financial Engineering"},
{"url":"http://web.mit.edu/workplacecenter/","name":"Workplace Center, MIT","altname":["MIT Workplace Center"]},
{"url":"http://ocw.mit.edu/","name":"OpenCourseWare (OCW)"}]},


{"category":
*/
