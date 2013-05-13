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
	
	LinearLayout mMainLayout;
	private PortraitDiningScheduleScreen mDiningScheduleScreen;

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
		String houseID = getIntent().getStringExtra(HOUSE_DINING_HALL_ID_KEY);
		HouseDiningHall selectedHouse = venues.getHouseDiningHall(houseID);
		
		long selectedTime;
		if (savedInstanceState != null) {
			selectedTime = savedInstanceState.getLong(SELECTED_DATE_KEY);
		} else {
			selectedTime = System.currentTimeMillis();
		}
		GregorianCalendar selectedDate = new GregorianCalendar();
		selectedDate.setTimeInMillis(selectedTime);
		
		mDiningScheduleScreen = new PortraitDiningScheduleScreen(venues, selectedHouse, selectedDate);
		View view = mDiningScheduleScreen.initializeView(this);

		setContentView(view, false);
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
	protected void onOptionSelected(String optionId) { }

	@Override
	protected boolean isModuleHomeActivity() {
		return false;
	}

}
