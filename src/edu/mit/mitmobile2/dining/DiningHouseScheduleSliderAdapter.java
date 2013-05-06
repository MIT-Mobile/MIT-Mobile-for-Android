package edu.mit.mitmobile2.dining;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import edu.mit.mitmobile2.DividerView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderView;
import edu.mit.mitmobile2.SliderView.ScreenPosition;
import edu.mit.mitmobile2.dining.DiningModel.DailyMeals;
import edu.mit.mitmobile2.dining.DiningModel.Meal;
import edu.mit.mitmobile2.dining.DiningModel.MenuItem;

public class DiningHouseScheduleSliderAdapter implements SliderView.Adapter {
	
	Context mContext;
	ScreenIndices mScreenIndices = new ScreenIndices();
	
	private List<DailyMeals> mSchedule;
	private String mCurrentDateString;
	private DateFormat mFormat;
	private Date mCurrentDate;
	
	public DiningHouseScheduleSliderAdapter(Context context, List<DailyMeals> schedule, long currentTime) {
		mContext = context;
		mSchedule = schedule;
		
		mCurrentDate = new Date(currentTime);
		mFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
		mFormat.setCalendar(new GregorianCalendar());
		mCurrentDateString = mFormat.format(mCurrentDate);
		
		for (int dayIndex = 0; dayIndex < schedule.size(); dayIndex++) {
			String scheduleDateString = mFormat.format(schedule.get(dayIndex).getDay().getTime());
			if (scheduleDateString.equals(mCurrentDateString)) {
				mScreenIndices.mDayIndex = dayIndex;
				break;
			}
		}
		
		if (mScreenIndices.mDayIndex != null) {
			// find the first meal today which is not done
			List<Meal> meals = schedule.get(mScreenIndices.mDayIndex).getMeals();
			for (int mealIndex = 0; mealIndex < meals.size() ; mealIndex++) {
				Meal meal = meals.get(mealIndex);
				if (currentTime < meal.getEnd().getTimeInMillis()) {
					mScreenIndices.mMealIndex = mealIndex;
				}
			}
		
			if (mScreenIndices.mMealIndex == null && (meals.size() > 0)) {
				// all the meals are over so just select the
				// last meal in the day
				mScreenIndices.mMealIndex = meals.size()-1;
			}
		}
	}

	
	@Override
	public boolean hasScreen(ScreenPosition screenPosition) {
		Integer dayIndex = mScreenIndices.mDayIndex;
		Integer mealIndex = mScreenIndices.mMealIndex;

		
		switch (screenPosition) {
			case Previous:
				if (dayIndex == null) {
					return false;
				}
				if (dayIndex > 0) {
					return true;
				}
				return (mealIndex != null) && 
						(mealIndex > 0);
				
			case Current:
				return true;
				
			case Next:
				if (dayIndex == null) {
					return false;
				}
				if (dayIndex + 1 < mSchedule.size()) {
					return true;
				}
				return (mealIndex != null) && 
						(mealIndex + 1 < mSchedule.get(dayIndex).getMeals().size());				
		}
		return false;
	}

	private static class ScreenIndices {
		Integer mDayIndex;
		Integer mMealIndex;
		
		ScreenIndices(Integer dayIndex, Integer mealIndex) {
			mDayIndex = dayIndex;
			mMealIndex = mealIndex;
		}

		public ScreenIndices() {
			// TODO Auto-generated constructor stub
		}
	}
	
	private ScreenIndices getScreenIndices(ScreenPosition screenPosition) {
		Integer dayIndex = mScreenIndices.mDayIndex;
		Integer mealIndex = mScreenIndices.mMealIndex;
		switch (screenPosition) {
			case Previous:
				if ((mealIndex != null) && (mealIndex > 0)) {
					return new ScreenIndices(dayIndex, mealIndex-1);
				} else {
					dayIndex--;
					if (mSchedule.get(dayIndex).getMeals().size() > 0) {
						mealIndex = mSchedule.get(dayIndex).getMeals().size() - 1;
					} else {
						mealIndex = null;
					}
					return new ScreenIndices(dayIndex, mealIndex);
				}
				
			case Next:
				if ((mealIndex != null) && (mealIndex + 1 < mSchedule.get(dayIndex).getMeals().size())) {
					return new ScreenIndices(dayIndex, mealIndex+1);
				} else {
					dayIndex++;
					if (mSchedule.get(dayIndex).getMeals().size() > 0) {
						mealIndex = 0;
					} else {
						mealIndex = null;
					}
					return new ScreenIndices(dayIndex, mealIndex);
				}
			default:
				break;
		}
		return new ScreenIndices(dayIndex, mealIndex);
	}

