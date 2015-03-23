package edu.mit.mitmobile2.dining;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.dining.DiningModel.DiningDietaryFlag;
import edu.mit.mitmobile2.dining.DiningModel.DiningHall;
import edu.mit.mitmobile2.dining.DiningModel.DiningVenues;
import edu.mit.mitmobile2.dining.DiningModel.HouseDiningHall;

public class DiningScheduleActivity extends NewModuleActivity {

	private static int FILTER_ACTIVITY_REQUEST_CODE = 1;
	
	private static String SELECTED_DATE_KEY = "selected_date";
	private static String HOUSE_DINING_HALL_ID_KEY = "hall_id";
	
	private List<DiningDietaryFlag> mFiltersApplied;
	
	LinearLayout mMainLayout;
	private DiningScheduleScreen mDiningScheduleScreen;

	private HouseDiningHall mSelectedHouse;

	public static void launch(Context context, DiningHall diningHall) {
		if (diningHall instanceof HouseDiningHall) {
			Intent intent = new Intent(context, DiningScheduleActivity.class);
			intent.putExtra(HOUSE_DINING_HALL_ID_KEY, diningHall.getID());
			context.startActivity(intent);	
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		DiningVenues venues = DiningModel.getDiningVenues();
		if (venues == null) {
			// fail gracefully
			finish();
			return;
		}
		mFiltersApplied = DiningDietaryFlag.loadFilters(this);
		String houseID = getIntent().getStringExtra(HOUSE_DINING_HALL_ID_KEY);
		mSelectedHouse = venues.getHouseDiningHall(houseID);
		
		long selectedTime;
		if (savedInstanceState != null) {
			selectedTime = savedInstanceState.getLong(SELECTED_DATE_KEY);
		} else {
			selectedTime = DiningModel.currentTimeMillis();
		}
		GregorianCalendar selectedDate = new GregorianCalendar();
		selectedDate.setTimeInMillis(selectedTime);
		
		int orientation = getResources().getConfiguration().orientation;
		switch (orientation) {
			case Configuration.ORIENTATION_LANDSCAPE:
				mDiningScheduleScreen = new LandscapeDiningScheduleScreen(venues, selectedDate);
				break;
				
			case Configuration.ORIENTATION_PORTRAIT:
			default:
				mDiningScheduleScreen = new PortraitDiningScheduleScreen(venues, mSelectedHouse, selectedDate);
				break;
		}
		
		View view = mDiningScheduleScreen.initializeView(this);

		setContentView(view, false);
		
		if (mDiningScheduleScreen.titleBarHidden()) {
			getTitleBar().setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FILTER_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				List<DiningDietaryFlag> list = data.getParcelableArrayListExtra(DiningFilterActivity.SELECTED_FILTERS);
				mFiltersApplied = list;
				DiningDietaryFlag.saveFilters(this, mFiltersApplied);
				mDiningScheduleScreen.refreshScreen();
			}
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(SELECTED_DATE_KEY, mDiningScheduleScreen.getSelectedDate(mSelectedHouse).getTimeInMillis());
	}
	
	@Override
	protected NewModule getNewModule() {
		return new DiningModule();
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}
	
	@Override
	protected List<String> getMenuItemBlackList() {
		ArrayList<String> list = new ArrayList<String>();
		int orientation = getResources().getConfiguration().orientation;
		switch (orientation) {
			case Configuration.ORIENTATION_LANDSCAPE:
				list.add(DiningModule.FILTER_ITEM_ID);
			case Configuration.ORIENTATION_PORTRAIT:
			default:
		}
		list.add(DiningModule.LISTVIEW_ITEM_ID);
		list.add(DiningModule.MAPVIEW_ITEM_ID);
		return list;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		if (optionId.equals(DiningModule.FILTER_ITEM_ID)) {
			Intent intent = new Intent(this, DiningFilterActivity.class);
			intent.putParcelableArrayListExtra(DiningFilterActivity.SELECTED_FILTERS, new ArrayList<DiningDietaryFlag>(mFiltersApplied));
			startActivityForResult(intent, FILTER_ACTIVITY_REQUEST_CODE);
		}
	}

	@Override
	protected boolean isModuleHomeActivity() {
		return false;
	}

}
