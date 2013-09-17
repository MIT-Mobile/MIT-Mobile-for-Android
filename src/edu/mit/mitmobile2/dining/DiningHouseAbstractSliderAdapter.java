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
import edu.mit.mitmobile2.dining.DiningModel.HouseDiningHall;

public abstract class DiningHouseAbstractSliderAdapter implements SliderView.Adapter {

	abstract protected View viewForMealOrDay(MealOrEmptyDay mealOrDay);
	
	private Context mContext;
	private DiningMealIterator mMealIterator;
	
	private String mCurrentDateString;
	private String mTomorrowDateString;
	private String mYesterdayDateString;
	
	private DateFormat mFormat;
	private Date mCurrentDate;

	
	public DiningHouseAbstractSliderAdapter(Context context) {
		mContext = context;
		long currentTime = DiningModel.currentTimeMillis();		
		mCurrentDate = new Date(currentTime);
		mFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
		mFormat.setCalendar(new GregorianCalendar());
		mCurrentDateString = mFormat.format(mCurrentDate);
		
		Calendar tomorrow = new GregorianCalendar();
		tomorrow.setTime(mCurrentDate);
		tomorrow.add(Calendar.DATE, 1);
		mTomorrowDateString =  mFormat.format(tomorrow.getTime());
		
		Calendar yesterday = new GregorianCalendar();
		yesterday.setTime(mCurrentDate);
		yesterday.add(Calendar.DATE, -1);
		mYesterdayDateString =  mFormat.format(yesterday.getTime());
	}
	
	protected void setMealIterator(DiningMealIterator mealIterator) {
		mMealIterator = mealIterator;
	}
	
	public Calendar getSelectedDate(HouseDiningHall house) {
		MealOrEmptyDay mealOrEmptyDay = mMealIterator.getCurrent();
		return mealOrEmptyDay.getDay(house);
	}
	
	public CharSequence getCurrentTitle() {
		MealOrEmptyDay mealOrEmptyDay = mMealIterator.getCurrent();
		
		SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEEE", Locale.US);
		SimpleDateFormat dateFormat = new SimpleDateFormat("LLLL d", Locale.US);
		Calendar day = mealOrEmptyDay.getDay();
		String dateString = mFormat.format(day.getTime());

		String dayName = dayNameFormat.format(day.getTime());		
		// check for today/tomorrow/yesterday
		if (mCurrentDateString.equals(dateString)) {
			dayName = "Today";
		} else if (mTomorrowDateString.equals(dateString)) {
			dayName = "Tomorrow";
		} else if (mYesterdayDateString.equals(dateString)) {
			dayName = "Yesterday";
		}
		
		if (!mealOrEmptyDay.isEmpty()) {
			String title = dateFormat.format(day.getTime());
			String mealName = mealOrEmptyDay.getCapitalizedMealName();
			title = mealName + ", " + title;
			
			return dayName + "'s " + title;			
		} else {
			return dayName + ", " + dateFormat.format(day.getTime());
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
