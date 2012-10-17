package edu.mit.mitmobile2.events;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import edu.mit.mitmobile2.AbstractSliderViewAdapter;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.SliderView.ScreenPosition;
import edu.mit.mitmobile2.events.EventsModel.EventType;

public class EventDayListSliderAdapter extends AbstractSliderViewAdapter{

	private long mDayTime; //unix timestamp for currently selected day
	private long mToday;
	
	private EventType mEventType;
	
	private Integer mCategoryID;
	
	private Context mContext;
	
	private SimpleDateFormat sDateFormat = new SimpleDateFormat("MMMM d");
	
	private OnDayChangeListener mDayChangeListener = null;
	
	private static long SECONDS_PER_DAY = 86400;
	
	public EventDayListSliderAdapter(Context context, EventType eventType, long currentDayTime, OnDayChangeListener dayChangeListener) {
		init(context, eventType, currentDayTime, dayChangeListener);
	}

	public EventDayListSliderAdapter(Context context, EventType eventType, long currentDayTime, int categoryID, OnDayChangeListener dayChangeListener) {
		init(context, eventType, currentDayTime, dayChangeListener);
		mCategoryID = categoryID;
	}
	
	private void init(Context context, EventType eventType, long currentDayTime, OnDayChangeListener dayChangeListener) {
		mContext = context;
		mEventType = eventType;
		mDayTime = currentDayTime;
		mToday = mDayTime;
		mDayChangeListener = dayChangeListener;
		
		mDayChangeListener.onDayChangeListener("YESTERDAY", "TODAY", "TOMMOROW");		
	}
	
	public interface OnDayChangeListener {
		public void onDayChangeListener(String previous, String current, String next);
	}
	
	
	@Override
	public boolean hasScreen(ScreenPosition screenPosition) {
		return true;
	}

	@Override
	public SliderInterface getSliderInterface(ScreenPosition screenPosition) {
		long dayTime = mDayTime;
		if (screenPosition == ScreenPosition.Next) {
			dayTime += SECONDS_PER_DAY;
		} else if (screenPosition == ScreenPosition.Previous) {
			dayTime -= SECONDS_PER_DAY;
		}
		if (mCategoryID == null) {
			return EventsListSliderInterface.daysFactory(mContext, mEventType, dayTime);
		} else {
			return EventsListSliderInterface.categoriesFactory(mContext, mCategoryID, mEventType, dayTime);
		}
	}

	@Override
	public void seek(ScreenPosition screenPosition) {
		super.seek(screenPosition);
		if (screenPosition == ScreenPosition.Next) {		
			
			mDayTime += SECONDS_PER_DAY;
			
		} else if (screenPosition == ScreenPosition.Previous) {
			
			mDayTime -= SECONDS_PER_DAY;
		}
		
		mDayChangeListener.onDayChangeListener(dayTitle(-1), dayTitle(0), dayTitle(1));
		
	}	
	
	private String dayTitle(int offset) {
		long dayTime = mDayTime + offset * SECONDS_PER_DAY;
		if (dayTime == mToday) {
			return "TODAY";
		} else if (dayTime == mToday + SECONDS_PER_DAY) {
			return "TOMORROW";
		} else if (dayTime == mToday - SECONDS_PER_DAY) {
			return "YESTERDAY";
		}
		
		return sDateFormat.format(new Date(dayTime*1000));
	}
}
