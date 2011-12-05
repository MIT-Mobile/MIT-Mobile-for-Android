package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookItem {
    public String id;
    public String title;
    public String image;
    public List<String> author;
    public List<String> year;
    public List<String> publisher;
    public List<String> isbn;
    
    public List<String> subjects;
    public List<String> lang;
    public List<String> extent;
    public List<String> format;
    public List<String> summary;
    public List<String> editions;
    public List<String> address;
    public List<Holding> holdings;
    
    public boolean detailsLoaded = false;
    
    public final static String MITLibrariesOCLCCode = "MYG";   
    
    public CharSequence getAuthorsDisplayString() {
    	if (author != null) {
    		StringBuilder builder = new StringBuilder();
    		if (year != null && year.size() > 0) {
    			builder.append(this.year.get(0) + "; ");
    		}
    		for (int i = 0; i < author.size(); i++) {
    			if (i > 0) {
    				builder.append(", ");
    			}
    			builder.append(author.get(i));
    		}
    		return builder;
    	} else {
    		return null;
    	}
    }
    /*
    public CharSequence getYearsDisplayString() {
    	if(year != null) {
    		StringBuilder builder = new StringBuilder();
    		for(String aYear : year) {
    			builder.append(aYear + ", ");
    		}
    		return builder;
    	} else {
    		return null;
    	}
    }
    */
    
    public List<Holding> getHoldingsByOCLCCode(String code) {
    	ArrayList<Holding> filteredHoldings = new ArrayList<Holding>();
    	for(Holding holding : holdings) {
    		if (holding.code.equals(code)){
    			filteredHoldings.add(holding);
    		}
    	}
    	return filteredHoldings;
    }
    
    public static class Holding {
        public String library;
        public String address;
        public String url;
        public String code;
        public int count;
        private ArrayList<Availability> mAvailibitity = new ArrayList<Availability>();
        
        public void addAvailibility(boolean available, String callNumber, String location, String status) {
        	mAvailibitity.add(new Availability(available, callNumber, location, status));
        }
        
        public List<Availability> getAvailabitity() {
        	return mAvailibitity;
        }
        
        public class Availability {
        	boolean available;
        	String callNumber;
        	String location;
        	String status;
        	
        	public Availability(boolean available, String callNumber, String location, String status) {
        		this.available = available;
        		this.callNumber = callNumber;
        		this.location = location;
        		this.status = status;
        	}
        }
        
        public class AvailableCount {
        	int available = 0;
        	int total = 0;
        }
        
        public Map<String, AvailableCount> getAvailabilityCounts() {
        	HashMap<String, AvailableCount> counts = new HashMap<String, AvailableCount>();
        	
        	for (Availability availability : mAvailibitity) {        		
        		AvailableCount availableCount = null;
        		if (!counts.containsKey(availability.location)) {
        			availableCount = new AvailableCount();
        			counts.put(availability.location, availableCount);
        		} else {
        			availableCount = counts.get(availability.location);
        		}
        		availableCount.total += 1;
        		if (availability.available) {
        			availableCount.available += 1;
        		}
        	}
        	return counts;
        }
    }
    
}
