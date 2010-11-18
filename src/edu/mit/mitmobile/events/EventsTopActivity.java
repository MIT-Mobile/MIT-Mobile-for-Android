package edu.mit.mitmobile.events;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import edu.mit.mitmobile.Module;
import edu.mit.mitmobile.ModuleActivity;
import edu.mit.mitmobile.R;
import edu.mit.mitmobile.SearchBar;
import edu.mit.mitmobile.TwoLineActionRow;

public class EventsTopActivity extends ModuleActivity {
	
	private TwoLineActionRow mTodaysEvents;
	private TwoLineActionRow mTodaysExhibits;
	private TwoLineActionRow mCategories;
	private TwoLineActionRow mAcademicCalendar;
	private TwoLineActionRow mHolidaysCalendar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.events_main);
		
		mTodaysEvents = (TwoLineActionRow) findViewById(R.id.eventsTodaysEvents);
		mTodaysEvents.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MITEventsDaysSliderActivity.launch(EventsTopActivity.this, EventsModel.EventType.Events);
			}
		});
		
		mTodaysExhibits = (TwoLineActionRow) findViewById(R.id.eventsTodaysExhibits);
		mTodaysExhibits.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MITEventsDaysSliderActivity.launch(EventsTopActivity.this, EventsModel.EventType.Exhibits);
			}
		});
		
		mCategories = (TwoLineActionRow) findViewById(R.id.eventsBrowseCatogories);
		mCategories.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EventsTopActivity.this, EventsCategoryActivity.class);
				startActivity(intent);
			}
		});
		
		mAcademicCalendar = (TwoLineActionRow) findViewById(R.id.eventsAcademicCalendar);
		mAcademicCalendar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EventsTopActivity.this, MITAcademicCalendarSliderActivity.class);
				startActivity(intent);
			}
		});
		
		mHolidaysCalendar = (TwoLineActionRow) findViewById(R.id.eventsHolidaysCalendar);
		mHolidaysCalendar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EventsTopActivity.this, MITHolidaysCalendarActivity.class);
				startActivity(intent);
			}
		});
		
		SearchBar searchBar = (SearchBar) findViewById(R.id.eventsSearchBar);
		searchBar.setSearchHint(getString(R.string.events_search_hint));
		searchBar.setSystemSearchInvoker(this);
	}

	@Override
	protected Module getModule() {
		return new EventsModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return true;
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) { 
		menu.add(0, MENU_SEARCH, Menu.NONE, MENU_SEARCH_TITLE)
			.setIcon(R.drawable.menu_search);
	}

}
