package edu.mit.mitmobile2.classes;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.objs.CourseListItem;

public class CourseListParser {
	
	public List<CourseListItem> parseJSONArray(JSONArray jArray) {
		

		ArrayList<CourseListItem> items = new ArrayList<CourseListItem>();
		
        
        
        try {
       
        	for(int i = 0; i < jArray.length(); i++) {
        		
        		JSONObject jItem = (JSONObject) jArray.get(i);
        		
        		CourseListItem cdi = new CourseListItem();
        		cdi.shortStr = jItem.getString("short");
        		cdi.name = jItem.getString("name");
        		cdi.is_course = jItem.getInt("is_course");
	        
        		items.add(cdi);
        	}
	        
	        
    	} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to parse courses from server");
		}
    	
    	return items;		    	
	}
	
}
