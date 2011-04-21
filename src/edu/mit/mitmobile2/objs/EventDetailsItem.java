package edu.mit.mitmobile2.objs;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EventDetailsItem {

	//  &command=detail
	
	public String owner;
	public String shortloc;
	public String location;
	public String status;
	public String event;
	public String id;
	public String title;
	// TODO change to private
	public long start;
	public Long end;
	public String cancelled;
	//String coordinate;
	
	
	public String infophone = "";
	public String infourl = "";
	public String description = "";
	
	public Coord coordinates;
	
	public class Coord {
		public double lat;
		public double lon;
		public String description;
	}
	
	public String getLocationName() {
		if(!location.equals("")) {
			return location;
		}
		
		if(!shortloc.equals("")) {
			return shortloc;
		}
		
		return null;
	}
	
	private static final SimpleDateFormat sShortDayFormat = new SimpleDateFormat("M/d/yy");
	private static final SimpleDateFormat sLongDayFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
	private static final SimpleDateFormat sTimeFormat = new SimpleDateFormat("h:mm a");
	
	public static class TimeSummaryMode {
	    boolean mShortOrLong;
	    boolean mIncludeDays;
	    boolean mIncludeTime;
	    
	    private TimeSummaryMode(boolean shortOrLong, boolean includeDays, boolean includeTime) {
	        mShortOrLong = shortOrLong;
	        mIncludeDays = includeDays;
	        mIncludeTime = includeTime;
	    }
	}
	
	public static final TimeSummaryMode LONG_DAY_TIME = new TimeSummaryMode(false, true, true);
	public static final TimeSummaryMode TIMES_ONLY = new TimeSummaryMode(false, false, true);
	public static final TimeSummaryMode SHORT_DAY_TIME = new TimeSummaryMode(true, true, true);
	public static final TimeSummaryMode SHORT_DAYS_ONLY = new TimeSummaryMode(true, true, false);
	
    public String getTimeSummary(TimeSummaryMode summaryMode) {
        String summary = "";
        
        if(summaryMode.mIncludeDays) {
            SimpleDateFormat dayFormat = summaryMode.mShortOrLong ? sShortDayFormat : sLongDayFormat;
            String startDay = dayFormat.format(getStartDate());
            summary += startDay;
            if(getEndDate() != null) {
                String endDay = dayFormat.format(getEndDate());
                    
                if(!endDay.equals(startDay)) {
                    summary += "-" + endDay;
                }
             }
        }
        
        if(summaryMode.mIncludeTime) {
            String time = sTimeFormat.format(getStartDate());
            if(getEndDate() != null) {
                String endTime = sTimeFormat.format(getEndDate());
                
                if(!endTime.equals(time)) {
                    time += "-" + endTime;
                }
            }
            
            if(time.equals("12:00 AM-11:59 PM")) {
                time = "All day";
            } 
            
            summary += " " + time;
        }
        
        return summary;
    }
	
	public Date getStartDate() {
		return new Date(start * 1000);
	}
	
	public Date getEndDate() {
		if(end != null) {
			return new Date(end * 1000);
		} else {
			return null;
	    }
	}
}

/*
//http://mobile-dev.mit.edu/api/?
type=Events  ***
&time=1276611321 ***
&module=calendar
&command=day

[{"owner":"1112",
	"shortloc":"E23-205",
	"location":"",
	"status":"N",
	"event":"98761",
	"end":1276660740,
	"id":"11612422",
	"title":"Wellness Class Registration",
	"start":1276574400,
	"cancelled":null,
	"coordinate":{"lat":42.36102212,"lon":-71.08663215},
	"description":""},
  
  {"owner":"1244",
		"shortloc":"W31-301",
		"location":"",
		"status":"N",
		"event":"95149",
		"end":1276635600,
		"id":"11605146",
		"title":"MIT\/CRE Professional Development Institute - Best Practices in Sustainable Development",
		"start":1276606800,
		"cancelled":null,
		"coordinate":{"lat":42.35963775,"lon":-71.09516824},
		"description":""},
	*/

/////////////////////////////////////////////////

/*
// DETAILS
 
//http://mobile-dev.mit.edu/api/?module=calendar
&id=11612422  ***
&command=detail

{"id":11612422,
	"event":98761,
	"title":"Wellness Class Registration",
	"description":"The Center for Health Promotion & Wellness sponsors wellness classes, including gentle yoga, pilates, tai chi, hula hooping, fitness, and many others. Classes are offered in a friendly, welcoming atmosphere with knowledgeable instructors. Classes are kept small and suitable for all levels of experience. Daytime and after work class schedules make it easy to commit to adding wellness into a busy day. Most classes are offered throughout the year and run for eight to 10 weeks. <br \/><br \/>Please visit our website for more information.",
	"start":1276574400,
	"end":1276660740,
	"lecturer":"",
	"infoname":"Stephanie Smith",
	"infomail":"smis@med.mit.edu",
	"infourl":"http:\/\/medweb.mit.edu\/wellness\/classes\/",
	"infoloc":null,
	"infophone":"617-253-5358",
	"tickets":null,
	"cost":null,
	"shortloc":"E23-205",
	"location":"",
	"cancelled":null,
	"soldout":null,
	"handicapped":null,
	"priority":null,
	"opento":0,
	"opentext":"",
	"private":null,
	"categories":[{"invisible":"0","name":"Recreation","catid":"31","obsolete":"0"}],
	"sponsors":[{"authorized":"1","privatemail":"damt@med.mit.edu","location":"E23-495","invisible":"0","permission":"0","name":"MIT Medical","contact":"Lisa Damtoft","phone":"617-253-1508","publicmail":null,"special":"0","url":"http:\/\/medweb.mit.edu\/","groupid":"1112","defunct":null},
	            {"authorized":"1","privatemail":"bars@med.mit.edu","location":"E23-205","invisible":"0","permission":"1","name":"Center for Health Promotion and Wellness","contact":"Susanna Barry","phone":"617\/253-3646","publicmail":null,"special":"0","url":"web.mit.edu\/medical\/wellness","groupid":"8892","defunct":null}
				],
	"othersponsor":"",
	"owner":1112,
	"seriestitle":"",
	"seriesdesc":"",
	"expired":null,
	"created_by":"smiths",
	"created":{"weekday":"Saturday","day":22,"month":5,"monthname":"May","year":2010,"hour":13,"minute":6},
	"modified_by":"smiths",
	"modified":{"weekday":"Saturday","day":22,"month":5,"monthname":"May","year":2010,"hour":13,"minute":30},
	"type_code":"R",
	"status":"N",
	"patterns":[{"month_of_year":"0",
		"startdate":{"hour":"00","weekday":"Monday","minute":"00","month":"05","monthname":"May","day":"24","year":"2010"},
		"week_of_month":"0","exclude_holidays":"1","unit":"D","subtype":"0",
		"starttime":{"hour":null,"weekday":null,"minute":null,"month":null,"monthname":null,"day":null,"year":null},
		"enddate":{"hour":null,"weekday":null,"minute":null,"month":null,"monthname":null,"day":null,"year":null},"order":"1","day_of_week":"0","day_of_month":"0",
		"endtime":{"hour":null,"weekday":null,"minute":null,"month":null,"monthname":null,"day":null,"year":null},"multiplier":"1"}
	],
	"exceptions":[],
	"coordinate":{"lat":42.36102212,"lon":-71.08663215}}
*/


