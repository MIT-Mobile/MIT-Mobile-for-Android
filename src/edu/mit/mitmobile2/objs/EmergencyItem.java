package edu.mit.mitmobile2.objs;

public class EmergencyItem {
	
	public EmergencyItem() {
		date = new Date();
	}
	
	
	public long unixtime;
	public String text;
	public String title;
	public String version;
	
	public Date date;
	
	public class Date {
		
		public int year;
		public int month;
		public int day;
		public int hour;
		public int second;
		public int fraction;
		public int warning_count;
		public String[]  warnings;
		public int error_count;
		public String[] errors;
		public int is_localtime;
		
	}
	
	public static class Contact {
		public String phone;
		public String contact;
		public String description = null;
	}
	
	
}
