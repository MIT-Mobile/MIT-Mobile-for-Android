package edu.mit.mitmobile2.classes;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.JSONParser;
import edu.mit.mitmobile2.about.BuildSettings;
import edu.mit.mitmobile2.objs.CourseItem;
import edu.mit.mitmobile2.objs.CourseItem.Announcement;
import edu.mit.mitmobile2.objs.CourseItem.CourseTime;

public class CourseSubjectParser extends JSONParser {
	
	boolean getSubject = true;
	boolean getDetails = false;
	boolean getChecksum = false;
	
	String params = "";  // TODO move up
	
	public CourseSubjectParser(boolean getChecksum, boolean getDetails) {
		
		items = new ArrayList<CourseItem>();
		
		this.getChecksum = getChecksum;
		this.getDetails = getDetails;
		
	}
	
	
	public String getBaseUrl() {
		return "http://" + Global.getMobileWebDomain() + "/api/?module=stellar";
	}
	
	/////////////////////////////////////////
	@Override
	public void parseObj() {
		

        CourseItem cdi = new CourseItem();
        
        try {
        

	        // #Subject
	        if (getSubject)  {
	        	
		        cdi.masterId = jItem.getString("masterId");
		        cdi.setTerm(jItem.getString("term"));
		        
		        cdi.name = jItem.optString("name",cdi.masterId);
		        cdi.title = jItem.optString("title",cdi.masterId);
		        cdi.description = jItem.optString("description","");
		        cdi.stellarUrl = jItem.optString("stellarUrl","");
	
		        /*
		        JSONObject jTimes = jItem.optJSONObject("times");
		        if (jTimes!=null) {
		        	CourseTime t = cdi.new CourseTime();
		        	t.title =  jTimes.getString("title");
		        	t.time =  jTimes.getString("time");
		        	t.location =  jTimes.getString("location");
		        	cdi.times.add(t);
		        }*/
		        JSONArray jTimes2 = jItem.optJSONArray("times");
		        if (jTimes2!=null) {
		        	for (int index=0; index<jTimes2.length(); index++) {
		        		JSONObject jTemp = jTimes2.getJSONObject(index);
		        		CourseTime t = cdi.new CourseTime();
			        	t.title =  jTemp.getString("title");
			        	t.time =  jTemp.getString("time");
			        	t.location =  jTemp.getString("location");
			        	cdi.times.add(t);
		        	}
		        }
		    
		        JSONObject jStaff = jItem.optJSONObject("staff");
		        if (jStaff!=null) {
		        	JSONArray jTemp = jStaff.optJSONArray("instructors");
			        if (jTemp!=null) {
			        	for (int index=0; index<jTemp.length(); index++) {
			        		String temp = jTemp.getString(index);
			        		cdi.staff.instructors.add(temp); 
			        	}
			        }
		        	jTemp = jStaff.optJSONArray("tas");
			        if (jTemp!=null) {
			        	for (int index=0; index<jTemp.length(); index++) {
			        		String temp = jTemp.getString(index);
			        		cdi.staff.tas.add(temp);
			        	}
			        }
		        }
	        }
	        
	        // #Details
	        if (getDetails)  {
		        JSONArray jAnnouncements = jItem.optJSONArray("announcements");
		        if (jAnnouncements!=null) {
		        	JSONObject jTemp;
		            for(int i=0; i<jAnnouncements.length(); i++)
	                {
		            	Announcement a = cdi.new Announcement();
		            	jTemp = jAnnouncements.getJSONObject(i);
		            	jTemp.optJSONObject("date");  // FIXME
			        	a.unixtime =  jTemp.getLong("unixtime");
			        	a.title =  jTemp.getString("title");
			        	a.text =  jTemp.getString("text");
			        	cdi.announcements.add(a);
	                }
		        }
	        }
	        
	        // #Checksum
	        if (getChecksum) cdi.checksum = jItem.optString("checksum",null);
	        
	        
	        /*
	        // Format Date
	        Date d = new Date();
	        //d.setTime(cdi.start*1000);
			
	        SimpleDateFormat sdf;
	        sdf = new SimpleDateFormat("EEE, MMM d 'at' hh:mm a");  
	        
	       // cdi.startStr = sdf.format(d);
	       */
	        
	        items.add(cdi);
	        
	        
    	} catch (JSONException e) {
			e.printStackTrace();
		}
    	
        
    	
	}


	public List<CourseItem> parseJSONArray(JSONArray array) {
		

		for(int i=0; i<array.length(); i++)
        {
        	try {
				jItem = array.getJSONObject(i);
			} catch (JSONException e) {
				e.printStackTrace();
			}
            parseObj();
        }
		
		return null;
	}
	
}
