package edu.mit.mitmobile2.events;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;

import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderActivity;
import edu.mit.mitmobile2.events.EventsModel.EventType;

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
		launchEventType(context, eventType.getTypeId());
	}
	
	public static void launchEventType(Context context, String eventType) {	
		Intent intent = new Intent(context, MITEventsDaysSliderActivity.class);
		intent.putExtra(EVENT_TYPE_KEY, eventType);
		intent.putExtra(LIST_TYPE_KEY, STANDARD_LIST);
		context.startActivity(intent);
	}
	
	public static void launchCategory(Context context, int categoryId, String categoryName, String eventType) {	
		Intent intent = new Intent(context, MITEventsDaysSliderActivity.class);
		intent.putExtra(LIST_TYPE_KEY, LIST_BY_CATEGORY);
		intent.putExtra(CATEGORY_ID_KEY, categoryId);
		intent.putExtra(CATEGORY_NAME_KEY, categoryName);
		intent.putExtra(EVENT_TYPE_KEY, eventType);
		context.startActivity(intent);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final Bundle extras = getIntent().getExtras();
		
		setJumpTitle("Go to Date", R.drawable.menu_go_to_date);
		
		if(extras.getInt(LIST_TYPE_KEY) == LIST_BY_CATEGORY) {
			mCategoryId = extras.getInt(CATEGORY_ID_KEY);
			mCategoryName = extras.getString(CATEGORY_NAME_KEY);
		}
		
		if(EventsModel.eventTypesLoaded()) {
			mEventType = EventsModel.getEventType(extras.getString(EVENT_TYPE_KEY));
			createViews();
		} else {
			// need to load the event types
			showLoading("Events");
			EventsModel.fetchEventTypes(this, new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if(msg.arg1 == MobileWebApi.SUCCESS) {
						mEventType = EventsModel.getEventType(extras.getString(EVENT_TYPE_KEY));
						showLoadingCompleted();
						createViews();
					} else {
						showLoadingError();
					}
				}
			});
		}
	}
	
	protected void createViews() {
		if(mCategoryName != null) {
			useSubtitles(mCategoryName);
		} else if(mEventType != null) {
			useSubtitles(mEventType.getShortName());
		}
		
		// create views for today, and a fixed number of days in the past and future
		for(long i = -DAYS_PAST_FUTURE; i <= DAYS_PAST_FUTURE; i++) {
			long dayTime = mCurrentTime + i * TWENTY_FOUR_HOURS;
			
			EventsListSliderInterface sliderInterface = null;
			if(mCategoryId < 0) {
				sliderInterface	= EventsListSliderInterface.daysFactory(this, mEventType, dayTime/1000);
			} else {
				sliderInterface = EventsListSliderInterface.categoriesFactory(this, mCategoryId, mEventType, dayTime/1000);
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
