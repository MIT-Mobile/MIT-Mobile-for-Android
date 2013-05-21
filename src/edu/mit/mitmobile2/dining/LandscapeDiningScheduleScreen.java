package edu.mit.mitmobile2.dining;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderView;
import edu.mit.mitmobile2.SliderView.Adapter;
import edu.mit.mitmobile2.SliderView.OnSeekListener;
import edu.mit.mitmobile2.dining.DiningModel.DiningVenues;
import edu.mit.mitmobile2.dining.DiningModel.HouseDiningHall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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
		final TextView dateView = (TextView) view.findViewById(R.id.diningComparisonDate);
		
		@SuppressWarnings("unchecked")
		List<HouseDiningHall> halls =  (List<HouseDiningHall>) mVenues.getHouses();
		
		DiningComparisionSliderAdapter diningAdapter = new DiningComparisionSliderAdapter(context, halls, mInitialDate.getTimeInMillis());
		dateView.setText(diningAdapter.getCurrentTitle());	
		
		sliderView.setAdapter(diningAdapter);		
		sliderView.setOnSeekListener(new OnSeekListener() {

			@Override
			public void onSeek(SliderView view, Adapter adapter) {
				DiningComparisionSliderAdapter diningAdapter = (DiningComparisionSliderAdapter) adapter;
				dateView.setText(diningAdapter.getCurrentTitle());
			}			
		});
		
		return view;
	}

	@Override
	public Calendar getSelectedDate() {
		return mInitialDate;
	}
	
	public boolean titleBarHidden() {
		return true;
	}

}
