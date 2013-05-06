package edu.mit.mitmobile2.dining;

import java.util.GregorianCalendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderView;
import edu.mit.mitmobile2.dining.DiningModel.DiningHall;
import edu.mit.mitmobile2.dining.DiningModel.DiningVenues;
import edu.mit.mitmobile2.dining.DiningModel.HouseDiningHall;

public class DiningScheduleActivity extends NewModuleActivity {

	private static String SELECTED_DATE_KEY = "selected_date";
	private static String HOUSE_DINING_HALL_ID_KEY = "hall_id";
	
	protected DiningVenues mVenues;
	protected HouseDiningHall mSelectedHouse;
	protected GregorianCalendar mSelectedDate;
	
	LinearLayout mMainLayout;

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
	
		mVenues = DiningModel.getDiningVenues();
		if (mVenues == null) {
			// fail gracefully
			finish();
			return;
		}
		String houseID = getIntent().getStringExtra(HOUSE_DINING_HALL_ID_KEY);
		mSelectedHouse = mVenues.getHouseDiningHall(houseID);
		
		long selectedTime;
		if (savedInstanceState != null) {
			selectedTime = savedInstanceState.getLong(SELECTED_DATE_KEY);
		} else {
			selectedTime = System.currentTimeMillis();
		}
		mSelectedDate = new GregorianCalendar();
		mSelectedDate.setTimeInMillis(selectedTime);
		
		mMainLayout = new LinearLayout(this);
		mMainLayout.setOrientation(LinearLayout.VERTICAL);
		setContentView(mMainLayout, false);
		
		addHeader();
		addSliderView();
	}
	
	protected void addHeader() {
		DiningHallHeaderView headerView = new DiningHallHeaderView(this, mSelectedHouse, System.currentTimeMillis());
		headerView.setActionImageResourceId(R.drawable.menu_info);
		headerView.setActionClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}			
		});
		mMainLayout.addView(headerView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}
	
	protected void addSliderView() {
		SliderView sliderView = new SliderView(this);
		mMainLayout.addView(sliderView);
		
		// test time
		long currentTime = 1367351565000L;
		// real time
		//long currentTime = System.currentTimeMillis();
		sliderView.setAdapter(new DiningHouseScheduleSliderAdapter(this, mSelectedHouse.getSchedule(), currentTime));
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(SELECTED_DATE_KEY, mSelectedDate.getTimeInMillis());
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
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isModuleHomeActivity() {
		return false;
	}

}
