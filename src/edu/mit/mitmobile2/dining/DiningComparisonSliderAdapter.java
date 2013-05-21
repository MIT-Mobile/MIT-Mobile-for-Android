package edu.mit.mitmobile2.dining;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
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
		mealParent.setFillViewport(true);
		LinearLayout mealLayout = new LinearLayout(mContext);
		mealLayout.setOrientation(LinearLayout.HORIZONTAL);
		mealParent.addView(mealLayout, new ScrollView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		for (int i = 0; i < mHalls.size(); i++) {
			HouseDiningHall hall = mHalls.get(i);
			Meal meal = mealOrEmptyDay.getMeal(hall.getID());
			DiningColumnLinearLayout hallMealView = hallMealView(hall, meal); 
			if (i == 0) {
				hallMealView.setLeftBorderEnabled(false);
			}
			if (i == mHalls.size()-1) {
				hallMealView.setRightBorderEnabled(false);
			}
			mealLayout.addView(hallMealView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
		}
		
		return mealParent;
	}
	
	private DiningColumnLinearLayout hallMealView(HouseDiningHall hall, Meal meal) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		DiningColumnLinearLayout view = (DiningColumnLinearLayout) inflater.inflate(R.layout.dining_comparison_hall_column, null);
		TextView titleView = (TextView) view.findViewById(R.id.diningComparisonHallColumnTitle);
		TextView subtitleView = (TextView) view.findViewById(R.id.diningComparisonHallColumnSubtitle);
		View hallClosedView = view.findViewById(R.id.diningComparisonHallColumnClosed);
		
		titleView.setText(hall.getShortName());
		if (meal != null) {
			subtitleView.setText(meal.getTimesSummary());
			for (MenuItem menuItem : meal.getMenuItems()) {
				view.addView(getMenuItemView(menuItem), new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			}
		} else {
			hallClosedView.setVisibility(View.VISIBLE);
		}
		return view;
	}
	
	private View getMenuItemView(MenuItem menuItem) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dining_comparison_meal_item, null);
		TextView nameView = (TextView) view.findViewById(R.id.diningComparisonMealItemRowName);
		TextView descriptionView = (TextView) view.findViewById(R.id.diningComparisonMealItemRowDescription);
		LinearLayout dietaryFlags = (LinearLayout) view.findViewById(R.id.diningComparisonMealItemRowDietaryFlagsTable);
		
		nameView.setText(menuItem.getName());
		if (menuItem.getDescription() != null) {
			descriptionView.setText(menuItem.getDescription());
		} else {
			descriptionView.setVisibility(View.GONE);
		}
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		for (String flag : menuItem.getDietaryFlags()) {
			ImageView flagImageView = new ImageView(mContext);
			flagImageView.setImageResource(getDietaryFlagResId(flag));
			dietaryFlags.addView(flagImageView, layoutParams);
		}
		
		return view;
	}
	
	private int getDietaryFlagResId(String flag) {
		String safeID = "dining_" + flag.replace(" ", "_");
		return mContext.getResources().getIdentifier(safeID, "drawable", "edu.mit.mitmobile2");
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