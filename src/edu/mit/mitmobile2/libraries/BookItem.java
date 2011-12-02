package edu.mit.mitmobile2.libraries;

import java.util.List;

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
    public static class Holding {
        public String library;
        public String address;
        public String url;
    }
    
}
