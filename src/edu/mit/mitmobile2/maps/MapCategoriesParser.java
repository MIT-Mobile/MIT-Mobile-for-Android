package edu.mit.mitmobile2.maps;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.JSONParser;
import edu.mit.mitmobile2.about.BuildSettings;
import edu.mit.mitmobile2.objs.MapCatItem;

public class MapCategoriesParser extends JSONParser {
	
	
	String params = "";  // TODO move up
	
	public MapCategoriesParser() {
		items = new ArrayList<MapCatItem>();
	}
	
	public String getBaseUrl() {
		return "http://" + BuildSettings.MOBILE_WEB_DOMAIN + "/api/map/";
	}
	
	/////////////////////////////////////////
	@Override
	public void parseObj() {
		
		
        MapCatItem mi = new MapCatItem();
        
        try {

	        mi.categoryName = jItem.getString("categoryName");
	        mi.categoryId = jItem.getString("categoryId");
	        
	        JSONArray subcats = jItem.optJSONArray("subcategories");
	        if (subcats!=null) {
	        	for (int index=0; index<subcats.length(); index++) {
	        		//String it = subcats.getString(index);
	        		JSONObject subcat = (JSONObject) subcats.get(index);
	        		MapCatItem si = new MapCatItem();
	        		si.categoryName = subcat.getString("categoryName");
	        		si.categoryId   = subcat.getString("categoryId");
	    	        mi.subcategories.add(si);
	        	}
	        }
	        
	        items.add(mi);
	        
	        
    	} catch (JSONException e) {
			e.printStackTrace();
		}
    	
	}
	

}

/*
[{"categoryName":"Building Number",
"categoryId":"building_number",
"subcategories":[{"categoryId":"m","categoryName":"Main Campus (1-76)"},
	{"categoryId":"e","categoryName":"East Campus (E1-E70)"},
	{"categoryId":"n","categoryName":"North Campus (N4-N57)"},
	{"categoryId":"ne","categoryName":"Northeast Campus (NE18-NE125)"},
	{"categoryId":"nw","categoryName":"Northwest Campus (NW10-NW95)"},
	{"categoryId":"w","categoryName":"West Campus (W1-WW15)"}]},

{"categoryName":"Building Name",
"categoryId":"building_name",
"subcategories":[{"categoryId":"1_999","categoryName":"1-999"},
	{"categoryId":"a_c","categoryName":"A-C"},
	{"categoryId":"d_f","categoryName":"D-F"},{"categoryId":"g_l","categoryName":"G-L"},{"categoryId":"m_q","categoryName":"M-Q"},{"categoryId":"r_u","categoryName":"R-U"},{"categoryId":"v_z","categoryName":"V-Z"}]},{"categoryName":"Selected Rooms","categoryId":"room"},{"categoryName":"Food Services","categoryId":"food"},{"categoryName":"Libraries","categoryId":"library"},{"categoryName":"Residences","categoryId":"residence"},{"categoryName":"Parking Lots","categoryId":"parking"},{"categoryName":"Streets and Landmarks"

*/
