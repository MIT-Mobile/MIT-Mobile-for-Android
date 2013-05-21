package edu.mit.mitmobile2.dining;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
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
import edu.mit.mitmobile2.dining.DiningMealIterator.MealOrEmptyDay;
import edu.mit.mitmobile2.dining.DiningModel.HouseDiningHall;
import edu.mit.mitmobile2.dining.DiningModel.Meal;
import edu.mit.mitmobile2.dining.DiningModel.MenuItem;

public class DiningHouseScheduleSliderAdapter extends DiningHouseAbstractSliderAdapter {
	
	Context mContext;
	
	private String mHallID;
	
	public DiningHouseScheduleSliderAdapter(Context context, HouseDiningHall hall, long currentTime) {
		super(context, currentTime);
		
		mContext = context;
		mHallID = hall.getID();
		
		GregorianCalendar day = new GregorianCalendar();
		day.setTimeInMillis(currentTime);
		ArrayList<HouseDiningHall> halls = new ArrayList<HouseDiningHall>();
		halls.add(hall);
		DiningMealIterator mealIterator = new DiningMealIterator(day, halls);
		setMealIterator(mealIterator);
	}

	@Override
	protected View viewForMealOrDay(MealOrEmptyDay mealOrEmptyDay) {
		if (mealOrEmptyDay.isEmpty()) {
			return noMealsTodayScreen(mealOrEmptyDay.getDayMessage());
		} else {
			return mealScreen(mealOrEmptyDay.getMeal(mHallID));
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
}
