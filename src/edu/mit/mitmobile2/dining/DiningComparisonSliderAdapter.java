package edu.mit.mitmobile2.dining;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderView;
import edu.mit.mitmobile2.SliderView.ScreenPosition;
import edu.mit.mitmobile2.dining.DiningMealIterator.MealOrEmptyDay;
import edu.mit.mitmobile2.dining.DiningModel.HouseDiningHall;
import edu.mit.mitmobile2.dining.DiningModel.Meal;
import edu.mit.mitmobile2.dining.DiningModel.MenuItem;

class DiningComparisionSliderAdapter implements SliderView.Adapter {

	
	private DiningMealIterator mMealIterator;
	private String mCurrentDateString;
	List<HouseDiningHall> mHalls;
	
	public DiningComparisionSliderAdapter(Context context, List<HouseDiningHall> halls, long currentTime) {		
		mContext = context;
		mHalls = halls;
		
		GregorianCalendar day = new GregorianCalendar();
		day.setTimeInMillis(currentTime);
		mMealIterator = new DiningMealIterator(day, mHalls);
		
		Date mCurrentDate = new Date(currentTime);
		SimpleDateFormat mFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
		mFormat.setCalendar(new GregorianCalendar());
		mCurrentDateString = mFormat.format(mCurrentDate);
		
	}
	
	private Context mContext;
	public DiningComparisionSliderAdapter(Context context) {
		mContext = context;
	}
	
	@Override
	public boolean hasScreen(ScreenPosition screenPosition) {
		switch (screenPosition) {
			case Previous:
				return mMealIterator.hasPrevious();
			case Next:
				return mMealIterator.hasNext();
			case Current:
				return true;
		}
		return true;
	}	

	@Override
	public View getScreen(ScreenPosition screenPosition) {
		MealOrEmptyDay mealOrEmptyDay = null;
		switch (screenPosition) {
			case Previous:
				mealOrEmptyDay = mMealIterator.getPrevious();
				break;
			
			case Next:
				mealOrEmptyDay = mMealIterator.getNext();
				break;
				
			case Current:
				mealOrEmptyDay = mMealIterator.getCurrent();
				break;
	
		}
		
		if (!mealOrEmptyDay.isEmpty()) {
			return mealComparisonScreen(mealOrEmptyDay);
		} else {
			return noMealsTodayScreen(mealOrEmptyDay.getDayMessage());
		}
	}

	private View mealComparisonScreen(MealOrEmptyDay mealOrEmptyDay) {
		ScrollView mealParent = new ScrollView(mContext);
		LinearLayout mealLayout = new LinearLayout(mContext);
		mealLayout.setOrientation(LinearLayout.HORIZONTAL);
		mealParent.addView(mealLayout, new ScrollView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		for (HouseDiningHall hall : mHalls) {
			Meal meal = mealOrEmptyDay.getMeal(hall.getID());
			View hallMealView = hallMealView(hall, meal); 
			mealLayout.addView(hallMealView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
		}
		
		return mealParent;
	}
	
	private View hallMealView(HouseDiningHall hall, Meal meal) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dining_comparison_hall_column, null);
		TextView titleView = (TextView) view.findViewById(R.id.diningComparisonHallColumnTitle);
		TextView subtitleView = (TextView) view.findViewById(R.id.diningComparisonHallColumnSubtitle);
		TextView descriptionView = (TextView) view.findViewById(R.id.diningComparisonHallColumnDescription);
		View hallClosedView = view.findViewById(R.id.diningComparisonHallColumnClosed);
		
		titleView.setText(hall.getName());
		if (meal != null) {
			subtitleView.setText(meal.getTimesSummary());
			String descriptionText = "";
			for (MenuItem menuItem : meal.getMenuItems()) {
				descriptionText = menuItem.getName() + "\n";
				if (menuItem.getDescription() != null) {
					descriptionText += menuItem.getDescription();
				}
			}
			descriptionView.setText(descriptionText);
			descriptionView.setVisibility(View.VISIBLE);
		} else {
			hallClosedView.setVisibility(View.VISIBLE);
		}
		return view;
	}
	
	private View noMealsTodayScreen(String message) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dining_meal_message, null);
		TextView messageView = (TextView) view.findViewById(R.id.diningMealMessageText);
		messageView.setText(message);
		return view;
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
			default:		
		}
	}

	@Override
	public void destroy() { }
	
	
}