package edu.mit.mitmobile.events;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import edu.mit.mitmobile.Module;
import edu.mit.mitmobile.R;
import edu.mit.mitmobile.SliderActivity;
import edu.mit.mitmobile.events.EventsModel.EventType;

public class MITEventsDaysSliderActivity extends SliderActivity {
	
	final static String LIST_TYPE_KEY = "list_type";
	final static int STANDARD_LIST = 0;
	final static int LIST_BY_CATEGORY = 1;
	
	final static String CATEGORY_ID_KEY = "category_id";
	final static String CATEGORY_NAME_KEY = "category_name";
	
	final static String EVENT_TYPE_KEY = "event_type";
	final static int EVENT_TYPE_EVENTS = 0;
	final static int EVENT_TYPE_EXHIBITS = 1;
	final static int DAYS_PAST_FUTURE = 30;
	
	private static final long TWENTY_FOUR_HOURS = 24 * 60 *  60 * 1000;
	
	private long mCurrentTime = System.currentTimeMillis();
	
	private SimpleDateFormat sDateFormat = new SimpleDateFormat("MMMM d");
	
	private EventType mEventType = null;
	
	private int mCategoryId = -1;
	private String mCategoryName = null;
	
	public static void launch(Context context, EventType eventType) {
		int eventsType = -1;
		if(eventType == EventType.Events) {
			eventsType = EVENT_TYPE_EVENTS;
		} else if(eventType == EventType.Exhibits) {
			eventsType = EVENT_TYPE_EXHIBITS;
		}
	
		Intent intent = new Intent(context, MITEventsDaysSliderActivity.class);
		intent.putExtra(EVENT_TYPE_KEY, eventsType);
		intent.putExtra(LIST_TYPE_KEY, STANDARD_LIST);
		context.startActivity(intent);
	}
	
	public static void launchCategory(Context context, int categoryId, String categoryName) {	
		Intent intent = new Intent(context, MITEventsDaysSliderActivity.class);
		intent.putExtra(LIST_TYPE_KEY, LIST_BY_CATEGORY);
		intent.putExtra(CATEGORY_ID_KEY, categoryId);
		intent.putExtra(CATEGORY_NAME_KEY, categoryName);
		context.startActivity(intent);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		
		if(extras.getInt(LIST_TYPE_KEY) == STANDARD_LIST) {
			if(extras.getInt(EVENT_TYPE_KEY) == EVENT_TYPE_EVENTS) {
				mEventType = EventType.Events;
			} else if(extras.getInt(EVENT_TYPE_KEY) == EVENT_TYPE_EXHIBITS) {
				mEventType = EventType.Exhibits;
			}
		} else {
			mCategoryId = extras.getInt(CATEGORY_ID_KEY);
			mCategoryName = extras.getString(CATEGORY_NAME_KEY);
		}
				
		setJumpTitle("Go to Date", R.drawable.menu_go_to_date);
		
		createViews();
	}
	
	protected void createViews() {
		if(mEventType == EventType.Events) {
			useSubtitles("Events");
		} else if(mEventType == EventType.Exhibits) {
			useSubtitles("Exhibits");
		} else if(mCategoryName != null) {
			useSubtitles(mCategoryName);
		}
		
		// create views for today, and a fixed number of days in the past and future
		for(long i = -DAYS_PAST_FUTURE; i <= DAYS_PAST_FUTURE; i++) {
			long dayTime = mCurrentTime + i * TWENTY_FOUR_HOURS;
			
			EventsDaySliderInterface sliderInterface = null;
			if(mEventType != null) {
				sliderInterface	= EventsDaySliderInterface.daysFactory(this, mEventType, dayTime/1000);
			} else if(mCategoryId > -1) {
				sliderInterface = EventsDaySliderInterface.categoriesFactory(this, mCategoryId, dayTime/1000);
			}
			
			String dayTitle = null;
			if(i == 0) {
				dayTitle = "Today";
			} else if(i == 1) {
				dayTitle = "Tommorrow";
			} else if(i == -1) {
				dayTitle = "Yesterday";
			} else {
				dayTitle = sDateFormat.format(new Date(dayTime));
			}
			
			addScreen(sliderInterface, dayTitle, dayTitle);
		}
		
		// set the position to today
		setPosition(DAYS_PAST_FUTURE);
		
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
		prepareJumpOptionsMenu(menu);
		
		menu.add(0, MENU_SEARCH, Menu.NONE, MENU_SEARCH_TITLE)
			.setIcon(R.drawable.menu_search);
	}
}
