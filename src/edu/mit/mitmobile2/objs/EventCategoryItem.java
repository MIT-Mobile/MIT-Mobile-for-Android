package edu.mit.mitmobile2.objs;

import java.util.List;

public class EventCategoryItem {

	//&command=categories -> returns name/catid/subcats
	
	//&command=search -> returns span and events
		          
		          
	public String name;
	public int catid;
	public List<EventCategoryItem> subcats=null;
	public String sourceId=null;
	
}


//http://mobile-dev.mit.edu/api/?module=calendar&command=categories
/*
[{"name":"Arts\/Music\/Film",
	"catid":"19",
	"subcategories":[{"name":"Dance","catid":"3"},
	              
 {"name":"Exhibits","catid":"5"},
 {"name":"Films\/Movies","catid":"8"},
 {"name":"Literary","catid":"11"},
 {"name":"Music","catid":"1"},
 {"name":"New Media Arts","catid":"125"},
 {"name":"Theater","catid":"12"},
 {"name":"Visual Arts","catid":"124"}]},
 {"name":"Campus Tours","catid":"52"},
 {"name":"Career Development",
	 "catid":"24",
	 "subcategories":[{"name":"Career Fairs\/Workshops","catid":"20"},
	                               {"name":"Computer Training","catid":"21"},
	                               {"name":"Fellowships\/Opportunities","catid":"22"},
	                               {"name":"Personal Development","catid":"23"}]},
	                               {"name":"Deadlines","catid":"4"},
	                               {"name":"Diversity & Inclusion","catid":"126"},
	                               {"name":"Global\/International","catid":"123"},
	                               {"name":"Lectures\/Conferences",
	                            	   "catid":"2",
	                            	   "subcategories":[{"name":"Art\/Architecture\/Museum",
	                            		   "catid":"13"}
	                            		   
/////////////////////////////

// ACADEMIC	      

/*
//academic calendar:
//http://mobile-dev.mit.edu/api/?
month=6  ***
&year=2010  ***
&module=calendar
&command=academic

[   {"id":174479444,
	"title":"9:00 am Second-Year and Third-Year Grades Meeting.",
	"start":1275364800,
	"end":1275364800},
	{"id":69093140,"title":"1:00 pm First-Year Grades Meeting.","start":1275451200,"end":1275451200},
	{"id":961271878,"title":"Doctoral Hooding Ceremony.","start":1275537600,"end":1275537600},
	{"id":2027868199,"title":"COMMENCEMENT.","start":1275624000,"end":1275624000},
	{"id":1672936864,"title":"First day of classes for Regular Summer Session.","start":1275883200,"end":1275883200},
	{"id":1836816450,"title":"9:00 am C.A.P. Deferred Action Meeting.","start":1276574400,"end":1276574400},
	{"id":59502237,"title":"9:00 am C.A.P. Deferred Action Meeting.","start":1276660800,"end":1276660800}
]
*/	

/////////////////////////////////////////////////
/*	
//an events search:
http://mobile-dev.mit.edu/api/?
module=calendar
&q=fun ***
&command=search 

{"span":"7 days",
	"events":[
	          {"owner":"1105",
	        	  "shortloc":"N52-200",
	        	  "location":"MIT Museum Main Gallery",
	        	  "status":"N","event":"73447",
	        	  "end":1276981200,
	        	  "id":"9018640",
	        	  "title":"Learning Lab: The Cell",
	        	  "start":1276956000,
	        	  "cancelled":null,
	        	  "coordinate":{"lat":42.36227273,"lon":-71.09718824},"description":""},
	       {"owner":"1105","shortloc":"N52-200","location":"MIT Museum Main Gallery","status":"N","event":"73447","end":1277067600,"id":"9018641","title":"Learning Lab: The Cell","start":1277042400,"cancelled":null,"coordinate":{"lat":42.36227273,"lon":-71.09718824},"description":""},{"owner":"1105","shortloc":"N52-200","location":"MIT Museum Main Gallery","status":"N","event":"73447","end":1277154000,"id":"9018642","title":"Learning Lab: The Cell","start":1277128800,"cancelled":null,"coordinate":{"lat":42.36227273,"lon":-71.09718824},"description":""},{"owner":"284","shortloc":"88.1 FM","location":"88.1 FM","status":"N","event":"98842","end":1277150400,"id":"11614642","title":"Gene 'Honeybear' Cedric Featured on WMBR's Research & Development Program","start":1277143200,"cancelled":null,"description":""},	
*/
	
	
 
