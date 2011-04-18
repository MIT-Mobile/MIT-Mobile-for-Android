package edu.mit.mitmobile2.events;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import edu.mit.mitmobile2.DividerView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SearchBar;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.events.EventsModel.EventType;

public class EventsTopActivity extends ModuleActivity {
	
	private TwoLineActionRow mCategories;
	private TwoLineActionRow mAcademicCalendar;
	private TwoLineActionRow mHolidaysCalendar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.events_main);
		
		EventsModel.fetchEventTypes(this, new Handler() {
			boolean mTypesLoaded = false;
			
			@Override
			public void handleMessage(Message msg) {
				FullScreenLoader loader = (FullScreenLoader) findViewById(R.id.eventsMainLoader);
				LinearLayout topLevelList = (LinearLayout) findViewById(R.id.eventsTopLevelTypes);
				View loaderDivider = findViewById(R.id.eventsMainLoaderDivider);
				
				if(msg.arg1 == MobileWebApi.SUCCESS && !mTypesLoaded) {
					loader.setVisibility(View.GONE);
					loaderDivider.setVisibility(View.GONE);
					
					for(final EventType eventType : EventsModel.getEventTypes()) {
						TwoLineActionRow actionRow = new TwoLineActionRow(EventsTopActivity.this);
						actionRow.setTitle(eventType.getLongName());
						actionRow.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								if(eventType.hasCategories()) {
									EventsSimpleCategoryActivity.launch(EventsTopActivity.this, eventType);
								} else {
									MITEventsDaysSliderActivity.launch(EventsTopActivity.this, eventType);
								}
							}
						});
						
						DividerView dividerView = new DividerView(EventsTopActivity.this, null);
						topLevelList.addView(actionRow);
						topLevelList.addView(dividerView);
					}	
					
					mTypesLoaded = true;
				} else if(msg.arg1 == MobileWebApi.ERROR) {
					loader.showError();
				}
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
