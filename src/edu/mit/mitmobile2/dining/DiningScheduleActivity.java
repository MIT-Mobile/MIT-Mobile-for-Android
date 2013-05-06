package edu.mit.mitmobile2.dining;

import java.util.GregorianCalendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderView;
import edu.mit.mitmobile2.SliderView.Adapter;
import edu.mit.mitmobile2.SliderView.OnSeekListener;
import edu.mit.mitmobile2.SliderView.ScreenPosition;
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
		LayoutInflater inflater = getLayoutInflater();
		View diningSliderBar = inflater.inflate(R.layout.dining_slider_bar, null);
		final View leftArrow = diningSliderBar.findViewById(R.id.diningSliderLeftArrow);
		final View rightArrow = diningSliderBar.findViewById(R.id.diningSliderRightArrow);
		final TextView sliderTitle = (TextView) diningSliderBar.findViewById(R.id.diningSliderTitle);
		
		mMainLayout.addView(diningSliderBar);
		
		final SliderView sliderView = new SliderView(this);
		mMainLayout.addView(sliderView);
		
		// test time
		long currentTime = 1367351565000L;
		// real time
		//long currentTime = System.currentTimeMillis();
		
		final DiningHouseScheduleSliderAdapter diningAdapter = new DiningHouseScheduleSliderAdapter(this, mSelectedHouse.getSchedule(), currentTime);
		sliderView.setAdapter(diningAdapter);
		leftArrow.setEnabled(diningAdapter.hasScreen(ScreenPosition.Previous));
		rightArrow.setEnabled(diningAdapter.hasScreen(ScreenPosition.Next));
		sliderTitle.setText(diningAdapter.getCurrentTitle());
		
		leftArrow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sliderView.slideLeft();
			}			
		});
		rightArrow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sliderView.slideRight();
			}			
		});
		
		sliderView.setOnSeekListener(new OnSeekListener() {

			@Override
			public void onSeek(SliderView view, Adapter adapter) {
				leftArrow.setEnabled(adapter.hasScreen(ScreenPosition.Previous));
				rightArrow.setEnabled(adapter.hasScreen(ScreenPosition.Next));
				sliderTitle.setText(diningAdapter.getCurrentTitle());
			}			
		});
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
