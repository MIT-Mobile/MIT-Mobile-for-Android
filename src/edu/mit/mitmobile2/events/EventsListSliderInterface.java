package edu.mit.mitmobile2.events;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.events.EventsModel.EventType;
import edu.mit.mitmobile2.objs.EventDetailsItem;
import edu.mit.mitmobile2.objs.EventDetailsItem.TimeSummaryMode;

public class EventsListSliderInterface implements SliderInterface {
	
	TextView mDayEventsEmpty;
	ListView mDayListView;
	FullScreenLoader mLoadingView;
	
	private long mDayTime = -1;
	private Context mContext;
	private EventType mEventType = null;
	private int mCategoryId = -1;
	
	private boolean mAcademicCalendar = false;
	private int mMonth = -1;
	private int mYear = -1;
	
	private boolean mLoadingCompleted = false;

	private EventsListSliderInterface() {
		
	}
	
	/*
	 * Look up events or exhibits by day (constructor)
	 */
	public static EventsListSliderInterface daysFactory(Context context, EventType eventType, long dayTime) {
		EventsListSliderInterface sliderInterface = new EventsListSliderInterface();
		sliderInterface.mContext = context;
		sliderInterface.mDayTime = dayTime;
		sliderInterface.mEventType = eventType;
		return sliderInterface;
	}

	/*
	 * Look up by category and day (constructor)
	 */
	public static EventsListSliderInterface categoriesFactory(Context context, int categoryId, long dayTime) {
		EventsListSliderInterface sliderInterface = new EventsListSliderInterface();
		sliderInterface.mContext = context;
		sliderInterface.mDayTime = dayTime;
		sliderInterface.mCategoryId = categoryId;
		return sliderInterface;
	}
	
	/*
	 * Look up events from academic calendar
	 */
	public static EventsListSliderInterface academicCalendarFactory(Context context, int year, int month) {
		EventsListSliderInterface sliderInterface = new EventsListSliderInterface();
		sliderInterface.mContext = context;
		sliderInterface.mYear = year;
		sliderInterface.mMonth = month;
		sliderInterface.mAcademicCalendar = true;
		return sliderInterface;
	}

	@Override
	public View getView() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.events_day_list, null);
		
		mDayEventsEmpty = (TextView) view.findViewById(R.id.eventsDayListEmptyTV);
		mLoadingView = (FullScreenLoader) view.findViewById(R.id.eventsDayListLoader);
		mDayListView = (ListView) view.findViewById(R.id.eventsDayList);
		mDayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View row, int position, long arg3) {
				EventDetailsItem event = (EventDetailsItem) adapterView.getItemAtPosition(position);
				
				if(mEventType != null) {
					MITEventsSliderActivity.launchEvents(mContext, event.id, mDayTime, mEventType);
				} else if(mCategoryId > -1) {
					MITEventsSliderActivity.launchCategory(mContext, event.id, mDayTime, mCategoryId);
				} else if(mAcademicCalendar) {
					MITEventsSliderActivity.launchAcademicCalendar(mContext, event.id, mYear, mMonth);
				}
			}
		});
		
		return view;
	}

	@Override
	public void onSelected() {
		// when selected need to download events for the day
		if(mLoadingCompleted) {
			// exit early
			return;
		}
		
		mLoadingView.setVisibility(View.VISIBLE);
		mLoadingView.showLoading();
		if(mEventType != null) {
			EventsModel.fetchDayEvents(mDayTime, mEventType, mContext, uiHandler());
		} else if(mCategoryId > -1) {
			EventsModel.fetchCategoryDayEvents(mDayTime, mCategoryId, mContext, uiHandler());
		} else if(mAcademicCalendar) {
			EventsModel.fetchAcademicCalendar(mYear, mMonth, mContext, uiHandler());
		}
	}

	private Handler uiHandler() {
		return new Handler() {						
			@Override
			public void handleMessage(Message message) {
				if(message.arg1 == MobileWebApi.SUCCESS) {
					mLoadingCompleted = true;
					
					List<EventDetailsItem> events = null;
					if(mEventType != null) {
						events = EventsModel.getDayEvents(mDayTime, mEventType);
					} else if(mCategoryId > -1) {
						events = EventsModel.getCategoryDayEvents(mDayTime, mCategoryId);
					} else if(mAcademicCalendar) {
						events = EventsModel.getAcademicCalendar(mYear, mMonth);
					}
					
					TimeSummaryMode timeSummaryMode = mAcademicCalendar ? EventDetailsItem.SHORT_DAYS_ONLY : EventDetailsItem.TIMES_ONLY;
					EventsArrayAdapter arrayAdapter = new EventsArrayAdapter(mContext, R.layout.events_row, 0, events, timeSummaryMode);
				
					mLoadingView.setVisibility(View.GONE);
					if(events.size() > 0 ) {
						mDayListView.setVisibility(View.VISIBLE);
						mDayListView.setAdapter(arrayAdapter);
					} else {
						mDayEventsEmpty.setVisibility(View.VISIBLE);
					}
				} else {
					mLoadingView.showError();
				}
			}
		};
	}

	@Override
	public void updateView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LockingScrollView getVerticalScrollView() {
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}
}
