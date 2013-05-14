package edu.mit.mitmobile2.dining;

import java.util.Calendar;

import edu.mit.mitmobile2.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class LandscapeDiningScheduleScreen extends DiningScheduleScreen {

	@Override
	public View initializeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dining_comparison, null);
		DiningSliderView sliderView = (DiningSliderView) view.findViewById(R.id.diningComparisonSliderView);
		sliderView.setAdapter(new DiningComparisionSliderAdapter(context));
		return view;
	}

	@Override
	public Calendar getSelectedDate() {
		// TODO Auto-generated method stub
		return null;
	}

}
