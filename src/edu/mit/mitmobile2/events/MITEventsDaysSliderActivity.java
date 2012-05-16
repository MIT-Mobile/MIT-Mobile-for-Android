package edu.mit.mitmobile2.events;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;

import edu.mit.mitmobile2.MITSliderTitleBar;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderActivity;
import edu.mit.mitmobile2.SliderNewModuleActivity;
import edu.mit.mitmobile2.SliderView;
import edu.mit.mitmobile2.SliderView.Adapter;
import edu.mit.mitmobile2.events.EventDayListSliderAdapter.OnDayChangeListener;
import edu.mit.mitmobile2.events.EventsModel.EventType;

public class MITEventsDaysSliderActivity extends SliderNewModuleActivity implements OnDayChangeListener {
	
	final static String LIST_TYPE_KEY = "list_type";
	final static int STANDARD_LIST = 0;
	final static int LIST_BY_CATEGORY = 1;
	
	final static String CATEGORY_ID_KEY = "category_id";
	final static String CATEGORY_NAME_KEY = "category_name";
	final static String START_TIME_KEY = "start_time";
	
	final static String EVENT_TYPE_KEY = "event_type";
	
	private long mCurrentTime = System.currentTimeMillis();
	private long mStartTime;
	
	private SimpleDateFormat sDateFormat = new SimpleDateFormat("MMMM d");
	
	private EventType mEventType = null;
	
	private int mCategoryId = -1;
	private String mCategoryName = null;
	
	private SliderView.Adapter mSliderAdapter;
	
	public static void launch(Context context, EventType eventType) {	
		launchEventType(context, eventType.getTypeId(), null);
	}
	
	public static void launchEventType(Context context, String eventType, Long startTime) {	
		Intent intent = new Intent(context, MITEventsDaysSliderActivity.class);
		intent.putExtra(EVENT_TYPE_KEY, eventType);
		intent.putExtra(LIST_TYPE_KEY, STANDARD_LIST);
		if(startTime != null) {
			intent.putExtra(START_TIME_KEY, startTime);
		}
		context.startActivity(intent);
	}
	
	public static void launchCategory(Context context, int categoryId, String categoryName, String eventType, Long startTime) {	
		Intent intent = new Intent(context, MITEventsDaysSliderActivity.class);
		intent.putExtra(LIST_TYPE_KEY, LIST_BY_CATEGORY);
		intent.putExtra(CATEGORY_ID_KEY, categoryId);
		intent.putExtra(CATEGORY_NAME_KEY, categoryName);
		intent.putExtra(EVENT_TYPE_KEY, eventType);
		if(startTime != null) {
			intent.putExtra(START_TIME_KEY, startTime);
		}
		context.startActivity(intent);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final Bundle extras = getIntent().getExtras();
		
		mStartTime = extras.getLong(START_TIME_KEY, System.currentTimeMillis());
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
			addSecondaryTitle(mCategoryName);
		} else if(mEventType != null) {
			addSecondaryTitle(mEventType.getShortName());
		}		
		
		mSliderAdapter = new EventDayListSliderAdapter(this, mEventType, mCurrentTime/1000, mCategoryId, this);
		reloadAdapter();
	}
	
	@Override
	protected NewModule getNewModule() {
		return new EventsModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected Adapter getSliderAdapter() {
		return mSliderAdapter;
	}


	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}

	private String mPrevious, mCurrent, mNext;
	@Override
	public void onDayChangeListener(String previous, String current, String next) {
		mPrevious = previous;
		mCurrent = current;
		mNext = next;
	}
	
	@Override
	protected String getPreviousTitle() {
		return mPrevious;
	}

	@Override
	protected String getCurrentHeaderTitle() {
		return mCurrent;
	}
	
	@Override
	protected String getNextTitle() {
		return mNext;
	}
	
}
