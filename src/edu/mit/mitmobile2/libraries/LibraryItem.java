package edu.mit.mitmobile2.libraries;

import java.util.List;
import java.util.Map;

public class LibraryItem {
    public String library;
    public String status;
    
    public boolean isDetailLoaded = false;
    
    public String hoursToday;
    public String url;
    public String tel;
    public String location;
    
    public Schedule currentTerm;
    public List<Schedule> previousTerms;
    
    
    
    
    public static class Schedule {
        public String range_start;
        public String range_end;
        public Map<String, String> hours;
        public String name;
        public String termday;
    }

}
