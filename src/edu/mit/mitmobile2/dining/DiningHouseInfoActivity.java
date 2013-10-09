package edu.mit.mitmobile2.dining;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.dining.DiningModel.DailyMeals;
import edu.mit.mitmobile2.dining.DiningModel.DailyMealsSchedule;
import edu.mit.mitmobile2.dining.DiningModel.DiningVenues;
import edu.mit.mitmobile2.dining.DiningModel.HouseDiningHall;

public class DiningHouseInfoActivity extends NewModuleActivity {
	
	private static String HOUSE_DINING_HALL_ID_KEY = "hall_id";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dining_hall_house_info);
		
		
		DiningVenues venues = DiningModel.getDiningVenues();
		if (venues == null) {
			// fail gracefully
			finish();
			return;
		}
		String houseID = getIntent().getStringExtra(HOUSE_DINING_HALL_ID_KEY);
		final HouseDiningHall house = venues.getHouseDiningHall(houseID);
		
		long selectedTime = DiningModel.currentTimeMillis();
		DiningHallHeaderView headerView = (DiningHallHeaderView) findViewById(R.id.diningHallHouseInfoHeader);
		headerView.setHall(house, selectedTime);
		headerView.setBackgroundColor(Color.TRANSPARENT);
		
		
		ListView infoLV = (ListView) findViewById(R.id.diningHallHouseInfoTopLV);
		infoLV.setAdapter(new SimpleArrayAdapter<InfoItem>(this, getHouseInfo(house), R.layout.dining_hall_info_item_row) {
			@Override
			public void updateView(InfoItem item, View view) {
				TextView label = (TextView) view.findViewById(R.id.diningHallInfoLabel);
				TextView value = (TextView) view.findViewById(R.id.diningHallInfoValue);
				ImageView img = (ImageView) view.findViewById(R.id.diningInfoItemRowActionIcon);
				
				label.setText(item.getInfoLabel());
				value.setText(item.getInfoValue());
				
				if (item.getInfoActionId() > 0) {
					img.setVisibility(View.VISIBLE);
					img.setImageResource(item.getInfoActionId());
				} else {
					img.setVisibility(View.GONE);
				}
			}
			
			@Override
			public boolean isEnabled(int position) {
				return position == 0;	// only location position is enabled
			}
			
			@Override
			public boolean areAllItemsEnabled() {
				return true;	// need to be true so dividers show up
			}
		});
		
		infoLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (position == 0) {
					String locationName = house.getLocation().mDescription;
					String url = "mitmobile://map/search?" + locationName;
					CommonActions.doAction(DiningHouseInfoActivity.this, url);
				}
				
			}
		});
		
		List <ScheduleItem> schedules = getHouseSchedule(house);
		Log.d("INFOSCHEDULE", "Schedules size :: "+schedules.size());
		
		ListView scheduleLV = (ListView) findViewById(R.id.diningHallHouseInfoScheduleLV);
		scheduleLV.setAdapter(new SimpleArrayAdapter<ScheduleItem>(this, schedules, R.layout.dining_hall_house_schedule_item_row) {
			@Override
			public void updateView(ScheduleItem item, View view) {
				TextView label = (TextView) view.findViewById(R.id.diningHallInfoLabel);
				TextView mealTV = (TextView) view.findViewById(R.id.diningScheduleMealName);
				TextView timeTV = (TextView) view.findViewById(R.id.diningScheduleMealTimes);
				
				label.setText(item.getSpanString());
				String mealName = item.getMealNameString();
				if (!mealName.isEmpty()) {
					mealTV.setText(item.getMealNameString());
				} else {
					mealTV.setText("Closed");
				}
				timeTV.setText(item.getMealTimeString());
			}
		});
	}
	
	private List<InfoItem> getHouseInfo(HouseDiningHall hall) {
		InfoItem locationInfo = new InfoItem(getString(R.string.dining_location_info_label), hall.getLocation().mDescription, R.drawable.action_map);
		InfoItem paymentInfo = new InfoItem(getString(R.string.dining_payment_info_label), hall.getPaymentOptionString(), 0);
		
		ArrayList<InfoItem> list = new ArrayList<InfoItem>();
		list.add(locationInfo);
		list.add(paymentInfo);
		
		return list;
	}
	
	private List<ScheduleItem> getHouseSchedule(HouseDiningHall hall) {
		
		long selectedTime = DiningModel.currentTimeMillis();
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(selectedTime);
		
		DailyMealsSchedule mealSchedule = hall.getSchedule();
		List<DailyMeals> days = mealSchedule.getDailyMealsForCurrentWeek(cal);
		
		DailyMeals previousDay = null;
		ArrayList <ScheduleItem> scheduleLists = new ArrayList<ScheduleItem>();
		for (DailyMeals mealDay : days) {
			if (previousDay == null) {
				// first time through loop add day, set previousDay.
				ScheduleItem item = new ScheduleItem();
				item.setStartDay(mealDay.getDay());
				item.setEndDay(mealDay.getDay());
				item.setMealSummary(mealDay.getMealTimes());
				scheduleLists.add(item);
			} else {
				long oneDayInMillis = 60 * 60 * 24 * 1000;
				if (DiningModel.compareDates(previousDay.getDay(), mealDay.getDay()) <= oneDayInMillis) {
					// day is adjacent, need to compare schedules
					ScheduleItem lastSchedule = scheduleLists.get(scheduleLists.size() - 1);
					if (lastSchedule.getMealSummary().equals(mealDay.getMealTimes())) {
						// comparison is equal, need to bump last schedule end date
						lastSchedule.setEndDay(mealDay.getDay());
					} else {
						// comparison is not equal add new schedule item
						ScheduleItem item = new ScheduleItem();
						item.setStartDay(mealDay.getDay());
						item.setEndDay(mealDay.getDay());
						item.setMealSummary(mealDay.getMealTimes());
						scheduleLists.add(item);
					}
				} else {
					// days are not adjacent, add new ScheduleItem
					ScheduleItem item = new ScheduleItem();
					item.setStartDay(mealDay.getDay());
					item.setEndDay(mealDay.getDay());
					item.setMealSummary(mealDay.getMealTimes());
					scheduleLists.add(item);
				}
			}
			previousDay = mealDay; // update reference for look behind
		}
		
		
		return scheduleLists;
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
		list.add(DiningModule.FILTER_ITEM_ID);
		list.add(DiningModule.LISTVIEW_ITEM_ID);
		list.add(DiningModule.MAPVIEW_ITEM_ID);
		return list;
	}
	
	@Override
	protected void onOptionSelected(String optionId) { }

	@Override
	protected boolean isModuleHomeActivity() {
		return false;
	}

	
	/****************************************************/
	/***	 Helper Object Definitions 		  ***********/
	/****************************************************/
	
	private static class ScheduleItem {
		
		private Calendar mStartDay;
		private Calendar mEndDay;
		private HashMap<String, String> mMealSummary;
		static SimpleDateFormat sFormat = new SimpleDateFormat("EEE", Locale.US);
		
//		public Calendar getStartDay() {
//			return mStartDay;
//		}
//		
//		public Calendar getEndDay() {
//			return mEndDay;
//		}
		
		public HashMap<String, String> getMealSummary() {
			return mMealSummary;
		}
		
		public void setStartDay(Calendar start) {
			 mStartDay = start;
		}
		
		public void setEndDay(Calendar end) {
			 mEndDay = end;
		}
		
		public void setMealSummary(HashMap<String, String> map) {
			 mMealSummary = map;
		}
		
		public String getSpanString() {
			if (DiningModel.compareDates(mStartDay, mEndDay) == 0) {
				String daySpan = sFormat.format(mStartDay.getTime());
				return daySpan.toLowerCase(Locale.ENGLISH);
			}
			String startStr = sFormat.format(mStartDay.getTime());
			String endStr = sFormat.format(mEndDay.getTime());			
			return startStr.toLowerCase(Locale.ENGLISH) + " - " + endStr.toLowerCase();
		}
		
		public String getMealNameString() {
			Set<String> mealStrings = mMealSummary.keySet();
			String nameString = "";
			int i = 0;
			int lastIndex = mealStrings.size() - 1;
			for (String s : mealStrings) {
				nameString += s;
				if (i != lastIndex )
					nameString += "\n";
				i++;
			}
			
			return nameString;
		}
		
		public String getMealTimeString() {
			
			Collection<String> values = mMealSummary.values();
			String timeString = "";
			int i = 0;
			int lastIndex = values.size() - 1;
			for (String s : values) {
				timeString += s;
				if (i != lastIndex)
					timeString += "\n";
				i++;
			}
			return timeString;
		}
	}
	
}
