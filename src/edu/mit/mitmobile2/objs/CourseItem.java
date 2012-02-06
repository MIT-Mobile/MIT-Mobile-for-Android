package edu.mit.mitmobile2.objs;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.objs.EmergencyItem.Date;

public class CourseItem {

	public class CourseTime {
		public String title;
		public String time;
		public String location;
	}
	public class Staff {
		public List<String> instructors = new ArrayList<String>();
		public List<String> tas = new ArrayList<String>();
	}
	public class Announcement {
		public Date date;
		public long unixtime;
		public String title;
		public String text;
	}
	
	/////////////////////

	
	// # Subject
	//command: subjectList
	//id: courseId
	public String masterId;
	public String name;
	public String title;
	public String description;
	public String stellarUrl;
	public List<CourseTime> times = new ArrayList<CourseTime>();;
	public Staff staff = new Staff();;
	public String term;
	private String termId;
	
	// # Checksum
	//command: subjectList
	//id: courseId
	//checksum: true
	public String checksum;
	
	// # Details
	//module: stellar
	//command: subjectInfo
	//id: subjectId
	public List<Announcement> announcements = new ArrayList<Announcement>();
	public java.util.Date lastViewed;
	public boolean read = true;
	
	//public boolean alarmSet = false;

	public void setTerm(String in) {
		
		this.termId = in;
		this.term = in;  // default
		if (in.length()==4) {
			String season = in.substring(0, 2);
			String year = in.substring(2, 4);
			if ("sp".equals(season)) {
				this.term = "Spring 20"+year;
			} else if ("su".equals(season)) {
				this.term = "Summer 20"+year;
			} else if ("fa".equals(season)) {
				this.term = "Fall 20"+year;
			} else if ("wi".equals(season)) {
				this.term = "Winter 20"+year;  // TODO drop? doesn't exist?
			} else if ("ia".equals(season)) {
				this.term = "IAP 20"+year;
			}
		}
		
	}
	
	public String getTermId() {
		return termId;
	}
	
	// # Search
	//module: stellar
	//command: search
	//query: searchTerms
	//term+ masterId + name + title + description
	
	
}
