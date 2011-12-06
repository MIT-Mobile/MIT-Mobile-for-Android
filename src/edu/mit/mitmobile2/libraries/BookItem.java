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
    
    public String url;
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
        
        public class Availabilitys {
        	int available = 0;
        	int total = 0;
        	ArrayList<Availability> books = new ArrayList<Availability>();
        	
        	public List<Availability> getBooks() {
        		return books;
        	}
        }
        
        public Map<String, Availabilitys> getAvailabilitys() {
        	HashMap<String, Availabilitys> counts = new HashMap<String, Availabilitys>();
        	
        	for (Availability availability : mAvailibitity) {        		
        		Availabilitys availablitys = null;
        		if (!counts.containsKey(availability.location)) {
        			availablitys = new Availabilitys();
        			counts.put(availability.location, availablitys);
        		} else {
        			availablitys = counts.get(availability.location);
        		}
        		availablitys.total += 1;
        		if (availability.available) {
        			availablitys.available += 1;
        		}
        		availablitys.books.add(availability);
        	}
        	
        	return counts;
        }
    }
    
}
