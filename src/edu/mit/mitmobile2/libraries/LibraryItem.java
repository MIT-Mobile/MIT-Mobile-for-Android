package edu.mit.mitmobile2.libraries;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LibraryItem {
    public String library;
    public String status;
    
    public boolean isDetailLoaded = false;
    
    public String hoursToday;
    public String tel;
    public String location;
    
    public Schedule currentTerm;
    public List<Schedule> previousTerms = Collections.emptyList();
    public List<Schedule> nextTerms = Collections.emptyList();
    
    
    
    
    public static class Schedule {
        public Date range_start;
        public Date range_end;
        public List<Hours> hours;
        public String name;
        public String termday;
    }

    public static class Hours {
    	public String title;
    	public String description;
    }
}
