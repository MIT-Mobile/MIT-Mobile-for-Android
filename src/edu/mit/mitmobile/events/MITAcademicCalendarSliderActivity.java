package edu.mit.mitmobile.events;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;

import edu.mit.mitmobile.Module;
import edu.mit.mitmobile.R;
import edu.mit.mitmobile.SliderActivity;

public class MITAcademicCalendarSliderActivity extends SliderActivity {	
	
	private Date mCurrentDate = new Date(System.currentTimeMillis());	
	private int MONTHS_PAST_FUTURE = 12;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String monthString = new SimpleDateFormat("M").format(mCurrentDate);
		int month = Integer.parseInt(monthString)-1;
		
		String yearString = new SimpleDateFormat("yyyy").format(mCurrentDate);
		int year = Integer.parseInt(yearString);
		
		int totalMonths = year * 12 + month;
		
		useSubtitles("Academic Calendar");
		
		// create views for this month , and a fixed number of months in the past and future
		for(int i = -MONTHS_PAST_FUTURE; i <= MONTHS_PAST_FUTURE; i++) {
			int sliderMonth = (totalMonths + i) % 12 + 1;
			int sliderYear = (totalMonths + i) / 12;
			
			String monthTitle = DateUtils.getMonthString(sliderMonth-1, DateUtils.LENGTH_LONG) + " " + Integer.toString(sliderYear);						
			
			EventsDaySliderInterface sliderInterface = EventsDaySliderInterface.academicCalendarFactory(this, sliderYear, sliderMonth);
			
			addScreen(sliderInterface, monthTitle, monthTitle);
		}
		
		// set the position to this month
		setPosition(MONTHS_PAST_FUTURE);
		
	}
	
	@Override
	protected Module getModule() {
		return new EventsModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		menu.add(0, MENU_SEARCH, Menu.NONE, MENU_SEARCH_TITLE)
			.setIcon(R.drawable.menu_search);
	}
}
