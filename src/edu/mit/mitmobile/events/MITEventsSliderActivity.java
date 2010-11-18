package edu.mit.mitmobile.events;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import edu.mit.mitmobile.Module;
import edu.mit.mitmobile.R;
import edu.mit.mitmobile.SliderActivity;
import edu.mit.mitmobile.events.EventsModel.EventType;
import edu.mit.mitmobile.objs.EventDetailsItem;

public class MITEventsSliderActivity extends SliderActivity {
	
	private final static String LIST_MODE_KEY = "list_mode";
	
	private final static int EVENTS_DAY_LIST = 0;
	private final static int EXHIBITS_DAY_LIST = 1;
	private final static int CATEGORY_DAY_LIST = 2;
	private final static int SEARCH_LIST = 3;
	private final static int ACADEMIC_LIST = 4;
	private final static int HOLIDAYS_LIST = 5;
	
	private final static String UNIXTIME_KEY = "unixtime";
	
	private final static String CATEGORY_ID_KEY = "category_id";
	
	private final static String EVENT_ID_KEY = "event_id";
	
	private final static String SEARCH_TERMS_KEY = "search_terms";
	
	private final static String YEAR_KEY = "year_key";
	
	private final static String MONTH_KEY = "month_key";
	
	private int mInitialEventId = -1;
	
	private List<EventDetailsItem> mEvents = null;
	private boolean mBriefMode = false; // this is used for events which dont have detail information such as (Academic Calendar)
	private ArrayList<EventDetailsView> eventViews = new ArrayList<EventDetailsView>();
	
	static final int MENU_ADD_TO_CALENDAR = MENU_LAST + 1;
	static final int MENU_SHARE = MENU_LAST + 2;
	
	public static void launchEvents(Context context, int eventId, long unixtime) {
		Intent intent = new Intent(context, MITEventsSliderActivity.class);
		intent.putExtra(LIST_MODE_KEY, EVENTS_DAY_LIST);
		intent.putExtra(EVENT_ID_KEY, eventId);
		intent.putExtra(UNIXTIME_KEY, unixtime);
		context.startActivity(intent);
	}
	
	public static void launchExhibits(Context context, int eventId, long unixtime) {
		Intent intent = new Intent(context, MITEventsSliderActivity.class);
		intent.putExtra(LIST_MODE_KEY, EXHIBITS_DAY_LIST);
		intent.putExtra(EVENT_ID_KEY, eventId);
		intent.putExtra(UNIXTIME_KEY, unixtime);
		context.startActivity(intent);
	}
	
	public static void launchCategory(Context context, int eventId, long unixtime, int categoryId) {
		Intent intent = new Intent(context, MITEventsSliderActivity.class);
		intent.putExtra(LIST_MODE_KEY, CATEGORY_DAY_LIST);
		intent.putExtra(EVENT_ID_KEY, eventId);
		intent.putExtra(UNIXTIME_KEY, unixtime);
		intent.putExtra(CATEGORY_ID_KEY , categoryId);
		context.startActivity(intent);
	}
	
	public static void launchSearchResults(Context context, int eventId, String searchTerms) {
		Intent intent = new Intent(context, MITEventsSliderActivity.class);
		intent.putExtra(LIST_MODE_KEY, SEARCH_LIST);
		intent.putExtra(EVENT_ID_KEY, eventId);
		intent.putExtra(SEARCH_TERMS_KEY, searchTerms);
		context.startActivity(intent);
	}
	
	public static void launchAcademicCalendar(Context context, int eventId, int year, int month) {
		Intent intent = new Intent(context, MITEventsSliderActivity.class);
		intent.putExtra(LIST_MODE_KEY, ACADEMIC_LIST);
		intent.putExtra(EVENT_ID_KEY, eventId);
		intent.putExtra(YEAR_KEY, year);
		intent.putExtra(MONTH_KEY, month);
		context.startActivity(intent);
	}
	
	public static void launchHolidaysCalendar(Context context, int eventId) {
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
    	mInitialEventId = extras.getInt(EVENT_ID_KEY);
    	int listMode = extras.getInt(LIST_MODE_KEY);
    	
    	if(listMode == EVENTS_DAY_LIST) {
    		long unixtime = extras.getLong(UNIXTIME_KEY);
    		mEvents = EventsModel.getDayEvents(unixtime, EventType.Events);
    	} else if(listMode == EXHIBITS_DAY_LIST) {
    		long unixtime = extras.getLong(UNIXTIME_KEY);
    		mEvents = EventsModel.getDayEvents(unixtime, EventType.Exhibits);
    	} else if(listMode == CATEGORY_DAY_LIST) {
    		long unixtime = extras.getLong(UNIXTIME_KEY);
    		int categoryId = extras.getInt(CATEGORY_ID_KEY);
    		mEvents = EventsModel.getCategoryDayEvents(unixtime, categoryId);
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
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		EventDetailsView view = eventViews.get(getPosition());
		
		if(view.hasLoadingCompleted()) {
			menu.add(0, MENU_ADD_TO_CALENDAR, Menu.NONE, "Add to Calendar")
				.setIcon(R.drawable.menu_add_to_calendar);
			menu.add(0, MENU_SHARE, Menu.NONE, "Share")
				.setIcon(R.drawable.menu_share);
		}
		
		return true;
	}

	@Override
	protected Module getModule() {
		return new EventsModule();
	}
	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		menu.add(0, MENU_SEARCH, Menu.NONE, MENU_SEARCH_TITLE)
			.setIcon(R.drawable.menu_search);
	}	
}
