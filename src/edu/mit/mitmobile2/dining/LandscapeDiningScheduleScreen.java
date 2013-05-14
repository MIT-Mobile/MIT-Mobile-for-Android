package edu.mit.mitmobile2.dining;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.DiningModel.DiningVenues;
import edu.mit.mitmobile2.dining.DiningModel.HouseDiningHall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class LandscapeDiningScheduleScreen extends DiningScheduleScreen {

	DiningVenues mVenues;
	protected GregorianCalendar mInitialDate;
	
	public LandscapeDiningScheduleScreen(DiningVenues venues, GregorianCalendar initialDate) {
		mVenues = venues;
		mInitialDate = initialDate;
	}
	
	@Override
	public View initializeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dining_comparison, null);
		DiningSliderView sliderView = (DiningSliderView) view.findViewById(R.id.diningComparisonSliderView);
		@SuppressWarnings("unchecked")
		List<HouseDiningHall> halls =  (List<HouseDiningHall>) mVenues.getHouses();
		sliderView.setAdapter(new DiningComparisionSliderAdapter(context, halls, mInitialDate.getTimeInMillis()));
		return view;
	}

	@Override
	public Calendar getSelectedDate() {
		return mInitialDate;
	}

}
