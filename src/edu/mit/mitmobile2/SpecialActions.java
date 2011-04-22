package edu.mit.mitmobile2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class SpecialActions {
	
	public static String actionTitle(String specialUrl) {
		if(specialUrl.startsWith("http://m.mit.edu/open-house")) {
			OpenHouseCategory category = getOpenHouseCategory(specialUrl);
			if(category != null) {
				return category.getTitle();
			};
			// must not be a specific category
			return "Open House";
		}		
		return null;
	}

	public static String actionUrl(String specialUrl) {
		if(specialUrl.startsWith("http://m.mit.edu/open-house")) {
			OpenHouseCategory category = getOpenHouseCategory(specialUrl);
			if(category != null) {
				try {
					return "mitmobile://calendar/category?listID=OpenHouse" +
						"&catID=" + category.getCatID() +
						"&title=" + URLEncoder.encode(category.getTitle(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return null;
				}
			};
			return "mitmobile://calendar/category?listID=OpenHouse";
		}		
		return null;		
	}
	
	private static OpenHouseCategory getOpenHouseCategory(String url) {
		HashMap<String, Integer> categoryIDs = new HashMap<String, Integer>();
		
		OpenHouseCategory[] categories = new OpenHouseCategory[] {
			new OpenHouseCategory("eng","Engineering, Technology, and Invention", 39),
			new OpenHouseCategory("energy", "Energy, Environment, and Sustainability", 40),
			new OpenHouseCategory("entrepreneurship", "Entrepreneurship and Management", 41),
			new OpenHouseCategory("biotech", "Life Sciences and Biotechnology", 42),
			new OpenHouseCategory("sciences", "The Sciences", 43),
			new OpenHouseCategory("air", "Air and Space Flight", 44),
			new OpenHouseCategory("architecture", "Architecture, Planning, and Design", 45),
			new OpenHouseCategory("humanities", "Arts, Humanities, and Social Sciences", 46),
			new OpenHouseCategory("life", "MIT Learning, Life, and Culture", 46),
		};
		
		for(OpenHouseCategory category : categories) {
			if(url.equals("http://m.mit.edu/open-house/" + category.getIdentifier())) {
				return category;
			}
		}
		
		return null;
	}
	
	private static class OpenHouseCategory {
		
		private String mIdentifier;
		private String mTitle;
		private int mCatID;
		
		public OpenHouseCategory(String identifier, String title, int catID) {
			mIdentifier = identifier;
			mTitle = title;
			mCatID = catID;
		}
		
		public String getIdentifier() {
			return mIdentifier;
		}
		
		public String getTitle() {
			return mTitle;
		}
		
		public int getCatID() {
			return mCatID;
		}
	}
}
