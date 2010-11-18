package edu.mit.mitmobile.events;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile.MobileWebApi;
import edu.mit.mitmobile.objs.EventCategoryItem;
import edu.mit.mitmobile.objs.EventDetailsItem;
import edu.mit.mitmobile.objs.SearchResults;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class EventsModel {

	private static int TIME_ZONE_OFFSET = 5;
	
	private static HashMap<Integer, EventDetailsItem> sBriefEventsCache = new HashMap<Integer, EventDetailsItem>();
	private static HashMap<Integer, EventDetailsItem> sFullEventsCache = new HashMap<Integer, EventDetailsItem>();
	
	public enum EventType {
		Events,
		Exhibits,
	}
	
	public static void fetchDayEvents(final long unixtime, final EventType eventType, Context context, final Handler uiHandler) {
		
		if(getDayEvents(unixtime, eventType) != null) {
			MobileWebApi.sendSuccessMessage(uiHandler);
			return;
		}
		
		MobileWebApi webApi = new MobileWebApi(false, true, "Calendar", context, uiHandler);
		
		HashMap<String, String> eventParameters = new HashMap<String, String>();
		eventParameters.put("module", "calendar");
		eventParameters.put("command", "day");
		eventParameters.put("time", Long.toString(unixtime));
		
		if(eventType == EventType.Events) {
			eventParameters.put("type", "Events");	
		} else if(eventType == EventType.Exhibits) {
			eventParameters.put("type", "Exhibits");
		}
		
		webApi.requestJSONArray(eventParameters, new MobileWebApi.JSONArrayResponseListener(
			new MobileWebApi.DefaultErrorListener(uiHandler), null)  {
			
			@Override
			public void onResponse(JSONArray jArray) {
				List<EventDetailsItem> events = parseDetailArray(jArray);
				putInDayEventsCache(unixtime, eventType, events);
				MobileWebApi.sendSuccessMessage(uiHandler);
			}
		});
	}
	
	private static List<EventDetailsItem> parseDetailArray(JSONArray jArray) {
		ArrayList<EventDetailsItem> events = new ArrayList<EventDetailsItem>();
		for(int i = 0; i < jArray.length(); i++) {
			try {
				JSONObject jItem = (JSONObject) jArray.get(i);
				events.add(parseDetailItem(jItem));
				
			} catch (JSONException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to parsed EventItem");
			}					
		}	
		return events;		
	}
	
	private static EventDetailsItem parseDetailItem(JSONObject jItem) {
        EventDetailsItem item = new EventDetailsItem();
        
        try {
        	item.id = jItem.getInt("id");
        	item.title = jItem.getString("title");
        	item.start = jItem.getLong("start");
	        item.end = jItem.getLong("end");
	        
        	item.owner = optString(jItem, "owner");
        	item.shortloc = optString(jItem, "shortloc");
        	item.location = optString(jItem, "location");
        	item.status = optString(jItem, "status");
        	item.event = optString(jItem, "event");
	        item.cancelled = optString(jItem, "cancelled");	        	        

	        item.infophone = optString(jItem, "infophone");
	        item.infourl = optString(jItem, "infourl");
	        item.description = optString(jItem, "description");
	        
	        
	        
	        // Format Date
	        Date d = new Date();
	        d.setTime(item.start*1000);
			
	        SimpleDateFormat sdf;
	        sdf = new SimpleDateFormat("EEE, MMM d 'at' hh:mm a");        
	        // sdf  = (SimpleDateFormat) SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
	        // sdf  = (SimpleDateFormat) SimpleDateFormat.getDateInstance(SimpleDateFormat.FULL);
	        // sdf  = (SimpleDateFormat) SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG);
	        
	        item.startStr = sdf.format(d);
	        
	        
	        JSONObject jCoords = jItem.optJSONObject("coordinate");
	        if (jCoords!=null) {
	        	item.coordinates = item.new Coord();
	        	item.coordinates.lon = jCoords.getDouble("lon");
	        	item.coordinates.lat = jCoords.getDouble("lat");
				item.coordinates.description = jCoords.optString("description", null);
	        }
	        
    	} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to parsed EventItem");
		}
    	
        return item;
		
	}
	
	private static HashMap<Long, List<Integer>> sEventsIdCache = new HashMap<Long, List<Integer>>();
	private static HashMap<Long, List<Integer>> sExhibitsIdCache = new HashMap<Long, List<Integer>>();
	
	private static List<EventDetailsItem> eventList(List<Integer> eventIds) {
		ArrayList<EventDetailsItem> events = new ArrayList<EventDetailsItem>();
		for(Integer eventId : eventIds) {
			events.add(sBriefEventsCache.get(eventId));
		}
		return events;
	}
	
	private static List<Integer> eventIds(List<EventDetailsItem> events) {
		ArrayList<Integer> eventIds = new ArrayList<Integer>();
		for(EventDetailsItem event : events) {
			sBriefEventsCache.put(event.id, event);
			eventIds.add(event.id);
		}
		return eventIds;
	}
	
	private static void putInDayEventsCache(long unixtime, EventType eventType, List<EventDetailsItem> events) {
		HashMap<Long, List<Integer>> cache = getDayEventsCache(eventType);
		Long timeKey = getDayEventKey(unixtime);
		
		List<Integer> eventIds = eventIds(events);
		cache.put(new Long(timeKey), eventIds);
	}
	
	public static List<EventDetailsItem> getDayEvents(long unixtime, EventType eventType) {
		HashMap<Long, List<Integer>> cache = getDayEventsCache(eventType);
		Long timeKey = getDayEventKey(unixtime);
		List<Integer> eventIds = cache.get(new Long(timeKey));
		
		if(eventIds == null) {
			return null;
		}
		
		return eventList(eventIds);
	}
	
	private static HashMap<Long, List<Integer>> getDayEventsCache(EventType eventType) {
		HashMap<Long, List<Integer>> cache = null;
		if(eventType == EventType.Events) {
			cache = sEventsIdCache;
		} else if(eventType == EventType.Exhibits) {
			cache = sExhibitsIdCache;
		}
		return cache;	
	}
	
	private static Long getDayEventKey(long unixtime) {
		// map all unixtimes for the same day to the same key
		
		unixtime -= TIME_ZONE_OFFSET * 60 * 60;
		long dayUnixtime = unixtime / (24 * 60 * 60);	
		
		dayUnixtime += 12 * 60 * 60;
		return new Long(dayUnixtime);
	}
	
	
	
	/*
	 * Event category related stuff
	 */
	private static List<EventCategoryItem> sCategories = null;
	
	public static void fetchCategories(Context context, final Handler uiHandler) {
		if(getCategories() != null) {
			MobileWebApi.sendSuccessMessage(uiHandler);
			return;
		}
		
		MobileWebApi webApi = new MobileWebApi(false, true, "Calendar", context, uiHandler);
		
		HashMap<String, String> eventParameters = new HashMap<String, String>();
		eventParameters.put("command", "categories");
		eventParameters.put("module", "calendar");
		
		webApi.requestJSONArray(eventParameters, new MobileWebApi.JSONArrayResponseListener(
			new MobileWebApi.DefaultErrorListener(uiHandler), null)  {
			
			@Override
			public void onResponse(JSONArray jArray) {
				sCategories = parseCategoryArray(jArray);				
				MobileWebApi.sendSuccessMessage(uiHandler);
			}
		});
	}
	
	public static List<EventCategoryItem> getCategories() {
		return sCategories;
	}
	
	public static EventCategoryItem getCategory(int categoryId) {
		for(EventCategoryItem categoryItem : sCategories) {
			if(categoryItem.catid == categoryId) {
					return categoryItem;
			}
		}
		return null;
	}
	
	private static List<EventCategoryItem> parseCategoryArray(JSONArray jArray) {
		ArrayList<EventCategoryItem> categories = new ArrayList<EventCategoryItem>();
		
		for(int i = 0; i < jArray.length(); i++) {
			try {
				JSONObject jItem = (JSONObject) jArray.get(i);
				categories.add(parseCategoryItem(jItem));
				
			} catch (JSONException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to parsed Event Categories");
			}					
		}
		return categories;
	}
	
	private static EventCategoryItem parseCategoryItem(JSONObject jItem) {
		EventCategoryItem category = new EventCategoryItem();

		try {

			category.name = jItem.getString("name");
			category.catid = jItem.getInt("catid");
			JSONArray subcategories = jItem.optJSONArray("subcategories");
			if(subcategories != null) {
				category.subcats = parseCategoryArray(subcategories);
			}

		} catch (JSONException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to parsed Event Category");
		}
	
		return category;
	}
	
	public static void fetchCategoryDayEvents(final long unixtime, final int categoryId, Context context, final Handler uiHandler) {
		
		if(getCategoryDayEvents(unixtime, categoryId) != null) {
			MobileWebApi.sendSuccessMessage(uiHandler);
			return;
		}
		
		MobileWebApi webApi = new MobileWebApi(false, true, "Calendar", context, uiHandler);
		
		HashMap<String, String> eventParameters = new HashMap<String, String>();
		eventParameters.put("module", "calendar");
		eventParameters.put("command", "category");
		eventParameters.put("id", Integer.toString(categoryId));
		eventParameters.put("start", Long.toString(unixtime));
		
		webApi.requestJSONArray(eventParameters, new MobileWebApi.JSONArrayResponseListener(
			new MobileWebApi.DefaultErrorListener(uiHandler), null)  {
			
			@Override
			public void onResponse(JSONArray jArray) {
				List<EventDetailsItem> events = parseDetailArray(jArray);
				putInCategoryDayEventsCache(unixtime, categoryId, events);
				MobileWebApi.sendSuccessMessage(uiHandler);
			}
		});
	}
	
	// HashMap with keys of categoryId then day timestamp as a key
	// the values are lists of eventIds, this data structure is a little ugly
	static private HashMap<Integer, HashMap<Long, List<Integer>>> sCategoryDayEventsCache = 
		new HashMap<Integer, HashMap<Long, List<Integer>>>();
	
	private static void putInCategoryDayEventsCache(long unixtime, int categoryId, List<EventDetailsItem> events) {
		Long timeKey = getDayEventKey(unixtime);		
		List<Integer> eventIds = eventIds(events);
		
		if(!sCategoryDayEventsCache.containsKey(categoryId)) {
			sCategoryDayEventsCache.put(categoryId, new HashMap<Long, List<Integer>>());
		}
		
		HashMap<Long, List<Integer>> categoryCache = sCategoryDayEventsCache.get(categoryId);
		categoryCache.put(timeKey, eventIds);		
	}
	
	public static List<EventDetailsItem> getCategoryDayEvents(long unixtime, int categoryId) {
		Long timeKey = getDayEventKey(unixtime);
		if(!sCategoryDayEventsCache.containsKey(categoryId)) {
			return null;
		}
		
		if(!sCategoryDayEventsCache.get(categoryId).containsKey(timeKey)) {
			return null;
		}
		
		List<Integer> eventIds = sCategoryDayEventsCache.get(categoryId).get(timeKey);
		return eventList(eventIds);
	}
	
	
	/*
	 *  Search API call
	 */
	static private HashMap<String, List<Integer>> sSearchCache =
		new HashMap<String, List<Integer>>();
	
	public static void executeSearch(final String searchTerms, final Context context, final Handler uiHandler) {
		
		if(sSearchCache.containsKey(searchTerms)) {
			List<EventDetailsItem> events = executeLocalSearch(searchTerms);
			MobileWebApi.sendSuccessMessage(uiHandler, new SearchResults<EventDetailsItem>(searchTerms, events));
			return;
		}
		
		MobileWebApi webApi = new MobileWebApi(false, true, "Calendar", context, uiHandler);
		
		HashMap<String, String> eventParameters = new HashMap<String, String>();
		eventParameters.put("module", "calendar");
		eventParameters.put("command", "search");
		eventParameters.put("q", searchTerms);
		
		webApi.requestJSONObject(eventParameters, new MobileWebApi.JSONObjectResponseListener(
			new MobileWebApi.DefaultErrorListener(uiHandler), null)  {
			
			@Override
			public void onResponse(JSONObject jObject) {
				try {
					JSONArray jArray;
					jArray = jObject.getJSONArray("events");
					List<EventDetailsItem> events = parseDetailArray(jArray);
					sSearchCache.put(searchTerms, eventIds(events));
					MobileWebApi.sendSuccessMessage(uiHandler, new SearchResults<EventDetailsItem>(searchTerms, events));
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(context, "Failure parsing events search", Toast.LENGTH_SHORT).show();
					MobileWebApi.sendErrorMessage(uiHandler);
				}
			}
		});
	}
	
	public static List<EventDetailsItem> executeLocalSearch(String searchTerms) {
		return eventList(sSearchCache.get(searchTerms));
	}
	
	public static void fetchAcademicCalendar(final int year, final int month, Context context, final Handler uiHandler) {
		
		if(getAcademicCalendar(year, month) != null) {
			MobileWebApi.sendSuccessMessage(uiHandler);
			return;
		}
		
		MobileWebApi webApi = new MobileWebApi(false, true, "Calendar", context, uiHandler);
		
		HashMap<String, String> eventParameters = new HashMap<String, String>();
		eventParameters.put("module", "calendar");
		eventParameters.put("command", "academic");
		eventParameters.put("year", Integer.toString(year));
		eventParameters.put("month", Integer.toString(month));
		
		
		webApi.requestJSONArray(eventParameters, new MobileWebApi.JSONArrayResponseListener(
			new MobileWebApi.DefaultErrorListener(uiHandler), null)  {
			
			@Override
			public void onResponse(JSONArray jArray) {
				List<EventDetailsItem> events = parseDetailArray(jArray);
				putInAcademicCalendarCache(year, month, events);
				MobileWebApi.sendSuccessMessage(uiHandler);
			}
		});
	}
	
	/*
	 * Keys are year such as (2010) then month (0-11)
	 * values are List of eventIds
	 * 
	 */
	private static HashMap<Integer, HashMap<Integer, List<Integer>>> sAcademicCalendarCache =
		new HashMap<Integer, HashMap<Integer, List<Integer>>>();
	
	private static void putInAcademicCalendarCache(int year, int month, List<EventDetailsItem> events) {		
		List<Integer> eventIds = eventIds(events);
		if(!sAcademicCalendarCache.containsKey(year)) {
			sAcademicCalendarCache.put(year, new HashMap<Integer, List<Integer>>());
		}
		sAcademicCalendarCache.get(year).put(month, eventIds);
	}
	
	public static List<EventDetailsItem> getAcademicCalendar(int year, int month) {
		HashMap<Integer, List<Integer>> yearCache = sAcademicCalendarCache.get(year);
		if(yearCache == null) {
			return null;
		}
		
		if(!yearCache.containsKey(month)) {
			return null;
		}
		
		return eventList(yearCache.get(month));
	}
	
	public static EventDetailsItem getBriefEventDetails(int eventId) {
		return sBriefEventsCache.get(eventId);
	}
	
	private static List<Integer> sHolidayIds;
	
	public static void fetchHolidays(Context context, final Handler uiHandler) {
		
		if(sHolidayIds != null) {
			MobileWebApi.sendSuccessMessage(uiHandler);
			return;
		}
		
		MobileWebApi webApi = new MobileWebApi(false, true, "Calendar", context, uiHandler);
		
		HashMap<String, String> eventParameters = new HashMap<String, String>();
		eventParameters.put("module", "calendar");
		eventParameters.put("command", "holidays");		
		
		webApi.requestJSONArray(eventParameters, new MobileWebApi.JSONArrayResponseListener(
			new MobileWebApi.DefaultErrorListener(uiHandler), null)  {
			
			@Override
			public void onResponse(JSONArray jArray) {
				List<EventDetailsItem> events = parseDetailArray(jArray);
				sHolidayIds = eventIds(events);
				MobileWebApi.sendSuccessMessage(uiHandler);
			}
		});
	}
	
	public static List<EventDetailsItem> getHolidays() {
		return eventList(sHolidayIds);
	}
	
	public static void fetchEventDetails(final int eventId, final Context context, final Handler uiHandler) {
		if(sFullEventsCache.containsKey(eventId)) {
			MobileWebApi.sendSuccessMessage(uiHandler);
		}
		
		MobileWebApi webApi = new MobileWebApi(false, true, "Calendar", context, uiHandler);
		
		HashMap<String, String> eventParameters = new HashMap<String, String>();
		eventParameters.put("module", "calendar");
		eventParameters.put("command", "detail");
		eventParameters.put("id", Integer.toString(eventId));
		
		webApi.requestJSONObject(eventParameters, new MobileWebApi.JSONObjectResponseListener(
			new MobileWebApi.DefaultErrorListener(uiHandler), null)  {
				
			@Override
			public void onResponse(JSONObject jObject) {
				EventDetailsItem event = parseDetailItem(jObject);
				sFullEventsCache.put(eventId, event);
				MobileWebApi.sendSuccessMessage(uiHandler);
			}
		});		
	}
	
	public static EventDetailsItem getFullEvent(int eventId) {
		return sFullEventsCache.get(eventId);
	}
	
	public static int getPosition( int eventId, List<EventDetailsItem> items) {
		for(int i = 0; i < items.size(); i++) {
			if(items.get(i).id == eventId) {
				return i;
			}
		}
		return -1;
	}
	
	/*
	 *  So strange the json parser seems to interpret the value null as
	 *  the string "null", so this litte wrapper works-around that issue
	 */
	private static String optString(JSONObject jObject, String fieldName) {
		String value = jObject.optString(fieldName, "");
		
		if(value.equals("null")) {
			return "";
		}
		
		return value;
	}
}
