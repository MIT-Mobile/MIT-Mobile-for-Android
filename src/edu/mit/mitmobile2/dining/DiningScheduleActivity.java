package edu.mit.mitmobile2.dining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.dining.DiningModel.DiningDietaryFlag;
import edu.mit.mitmobile2.dining.DiningModel.DiningHall;
import edu.mit.mitmobile2.dining.DiningModel.DiningVenues;
import edu.mit.mitmobile2.dining.DiningModel.HouseDiningHall;

public class DiningScheduleActivity extends NewModuleActivity {

	private static String FILTER_PREFERENCE_KEY = "filter.preference";
	private static int FILTER_ACTIVITY_REQUEST_CODE = 1;
	
	private static String SELECTED_DATE_KEY = "selected_date";
	private static String HOUSE_DINING_HALL_ID_KEY = "hall_id";
	
	private List<DiningDietaryFlag> mFiltersApplied;
	
	LinearLayout mMainLayout;
	private DiningScheduleScreen mDiningScheduleScreen;

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
		mFiltersApplied = loadFilters();
		String houseID = getIntent().getStringExtra(HOUSE_DINING_HALL_ID_KEY);
		HouseDiningHall selectedHouse = venues.getHouseDiningHall(houseID);
		
		long selectedTime;
		if (savedInstanceState != null) {
			selectedTime = savedInstanceState.getLong(SELECTED_DATE_KEY);
		} else {
			// test time
			selectedTime = 1367351565000L;
			//selectedTime = System.currentTimeMillis();
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
				mDiningScheduleScreen = new PortraitDiningScheduleScreen(venues, selectedHouse, selectedDate);
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
				saveFilters();
			}
		}
	}
	
	private void saveFilters() {
		Set<String> filterNames = new HashSet<String>();
		for (DiningDietaryFlag flag : mFiltersApplied) {
			filterNames.add(flag.getName());
		}
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putStringSet(FILTER_PREFERENCE_KEY, filterNames);
		editor.apply();
	}
	
	private List<DiningDietaryFlag> loadFilters() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Set<String> nameSet = prefs.getStringSet(FILTER_PREFERENCE_KEY, new HashSet<String>());
		ArrayList<DiningDietaryFlag> flagList = new ArrayList<DiningDietaryFlag>();
		for (String name : nameSet) {
			flagList.add(DiningDietaryFlag.flagsByName().get(name));
		}
		return flagList;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(SELECTED_DATE_KEY, mDiningScheduleScreen.getSelectedDate().getTimeInMillis());
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
		int orientation = getResources().getConfiguration().orientation;
		switch (orientation) {
			case Configuration.ORIENTATION_LANDSCAPE:
				ArrayList<String> list = new ArrayList<String>();
				list.add(DiningModule.FILTER_ITEM_ID);
				return list;
		case Configuration.ORIENTATION_PORTRAIT:
			default:
				return Collections.emptyList();
		}
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
