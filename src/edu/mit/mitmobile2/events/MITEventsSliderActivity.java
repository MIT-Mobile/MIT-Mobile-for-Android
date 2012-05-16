package edu.mit.mitmobile2.events;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderActivity;
import edu.mit.mitmobile2.SliderListNewModuleActivity;
import edu.mit.mitmobile2.events.EventsModel.EventType;
import edu.mit.mitmobile2.objs.EventDetailsItem;

public class MITEventsSliderActivity extends SliderListNewModuleActivity {
	
	private final static String LIST_MODE_KEY = "list_mode";
	
	private final static int EVENTS_DAY_LIST = 0;
	private final static int CATEGORY_DAY_LIST = 2;
	private final static int SEARCH_LIST = 3;
	private final static int ACADEMIC_LIST = 4;
	private final static int HOLIDAYS_LIST = 5;
	
	private final static String UNIXTIME_KEY = "unixtime";
	
	private final static String EVENT_TYPE_KEY = "event_type_id";
	
	private final static String CATEGORY_ID_KEY = "category_id";
	
	private final static String EVENT_ID_KEY = "event_id";
	
	private final static String SEARCH_TERMS_KEY = "search_terms";
	
	private final static String YEAR_KEY = "year_key";
	
	private final static String MONTH_KEY = "month_key";
	
	private String mInitialEventId = null;
	
	private List<EventDetailsItem> mEvents = null;
	private boolean mBriefMode = false; // this is used for events which dont have detail information such as (Academic Calendar)
	private ArrayList<EventDetailsView> eventViews = new ArrayList<EventDetailsView>();
	
	public static void launchEvents(Context context, String eventId, long unixtime, EventType eventType) {
		Intent intent = new Intent(context, MITEventsSliderActivity.class);
		intent.putExtra(LIST_MODE_KEY, EVENTS_DAY_LIST);
		intent.putExtra(EVENT_ID_KEY, eventId);
		intent.putExtra(UNIXTIME_KEY, unixtime);
		intent.putExtra(EVENT_TYPE_KEY, eventType.getTypeId());
		context.startActivity(intent);
	}
	
	public static void launchCategory(Context context, String eventId, long unixtime, int categoryId, EventType eventType) {
		Intent intent = new Intent(context, MITEventsSliderActivity.class);
		intent.putExtra(LIST_MODE_KEY, CATEGORY_DAY_LIST);
		intent.putExtra(EVENT_ID_KEY, eventId);
		intent.putExtra(UNIXTIME_KEY, unixtime);
		intent.putExtra(CATEGORY_ID_KEY , categoryId);
		intent.putExtra(EVENT_TYPE_KEY, eventType.getTypeId());
		context.startActivity(intent);
	}
	
	public static void launchSearchResults(Context context, String eventId, String searchTerms) {
		Intent intent = new Intent(context, MITEventsSliderActivity.class);
		intent.putExtra(LIST_MODE_KEY, SEARCH_LIST);
		intent.putExtra(EVENT_ID_KEY, eventId);
		intent.putExtra(SEARCH_TERMS_KEY, searchTerms);
		context.startActivity(intent);
	}
	
	public static void launchAcademicCalendar(Context context, String eventId, int year, int month) {
		Intent intent = new Intent(context, MITEventsSliderActivity.class);
		intent.putExtra(LIST_MODE_KEY, ACADEMIC_LIST);
		intent.putExtra(EVENT_ID_KEY, eventId);
		intent.putExtra(YEAR_KEY, year);
		intent.putExtra(MONTH_KEY, month);
		context.startActivity(intent);
	}
	
	public static void launchHolidaysCalendar(Context context, String eventId) {
		Intent intent = new Intent(context, MITEventsSliderActivity.class);
		intent.putExtra(LIST_MODE_KEY, HOLIDAYS_LIST);
		intent.putExtra(EVENT_ID_KEY, eventId);
		context.startActivity(intent);
	}
	
	/****************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
    	
    	Bundle extras = getIntent().getExtras();
    	mInitialEventId = extras.getString(EVENT_ID_KEY);
    	int listMode = extras.getInt(LIST_MODE_KEY);
    	
    	EventType eventType = null;
    	if(extras.containsKey(EVENT_TYPE_KEY)) {
    		if(EventsModel.eventTypesLoaded()) {
    			eventType = EventsModel.getEventType(extras.getString(EVENT_TYPE_KEY));
    		} else {
    			// graceful exit
    			finish();
    			return;
    		}
    	}
    	
    	if(listMode == EVENTS_DAY_LIST) {
    		long unixtime = extras.getLong(UNIXTIME_KEY);
    		mEvents = EventsModel.getDayEvents(unixtime, eventType);
    	} else if(listMode == CATEGORY_DAY_LIST) {
    		long unixtime = extras.getLong(UNIXTIME_KEY);
    		int categoryId = extras.getInt(CATEGORY_ID_KEY);
    		mEvents = EventsModel.getCategoryDayEvents(unixtime, categoryId, eventType);
    	} else if(listMode == SEARCH_LIST) {
    		String searchTerms = extras.getString(SEARCH_TERMS_KEY);
    		mEvents = EventsModel.executeLocalSearch(searchTerms);
    	} else if(listMode == ACADEMIC_LIST) {
    		int year = extras.getInt(YEAR_KEY);
    		int month = extras.getInt(MONTH_KEY);
    		mEvents = EventsModel.getAcademicCalendar(year, month);
    		mBriefMode = true;
    	} else if(listMode == HOLIDAYS_LIST) {
    		mEvents = EventsModel.getHolidays();
    		mBriefMode = true;
    	}
    	
    	if(mEvents == null) {
    		// gracefull exit
    		finish();
    		return;
    	}
    	
    	createViews();
	}
	/****************************************************/
    void createViews() {
    	
    	for (EventDetailsItem event : mEvents) {
    		EventDetailsView eventView = new EventDetailsView(this, event, mBriefMode);
    		addScreen(eventView, event.title, "Event Details");    
    		eventViews.add(eventView);
    	}
    	
    	int position = EventsModel.getPosition(mInitialEventId, mEvents);
    	setPosition(position);        
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
    	eventViews = null;
    }
    
    /*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(item.getItemId() == MENU_ADD_TO_CALENDAR) {
			EventDetailsView eventView = eventViews.get(getPosition());
			eventView.addEvent();
			return true;
		} else if(item.getItemId() == MENU_SHARE) {
			EventDetailsView eventView = eventViews.get(getPosition());
			eventView.shareEvent();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	*/
    
	@Override
	protected NewModule getNewModule() {
		return new EventsModule();
	}
	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}
}
