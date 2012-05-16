package edu.mit.mitmobile2.events;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import edu.mit.mitmobile2.DividerView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.MITPopupSecondaryTitleBar;
import edu.mit.mitmobile2.MITSliderTitleBar;
import edu.mit.mitmobile2.MITSliderTitleBar.OnPreviousNextListener;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.OnMITMenuItemListener;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SearchBar;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.SliderView;
import edu.mit.mitmobile2.SliderView.ScreenPosition;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.events.EventDayListSliderAdapter.OnDayChangeListener;
import edu.mit.mitmobile2.events.EventMonthAcademicaCalendarListSliderAdapter.OnMonthChangeListener;
import edu.mit.mitmobile2.events.EventsModel.EventType;
import edu.mit.mitmobile2.objs.EventCategoryItem;
import edu.mit.mitmobile2.objs.EventDetailsItem;

public class EventsTopActivity extends NewModuleActivity implements OnMITMenuItemListener {
	
	private Context mContext;
	private FullScreenLoader mLoader;
	private MITPopupSecondaryTitleBar mSecondaryTitleBar;
	
	private HashMap<String, EventType> mEventTypes = new HashMap<String, EventType>();
    private long mCurrentTimestamp;
	private MITSliderTitleBar mSecondarySliderBar;
	 
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		mContext = this;
		
		mSecondaryTitleBar = new MITPopupSecondaryTitleBar(this);
		mSecondaryTitleBar.setOnPopupMenuItemListener(this);
		
		mSecondarySliderBar = new MITSliderTitleBar(this);
		initContentView();
		
		mLoader = new FullScreenLoader(this, null);
		mLoader.showLoading();
		setContentView(mLoader, true);
		
		mCurrentTimestamp = System.currentTimeMillis() / 1000;
		
