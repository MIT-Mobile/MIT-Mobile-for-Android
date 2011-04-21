package edu.mit.mitmobile2;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateStrings {
	
	
	private static final long SECONDS_PER_MINUTE = 60;
	private static final long SECONDS_PER_HOUR = 3600;
	private static final long SECONDS_PER_DAY = 86400;
	
	static final SimpleDateFormat sDateFormat = new SimpleDateFormat("MMM d");
	
	public static String agoString(Date date) {
		long currentTime = System.currentTimeMillis();
		
		long diff = (currentTime - date.getTime()) / 1000;
		
		if(diff < SECONDS_PER_MINUTE) {
			return "Less than 1 minute ago";
		} else if(diff < SECONDS_PER_HOUR) {
			long minutes = diff / SECONDS_PER_MINUTE;
			String plural = (minutes > 1) ? "s" : "";
			return minutes + " minute" + plural + " ago";		
		} else if(diff < SECONDS_PER_DAY) {
			long hours = diff / SECONDS_PER_HOUR;
			String plural = (hours > 1) ? "s" : "";
			return hours + " hour" + plural + " ago";
		} else {
			return sDateFormat.format(date);
		}
	}

}
