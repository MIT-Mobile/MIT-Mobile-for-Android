package edu.mit.mitmobile2.dining;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.Context;
import android.view.View;

import edu.mit.mitmobile2.SliderView;
import edu.mit.mitmobile2.SliderView.ScreenPosition;
import edu.mit.mitmobile2.dining.DiningMealIterator.MealOrEmptyDay;

public abstract class DiningHouseAbstractSliderAdapter implements SliderView.Adapter {

	abstract protected View viewForMealOrDay(MealOrEmptyDay mealOrDay);
	
	private Context mContext;
	private DiningMealIterator mMealIterator;
	
	private String mCurrentDateString;
	private String mTommorowDateString;
	private String mYesterdayDateString;
	
	private DateFormat mFormat;
	private Date mCurrentDate;

	
	public DiningHouseAbstractSliderAdapter(Context context, long currentTime) {
		mContext = context;
		mCurrentDate = new Date(currentTime);
		mFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
		mFormat.setCalendar(new GregorianCalendar());
		mCurrentDateString = mFormat.format(mCurrentDate);
		
		Calendar tomorow = new GregorianCalendar();
		tomorow.setTime(mCurrentDate);
		tomorow.add(Calendar.DATE, 1);
		mTommorowDateString =  mFormat.format(tomorow.getTime());
		
		Calendar yesterday = new GregorianCalendar();
		yesterday.setTime(mCurrentDate);
		yesterday.add(Calendar.DATE, -1);
		mYesterdayDateString =  mFormat.format(yesterday.getTime());
	}
	
	protected void setMealIterator(DiningMealIterator mealIterator) {
		mMealIterator = mealIterator;
	}
	
	public Calendar getSelectedDate() {
		MealOrEmptyDay mealOrEmptyDay = mMealIterator.getCurrent();
		return mealOrEmptyDay.getDay();
	}
	
	public CharSequence getCurrentTitle() {
		MealOrEmptyDay mealOrEmptyDay = mMealIterator.getCurrent();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("LLLL d", Locale.US);
		Calendar day = mealOrEmptyDay.getDay();
		String dateString = mFormat.format(day.getTime());
		
		if (!mealOrEmptyDay.isEmpty()) {
			String title = dateFormat.format(day.getTime());
			String mealName = mealOrEmptyDay.getCapitalizedMealName();
			title = mealName + ", " + title;
			
			// check if the meal is today/tomorrow/yesterday
			if (mCurrentDateString.equals(dateString)) {
				title = "Today's " + title;
			} else if (mTommorowDateString.equals(dateString)) {
				title = "Tomorow's " + title;
			} else if (mYesterdayDateString.equals(dateString)) {
				title = "Yesterday's " + title;
			}
			
			return title;
		} else {
			return dateFormat.format(day.getTime());
		}
	}
	
	@Override
	public void destroyScreen(ScreenPosition screenPosition) { }

	@Override
	public void seek(ScreenPosition screenPosition) {
		switch (screenPosition) {
			case Previous:
				mMealIterator.moveToPrevious();
				break;
				
			case Next:
				mMealIterator.moveToNext();
				break;
				
			case Current:
				// nothing to do
				break;
		}
	}

	@Override
	public boolean hasScreen(ScreenPosition screenPosition) {
		switch (screenPosition) {
			case Previous:
				return mMealIterator.hasPrevious();
				
			case Current:
				return true;
				
			case Next:
				return mMealIterator.hasNext();			
		}
		return false;
	}

	@Override
	public View getScreen(ScreenPosition screenPosition) {
		MealOrEmptyDay mealOrEmptyDay = null;
		switch (screenPosition) {
			case Previous:
				mealOrEmptyDay = mMealIterator.getPrevious();
				break;
			
			case Current:
				mealOrEmptyDay = mMealIterator.getCurrent();
				break;
			
			case Next:
				mealOrEmptyDay = mMealIterator.getNext();
				break;
		}
		
		return viewForMealOrDay(mealOrEmptyDay);		
	}
	
	@Override
	public void destroy() { }
}