	@Override
	public View getScreen(ScreenPosition screenPosition) {
		ScreenIndices screenIndices = getScreenIndices(screenPosition);
		if (screenIndices.mDayIndex == null || 
		   (mSchedule.get(screenIndices.mDayIndex).getMeals().size() == 0)) {
			String dayMessage;
			if (screenIndices.mDayIndex == null)  {
				dayMessage = "No information available";
			} else {
				DailyMeals day = mSchedule.get(screenIndices.mDayIndex);
				dayMessage = day.getMessage();
			}
			
			return noMealsTodayScreen(dayMessage);
		} else {
			Meal meal = mSchedule.get(screenIndices.mDayIndex).getMeals().get(screenIndices.mMealIndex);
			return mealScreen(meal);
		}
	}



	public CharSequence getCurrentTitle() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("LLLL d", Locale.US);
		
		ScreenIndices screenIndices = mScreenIndices;
		
		if (screenIndices.mDayIndex != null) {
			DailyMeals dailyMeals = mSchedule.get(screenIndices.mDayIndex);
			Calendar day = dailyMeals.getDay();
			String title = dateFormat.format(day.getTime());
			
			if (screenIndices.mMealIndex != null) {
				String mealName = dailyMeals.getMeals().get(screenIndices.mMealIndex).getCapitalizedName();
				title = mealName + ", " + title;
				// check if the meal is today
				if (mCurrentDateString.equals(mFormat.format(day.getTime()))) {
					title = "Today's " + title;
				}
			}
			return title;
		} else {
			return dateFormat.format(mCurrentDate.getTime());
		}
	}

	private View noMealsTodayScreen(String message) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dining_meal_message, null);
		TextView messageView = (TextView) view.findViewById(R.id.diningMealMessageText);
		messageView.setText(message);
		return view;
	}
	
	private View mealScreen(Meal meal) {
		
		// Parent Layout
		ScrollView scrollWrapper = new ScrollView(mContext);
		LinearLayout layout = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.VERTICAL);
		scrollWrapper.addView(layout);
		
		// Meal header
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mealHeader = inflater.inflate(R.layout.dining_meal_header, null);	
		TextView mealTitleView = (TextView) mealHeader.findViewById(R.id.diningMealHeaderTitle);
		TextView mealTimeView = (TextView) mealHeader.findViewById(R.id.diningMealHeaderTime);
		mealTitleView.setText(meal.getCapitalizedName());
		
		if ((meal.getStart() != null) && (meal.getEnd() != null)) {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("h:mma", Locale.US);
			String time = dateFormatter.format(meal.getStart().getTime()) + " - " +
					dateFormatter.format(meal.getEnd().getTime());
			time = time.toLowerCase(Locale.US);
			mealTimeView.setText(time);
		}
		layout.addView(mealHeader);
		
		
		// meal message or list of meal items
		if (meal.getMessage() != null) {
			View view = inflater.inflate(R.layout.dining_meal_message, null);
			TextView messageView = (TextView) view.findViewById(R.id.diningMealMessageText);
			messageView.setText(meal.getMessage());			
		} else {
			for (MenuItem menuItem : meal.getMenuItems()) {
				View view = inflater.inflate(R.layout.dining_meal_item_row, null);
				TextView stationView = (TextView) view.findViewById(R.id.diningMealItemRowStation);
				TextView nameView = (TextView) view.findViewById(R.id.diningMealItemRowName);
				TextView descriptionView = (TextView) view.findViewById(R.id.diningMealItemRowDescription);
				TableLayout dietaryFlags = (TableLayout) view.findViewById(R.id.diningMealItemRowDietaryFlagsTable);
				
				stationView.setText(menuItem.getStation());
				nameView.setText(menuItem.getName());
				if (menuItem.getDescription() != null) {
					descriptionView.setText(menuItem.getDescription());
				} else {
					descriptionView.setVisibility(View.GONE);
				}
				
				List<String> flags = menuItem.getDietaryFlags();
				TableRow tableRow = null;
				int columns = 2; 
				for (int i = 0; i < flags.size(); i++) {
					if (i % columns == 0) {
						// lets make new row
						tableRow = new TableRow(mContext);
						tableRow.setGravity(Gravity.RIGHT);
						dietaryFlags.addView(tableRow);
					}
					ImageView flagImageView = new ImageView(mContext);
					flagImageView.setImageResource(getDietaryFlagResId(flags.get(i)));
					tableRow.addView(flagImageView);
				}
				
				layout.addView(view);
				layout.addView(new DividerView(mContext, null));
			}
		}
		
		return scrollWrapper;
	}
	
	private int getDietaryFlagResId(String flag) {
		String safeID = "dining_" + flag.replace(" ", "_");
		return mContext.getResources().getIdentifier(safeID, "drawable", "edu.mit.mitmobile2");
	}
	
	@Override
	public void destroyScreen(ScreenPosition screenPosition) { }

	@Override
	public void seek(ScreenPosition screenPosition) {
		mScreenIndices = getScreenIndices(screenPosition);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}
