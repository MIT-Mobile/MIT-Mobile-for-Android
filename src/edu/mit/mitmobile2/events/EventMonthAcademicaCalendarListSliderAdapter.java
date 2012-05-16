package edu.mit.mitmobile2.events;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import edu.mit.mitmobile2.AbstractSliderViewAdapter;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.SliderView.ScreenPosition;

public class EventMonthAcademicaCalendarListSliderAdapter extends AbstractSliderViewAdapter {
	
	private int mSelectedMonth;  
	private int mSelectedYear; 
	
	private Context mContext;
	
	OnMonthChangeListener mMonthChangeListener;
	
	public interface OnMonthChangeListener {
		public void onMonthChange(EventMonthAcademicaCalendarListSliderAdapter adapter);
	}
	
	EventMonthAcademicaCalendarListSliderAdapter(Context context, long currentDayTime,OnMonthChangeListener monthChangeListener) {
		mContext = context;
		mMonthChangeListener = monthChangeListener;
		
		Date date = new Date(currentDayTime*1000);
	
		String monthString = new SimpleDateFormat("M").format(date);
		mSelectedMonth = Integer.parseInt(monthString)-1;
		
		String yearString = new SimpleDateFormat("yyyy").format(date);
		mSelectedYear = Integer.parseInt(yearString);
		
		mMonthChangeListener.onMonthChange(this);
	}
	
	@Override
	public boolean hasScreen(ScreenPosition screenPosition) {
		return true;
	}
	
	@Override
	public SliderInterface getSliderInterface(ScreenPosition screenPosition) {
		return EventsListSliderInterface.academicCalendarFactory(
				mContext, getYear(screenPosition), getMonth(screenPosition));
	}
	
	@Override
	public void seek(ScreenPosition screenPosition) {
		super.seek(screenPosition);
		if (screenPosition == ScreenPosition.Next) {
			mSelectedMonth = (mSelectedMonth + 1) % 12;
			if (mSelectedMonth == 0) {
				mSelectedYear++;
			}
		} else if (screenPosition == ScreenPosition.Previous) {
			if (mSelectedMonth == 0) {
				mSelectedYear--;
			}
			mSelectedMonth = (mSelectedMonth - 1 + 12) % 12;
		}
		
		mMonthChangeListener.onMonthChange(this);
	}
	
	private int getMonth(ScreenPosition screenPosition) {
		int month = mSelectedMonth + 12;
		if (screenPosition == ScreenPosition.Next) {
			month++;
		} else if (screenPosition == ScreenPosition.Previous) {
			month--;
		}
		return month % 12;
	}
	
	private int getYear(ScreenPosition screenPosition) {
		int year = mSelectedYear;
		if (screenPosition == ScreenPosition.Next) {
			if (mSelectedMonth == 12 - 1) {
				year++;
			}
		} else if (screenPosition == ScreenPosition.Previous) {
			if (mSelectedMonth == 0) {
				year--;
			}
		}
		return year;
	}
	
	public String monthTitle(ScreenPosition screenPosition, boolean fullTitle) {
		String title = new DateFormatSymbols().getMonths()[getMonth(screenPosition)];
		if (fullTitle) {
			title += " " + getYear(screenPosition);
		}
		return title;
	}
}
