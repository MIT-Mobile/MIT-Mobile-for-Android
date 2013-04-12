package edu.mit.mitmobile2.events;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.SliderListNewModuleActivity;
import edu.mit.mitmobile2.SmallActivityCache;
import edu.mit.mitmobile2.objs.EventDetailsItem;


/*
 * This is meant a simpler version of MITEventsSliderActivity
 * might be able replace MITEventsSliderActivity with this activity
 */
public class EventsDetailsSliderActivity extends SliderListNewModuleActivity {
	
	private static SmallActivityCache<List<EventDetailsItem>> sEventsCache = new SmallActivityCache<List<EventDetailsItem>>();
	private static String CACHE_KEY = "cache_key";
	private static String EVENT_ID_KEY = "event_id";
	private List<EventDetailsItem> mEvents;

	boolean mBriefMode = false;
	private String mInitialEventId;

	public static void launch(Context context, List<EventDetailsItem> events, String eventId) {
		long cacheKey = sEventsCache.put(events);
		Intent intent = new Intent(context, EventsDetailsSliderActivity.class);
		intent.putExtra(CACHE_KEY, cacheKey);
		intent.putExtra(EVENT_ID_KEY, eventId);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mEvents = sEventsCache.getItem(getIntent().getLongExtra(CACHE_KEY, 0));
		if (mEvents == null) {
			// fail gracefully
			finish();
			return;
		}

		mInitialEventId = getIntent().getStringExtra(EVENT_ID_KEY);
		createViews();
	}
	
    void createViews() {
    	
    	for (EventDetailsItem event : mEvents) {
    		EventDetailsView eventView = new EventDetailsView(this, event, mBriefMode);
    		addScreen(eventView, event.title, "Event Details");    
    	}
    	
    	int position = EventsModel.getPosition(mInitialEventId, mEvents);
    	setPosition(position);        
    }

	@Override
	protected NewModule getNewModule() {
		return new EventsModule();
	}

	@Override
	protected void onOptionSelected(String optionId) { }

	@Override
	protected boolean isModuleHomeActivity() {
		return false;
	}

}
