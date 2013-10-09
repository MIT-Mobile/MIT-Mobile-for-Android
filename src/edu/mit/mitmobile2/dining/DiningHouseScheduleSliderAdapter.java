package edu.mit.mitmobile2.dining;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
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
import edu.mit.mitmobile2.dining.DiningModel.DiningDietaryFlag;
import edu.mit.mitmobile2.dining.DiningModel.HouseDiningHall;
import edu.mit.mitmobile2.dining.DiningModel.Meal;
import edu.mit.mitmobile2.dining.DiningModel.MenuItem;

public class DiningHouseScheduleSliderAdapter extends DiningHouseAbstractSliderAdapter {
	
	Context mContext;
	
	private String mHallID;
	
	public DiningHouseScheduleSliderAdapter(Context context, HouseDiningHall hall, long selectedTime) {
		super(context);
		
		mContext = context;
		mHallID = hall.getID();
		
		GregorianCalendar day = new GregorianCalendar();
		day.setTimeInMillis(selectedTime);
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
		
		if (meal.getScheduleSummary() != null) {
			mealTimeView.setText(meal.getScheduleSummary());
		}
		layout.addView(mealHeader);
		
		
		// meal message or list of meal items
		if (meal.getMessage() != null) {
			View view = inflater.inflate(R.layout.dining_meal_message, null);
			TextView messageView = (TextView) view.findViewById(R.id.diningMealMessageText);
			messageView.setText(meal.getMessage());			
		} else {
			
			List <DiningDietaryFlag> appliedFilters = DiningDietaryFlag.loadFilters(mContext);
			boolean noSelectedFilters = appliedFilters.isEmpty();
			if (noSelectedFilters) {
				// no filters applied means all filters are applied
				appliedFilters = new ArrayList<DiningDietaryFlag>(DiningDietaryFlag.allFlags());
			}
			
			boolean showingItems = false;
			for (MenuItem menuItem : meal.getMenuItems()) {
				
				boolean showItem = false;
				if (menuItem.getDietaryFlags().isEmpty() && noSelectedFilters) {
					// if menuItem does not have any flags and no filters have been selected (before we take all filters)
					showItem = true;
				}
				for (DiningDietaryFlag menuFlag : menuItem.getDietaryFlags()) {
					if (appliedFilters.contains(menuFlag)) {
						showItem = true;
						break;
					}
				}
				
				if (showItem) {
					showingItems = true;
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
					
					List<DiningDietaryFlag> flags = menuItem.getDietaryFlags();
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
						DiningDietaryFlag flag = flags.get(i);
						flagImageView.setImageResource(flag.getIconId());
						tableRow.addView(flagImageView);
					}
					
					layout.addView(view);
					
					View dividerView = new DividerView(mContext, null);
					layout.addView(dividerView);
				}
			}
			
			if (!showingItems) {
				String message;
				if (meal.getMenuItems().size() == 0) {
					message = "No items";
				} else {
					message = "No matching items";
				}
				layout.addView(getEmptyMenuView(message));
				
				layout.addView(new DividerView(mContext, null));
			}
			
			View rotateLegend = inflater.inflate(R.layout.dining_rotate_legend, null);
			layout.addView(rotateLegend);
			
			rotateLegend.setBackgroundColor(mContext.getResources().getColor(R.color.rowBackground));
		}
		
		return scrollWrapper;
	}
	
	private View getEmptyMenuView(String message) {
		TextView emptyMessage = new TextView(mContext);
		emptyMessage.setGravity(Gravity.CENTER_HORIZONTAL);
		emptyMessage.setTextAppearance(mContext, R.style.ListItemPrimary);
		emptyMessage.setText(message);
		int topPadding = mContext.getResources().getDimensionPixelSize(R.dimen.standardPadding);
		emptyMessage.setPadding(topPadding, topPadding, topPadding, topPadding);
		emptyMessage.setBackgroundColor(Color.WHITE);
		return emptyMessage;
	}
	
}