		EventsModel.fetchEventTypes(this, new Handler() {
			boolean mTypesLoaded = false;
			
			@Override
			public void handleMessage(Message msg) {
				
				if(msg.arg1 == MobileWebApi.SUCCESS && !mTypesLoaded) {
					setContentView(new View(mContext), false);
					getTitleBar().addSecondaryBar(mSecondaryTitleBar);
					
					for(final EventType eventType : EventsModel.getEventTypes()) {
						mSecondaryTitleBar.addPopupMenuItem(new MITMenuItem(eventType.getTypeId(), eventType.getLongName()));
					}	
					
					if (EventsModel.getEventTypes().size() > 0) {
						EventsTopActivity.this.onOptionItemSelected(EventsModel.getEventTypes().get(0).getTypeId());
					}
					
					mSecondaryTitleBar.addPopupMenuItem(new MITMenuItem("Browse_Categories", "Browse Events by Category"));
					mSecondaryTitleBar.addPopupMenuItem(new MITMenuItem("Academic_Calendar", "Academic Calendar"));
					mSecondaryTitleBar.addPopupMenuItem(new MITMenuItem("Holidays_Calendar", "Holidays"));
					
					mTypesLoaded = true;
				} else if(msg.arg1 == MobileWebApi.ERROR) {
					mLoader.showError();
				}
			}
			
		});
	}

	/*
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
	*/
	
	/*
	DividerView dividerView = new DividerView(EventsTopActivity.this, null);
	topLevelList.addView(actionRow);
	topLevelList.addView(dividerView);
	private TwoLineActionRow mCategories;
	private TwoLineActionRow mAcademicCalendar;
	private TwoLineActionRow mHolidaysCalendar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.events_main);
		final FullScreenLoader loader = (FullScreenLoader) findViewById(R.id.eventsMainLoader);
		loader.showLoading();
		
		EventsModel.fetchEventTypes(this, new Handler() {
			boolean mTypesLoaded = false;
			
			@Override
			public void handleMessage(Message msg) {
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
   
    */
	@Override
	protected NewModule getNewModule() {
		return new EventsModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return true;
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) { }

	@Override
	public void onOptionItemSelected(String optionId) {
		getTitleBar().removeSliderBar();
		
		EventType eventType = EventsModel.getEventType(optionId);
		if (eventType != null) {
			loadEvents(eventType);
		} else if (optionId.equals("Academic_Calendar")) {
			loadAcademicCalendar();
		} else if (optionId.equals("Browse_Categories")) {
			loadBrowseCategoriesView();
		} else if (optionId.equals("Holidays_Calendar")) {
			loadHolidays();
		}
	}

	private void loadEvents(EventType eventType) {
		getTitleBar().addSliderBar(mSecondarySliderBar);
		final SliderView sliderView = new SliderView(mContext);
		sliderView.setAdapter(new EventDayListSliderAdapter(this, eventType, mCurrentTimestamp, 
			new OnDayChangeListener() {
				@Override
				public void onDayChangeListener(String previous,String current, String next) {
					mSecondarySliderBar.showPreviousNext();
					mSecondarySliderBar.setAllTitles(previous, current, next);
				}
			}	
		));
		mSecondarySliderBar.setPreviousNextListener(new OnPreviousNextListener() {
			@Override
			public void onPreviousClicked() {
				sliderView.slideLeft();					
			}

			@Override
			public void onNextClicked() {
				sliderView.slideRight();
			}
		});
		setContentView(sliderView, false);		
	}

	private void loadAcademicCalendar() {
		getTitleBar().addSliderBar(mSecondarySliderBar);
		final SliderView sliderView = new SliderView(mContext);
		sliderView.setAdapter(new EventMonthAcademicaCalendarListSliderAdapter(this, mCurrentTimestamp,
			new OnMonthChangeListener() {
				@Override
				public void onMonthChange(EventMonthAcademicaCalendarListSliderAdapter adapter) {
					mSecondarySliderBar.showPreviousNext();
					mSecondarySliderBar.setAllTitles(
							adapter.monthTitle(ScreenPosition.Previous, false), 
							adapter.monthTitle(ScreenPosition.Current, true), 
							adapter.monthTitle(ScreenPosition.Next, false));					
				}
			}
		));
		
		mSecondarySliderBar.setPreviousNextListener(new OnPreviousNextListener() {
			@Override
			public void onPreviousClicked() {
				sliderView.slideLeft();					
			}

			@Override
			public void onNextClicked() {
				sliderView.slideRight();
			}
		});
		setContentView(sliderView, false);		
	}
	
	private void loadBrowseCategoriesView() {
		mLoader.showLoading();
		setContentView(mLoader, false);
		if (EventsModel.categoriesAvailable()) {
			EventsModel.fetchCategories(this, handleCategories());
		} else {
			// activity will need to gracefully exist
			finish();
			return;
		}
	}
	
	private Handler handleCategories() {
		return new Handler() {
			
			@Override
			public void handleMessage(Message message) {
				if(message.arg1 == MobileWebApi.SUCCESS) {
					List<EventCategoryItem> categories = EventsModel.getCategories();
					loadCategories(categories);
				} else {
					mLoader.showError();
				}
			}
		};
	}
	
	private void loadCategories(List<EventCategoryItem> categories) {
		ListView listView = new ListView(this);
		SimpleArrayAdapter<EventCategoryItem> adapter = new EventCategoryArrayAdapter(mContext, categories, null);
		adapter.setOnItemClickListener(listView, 
			new SimpleArrayAdapter.OnItemClickListener<EventCategoryItem>() {
				@Override
				public void onItemSelected(EventCategoryItem item) {
					if (item.subcats == null) {
						MITEventsDaysSliderActivity.launchCategory(mContext, item.catid, item.name, "Events", null);
					} else {
						Intent intent = new Intent(mContext, EventsCategoryActivity.class);
						intent.putExtra(EventsCategoryActivity.CATEGORY_ID_KEY, item.catid);
						mContext.startActivity(intent);
					}
				}
			}
		);
		listView.setAdapter(adapter);
		listView.setBackgroundColor(getResources().getColor(R.color.rowBackground));
		setContentView(listView, false);
	}
	
	private void loadHolidays() {
		setContentView(mLoader, false);
		mLoader.showLoading();
		
		EventsModel.fetchHolidays(this, new Handler() {
			@Override
			public void handleMessage(Message message) {
				if(message.arg1 == MobileWebApi.SUCCESS) {
					List<EventDetailsItem> events = EventsModel.getHolidays();
					ListAdapter listAdapter = new EventsArrayAdapter(mContext, R.layout.events_row, 0, events, EventDetailsItem.SHORT_DAYS_ONLY);
					ListView listView = new ListView(mContext);
					listView.setBackgroundColor(mContext.getResources().getColor(R.color.rowBackground));
					listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> listView, View view, int position, long arg3) {
							EventDetailsItem eventDetails = (EventDetailsItem) listView.getItemAtPosition(position);
							MITEventsSliderActivity.launchHolidaysCalendar(mContext, eventDetails.id);
						}
					});
					listView.setAdapter(listAdapter);
					
					setContentView(listView, false);
					
				} else {
					mLoader.showError();
				}
			}			
		});		
	}
}
