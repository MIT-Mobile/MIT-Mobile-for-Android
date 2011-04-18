package edu.mit.mitmobile2.events;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.MobileWebApi.ServerResponseException;
import edu.mit.mitmobile2.objs.EventCategoryItem;
import edu.mit.mitmobile2.objs.EventDetailsItem;
import edu.mit.mitmobile2.objs.SearchResults;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class EventsModel {

	private static int TIME_ZONE_OFFSET = 5;
	
	private static HashMap<String, EventDetailsItem> sBriefEventsCache = new HashMap<String, EventDetailsItem>();
	private static HashMap<String, EventDetailsItem> sFullEventsCache = new HashMap<String, EventDetailsItem>();
	
	public static class EventType {
		private String mId;
		private String mLongName;
		private String mShortName;
		private boolean mHasCategories;
	
		public EventType(String id, String longName, String shortName, boolean hasCategories) {
			mId = id;
			mLongName = longName;
			mShortName = shortName;
			mHasCategories = hasCategories;
		}
		
		public String getTypeId() {
			return mId;
		}
		
		public String getLongName() {
			return mLongName;
		}
		
		public String getShortName() {
			return mShortName;
		}
		
		public boolean hasCategories() {
			return mHasCategories;
		}
	}
	
	private static ArrayList<EventType> sEventTypes;
	
	public static void fetchEventTypes(Context context, final Handler uiHandler) {
		if(sEventTypes != null) {
			// cache already populated
			MobileWebApi.sendSuccessMessage(uiHandler);
		}
		
		MobileWebApi webApi = new MobileWebApi(false, true, "Calendar", context, uiHandler);
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("module", "calendar");
		params.put("command", "extraTopLevels");
		params.put("version", "2");
	
		webApi.requestJSONArray(params, new MobileWebApi.JSONArrayResponseListener(
				new MobileWebApi.DefaultErrorListener(uiHandler), null) {
			
			@Override
			public void onResponse(JSONArray array) throws ServerResponseException,
					JSONException {
				
				sEventTypes = new ArrayList<EventType>();
				sEventTypes.add(new EventType("Events", "Today's Events", "Events", false));
				
				for(int i = 0; i < array.length(); i++) {
					JSONObject eventType = array.getJSONObject(i);
					sEventTypes.add(new EventType(
						eventType.getString("type"),
						eventType.getString("longName"),
						eventType.getString("shortName"),
						eventType.getBoolean("hasCategories")
					));
				}
				
				MobileWebApi.sendSuccessMessage(uiHandler);
			}
		});
	}
	
	public static List<EventType> getEventTypes() {
		return sEventTypes;
	}
	
	public static boolean eventTypesLoaded() {
		return sEventTypes != null;
	}
	
	public static EventType getEventType(String eventTypeId) {
		for(EventType eventType : sEventTypes) {
			if(eventType.getTypeId().equals(eventTypeId)) {
				return eventType;
			}
		}
		return null;
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
		eventParameters.put("type", eventType.getTypeId());
		
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
        	item.id = jItem.getString("id");
        	item.title = jItem.getString("title");
        	item.start = jItem.getLong("start");
	        item.end = optLong(jItem, "end");
	        
        	item.owner = optString(jItem, "owner");
        	item.shortloc = optString(jItem, "shortloc");
        	item.location = optString(jItem, "location");
        	item.status = optString(jItem, "status");
        	item.event = optString(jItem, "event");
	        item.cancelled = optString(jItem, "cancelled");	        	        

	        item.infophone = optString(jItem, "infophone");
	        item.infourl = optString(jItem, "infourl");
	        item.description = optString(jItem, "description");
	        
	        
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
	
	@SuppressWarnings("serial")
	private static class DayEventsIdsCache extends  HashMap<Long, List<String>> {};
	
	private static HashMap<String, DayEventsIdsCache> sEventsIdCache = new HashMap<String, DayEventsIdsCache>();
	
	private static List<EventDetailsItem> eventList(List<String> eventIds) {
		ArrayList<EventDetailsItem> events = new ArrayList<EventDetailsItem>();
		for(String eventId : eventIds) {
			events.add(sBriefEventsCache.get(eventId));
		}
		return events;
	}
	
	private static List<String> eventIds(List<EventDetailsItem> events) {
		ArrayList<String> eventIds = new ArrayList<String>();
		for(EventDetailsItem event : events) {
			sBriefEventsCache.put(event.id, event);
			eventIds.add(event.id);
		}
		return eventIds;
	}
	
	private static void putInDayEventsCache(long unixtime, EventType eventType, List<EventDetailsItem> events) {
		DayEventsIdsCache cache = getDayEventsCache(eventType);
		Long timeKey = getDayEventKey(unixtime);
		
		List<String> eventIds = eventIds(events);
		cache.put(new Long(timeKey), eventIds);
	}
	
	public static List<EventDetailsItem> getDayEvents(long unixtime, EventType eventType) {
		DayEventsIdsCache cache = getDayEventsCache(eventType);
		Long timeKey = getDayEventKey(unixtime);
		List<String> eventIds = cache.get(new Long(timeKey));
		
		if(eventIds == null) {
			return null;
		}
		
		return eventList(eventIds);
	}
	
	private static DayEventsIdsCache getDayEventsCache(EventType eventType) {
		DayEventsIdsCache cache = sEventsIdCache.get(eventType.getTypeId());	
		if(cache == null) {
			cache = new DayEventsIdsCache();
			sEventsIdCache.put(eventType.getTypeId(), cache);
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
	private static HashMap<String, List<EventCategoryItem>> sCategories = new HashMap<String, List<EventCategoryItem>>();
	
	public static void fetchCategories(Context context, Handler uiHandler) {
		fetchCategories(context, getEventType("Events"), uiHandler);
	}
	
	public static void fetchCategories(Context context, final EventType type, final Handler uiHandler) {
		if(getCategories() != null) {
			MobileWebApi.sendSuccessMessage(uiHandler);
			return;
		}
		
		MobileWebApi webApi = new MobileWebApi(false, true, "Calendar", context, uiHandler);
		
		HashMap<String, String> eventParameters = new HashMap<String, String>();
		eventParameters.put("command", "categories");
		eventParameters.put("module", "calendar");
		eventParameters.put("type", type.getTypeId());
		
		webApi.requestJSONArray(eventParameters, new MobileWebApi.JSONArrayResponseListener(
			new MobileWebApi.DefaultErrorListener(uiHandler), null)  {
			
			@Override
			public void onResponse(JSONArray jArray) {
				sCategories.put(type.getTypeId(), parseCategoryArray(jArray));				
				MobileWebApi.sendSuccessMessage(uiHandler);
			}
		});
	}
	
	public static List<EventCategoryItem> getCategories(EventType type) {
		return sCategories.get(type.getTypeId());
	}
	
	public static List<EventCategoryItem> getCategories() {
		return sCategories.get("Events");
	}
	
	public static EventCategoryItem getCategory(int categoryId) {
		for(EventCategoryItem categoryItem : getCategories()) {
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
	static private HashMap<Integer, HashMap<Long, List<String>>> sCategoryDayEventsCache = 
		new HashMap<Integer, HashMap<Long, List<String>>>();
	
	private static void putInCategoryDayEventsCache(long unixtime, int categoryId, List<EventDetailsItem> events) {
		Long timeKey = getDayEventKey(unixtime);		
		List<String> eventIds = eventIds(events);
		
		if(!sCategoryDayEventsCache.containsKey(categoryId)) {
			sCategoryDayEventsCache.put(categoryId, new HashMap<Long, List<String>>());
		}
		
		HashMap<Long, List<String>> categoryCache = sCategoryDayEventsCache.get(categoryId);
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
		
		List<String> eventIds = sCategoryDayEventsCache.get(categoryId).get(timeKey);
		return eventList(eventIds);
	}
	
	
	/*
	 *  Search API call
	 */
	static private HashMap<String, List<String>> sSearchCache =
		new HashMap<String, List<String>>();
	
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
					ArrayList<EventDetailsItem> events = new ArrayList<EventDetailsItem>();
					events.addAll(parseDetailArray(jArray));
					Collections.sort(events, sCompareEventTimes);
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
	
	private static Comparator<EventDetailsItem> sCompareEventTimes = new Comparator<EventDetailsItem>() {
		@Override
		public int compare(EventDetailsItem event1, EventDetailsItem event2) {
			Long time1 = event1.start;
			Long time2 = event2.start;
			return time1.compareTo(time2);
		}		
	};
	
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
	private static HashMap<Integer, HashMap<Integer, List<String>>> sAcademicCalendarCache =
		new HashMap<Integer, HashMap<Integer, List<String>>>();
	
	private static void putInAcademicCalendarCache(int year, int month, List<EventDetailsItem> events) {		
		List<String> eventIds = eventIds(events);
		if(!sAcademicCalendarCache.containsKey(year)) {
			sAcademicCalendarCache.put(year, new HashMap<Integer, List<String>>());
		}
		sAcademicCalendarCache.get(year).put(month, eventIds);
	}
	
	public static List<EventDetailsItem> getAcademicCalendar(int year, int month) {
		HashMap<Integer, List<String>> yearCache = sAcademicCalendarCache.get(year);
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
	
	private static List<String> sHolidayIds;
	
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
	
	public static void fetchEventDetails(final String eventId, final Context context, final Handler uiHandler) {
		if(sFullEventsCache.containsKey(eventId)) {
			MobileWebApi.sendSuccessMessage(uiHandler);
		}
		
		MobileWebApi webApi = new MobileWebApi(false, true, "Calendar", context, uiHandler);
		
		HashMap<String, String> eventParameters = new HashMap<String, String>();
		eventParameters.put("module", "calendar");
		eventParameters.put("command", "detail");
		eventParameters.put("id", eventId);
		
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
	
	public static EventDetailsItem getFullEvent(String eventId) {
		return sFullEventsCache.get(eventId);
	}
	
	public static int getPosition(String eventId, List<EventDetailsItem> items) {
		for(int i = 0; i < items.size(); i++) {
			if(items.get(i).id.equals(eventId)) {
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
	
	private static Long optLong(JSONObject jObject, String fieldName) {
		if(jObject.has(fieldName)) {
			try {
				return jObject.getLong(fieldName);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
