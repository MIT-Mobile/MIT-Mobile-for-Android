package edu.mit.mitmobile2.events;


import java.util.List;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TitleBar;
import edu.mit.mitmobile2.objs.EventDetailsItem;

public class MITHolidaysCalendarActivity extends ModuleActivity {	
	

	ListView mListView;
	FullScreenLoader mLoaderView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final Context context = this;
		
		setContentView(R.layout.boring_list_layout);
		
		TitleBar titleBar = (TitleBar) findViewById(R.id.boringListTitleBar);
		titleBar.setTitle("Holidays");
		
		mListView = (ListView) findViewById(R.id.boringListLV);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view, int position, long arg3) {
				EventDetailsItem eventDetails = (EventDetailsItem) listView.getItemAtPosition(position);
				MITEventsSliderActivity.launchHolidaysCalendar(context, eventDetails.id);
			}
		});
		
		mLoaderView = (FullScreenLoader) findViewById(R.id.boringListLoader);		
		
		EventsModel.fetchHolidays(this, new Handler() {
			@Override
			public void handleMessage(Message message) {
				if(message.arg1 == MobileWebApi.SUCCESS) {
					List<EventDetailsItem> events = EventsModel.getHolidays();
					ListAdapter listAdapter = new EventsArrayAdapter(context, R.layout.events_row, 0, events, EventDetailsItem.SHORT_DAYS_ONLY);
					mLoaderView.setVisibility(View.GONE);
					mListView.setAdapter(listAdapter);
					mListView.setVisibility(View.VISIBLE);
				} else {
					mLoaderView.showError();
				}
			}			
		});
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
