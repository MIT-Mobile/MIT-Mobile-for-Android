package edu.mit.mitmobile2.events;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SmallActivityCache;
import edu.mit.mitmobile2.maps.MITMapView;
import edu.mit.mitmobile2.objs.EventDetailsItem;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.MapPoint;

public class EventsMapActivity extends NewModuleActivity {

	private static SmallActivityCache<List<EventDetailsItem>> sEventsCache = new SmallActivityCache<List<EventDetailsItem>>();
	private static String CACHE_KEY = "cache_key";
	private List<EventDetailsItem> mEvents;

	private MITMapView mMapView;

	public static void launch(Context context, List<EventDetailsItem> events) {
		long cacheKey = sEventsCache.put(events);
		Intent intent = new Intent(context, EventsMapActivity.class);
		intent.putExtra(CACHE_KEY, cacheKey);
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

		setContentView(R.layout.maps);
		mMapView = (MITMapView) findViewById(R.id.map);

		populateMapView();
	}

	private void populateMapView() {
		ArrayList<MapItem> eventMapItems = new ArrayList<MapItem>();
		for (EventDetailsItem event : mEvents) {
			if (event.coordinates != null) {
				eventMapItems.add(new EventMapItem(event));
			}
		}

		mMapView.addMapItems(eventMapItems, "events");
		mMapView.fitMapItems();
	}

	private class EventMapItem extends MapItem {
		public EventMapItem(EventDetailsItem event) {
			MapPoint coordinates = new MapPoint();
			coordinates.lat_wgs84 = event.coordinates.lat;
			coordinates.long_wgs84 = event.coordinates.lon;
			mapPoints.add(coordinates);
			setGeometryType(MapItem.TYPE_POINT);

		}

		@Override
		public View getCallout(Context mContext) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public View getCallout(Context mContext, ArrayList<? extends MapItem> mapItems) {
			return null;
		}

		@Override
		public View getCallout(final Context context, ArrayList<? extends MapItem> mapItems, int position) {

			final EventDetailsItem event = mEvents.get(position);

	   		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout calloutLayout = (LinearLayout) inflater.inflate(R.layout.events_map_callout, null);
			
			TextView titleView = (TextView)calloutLayout.findViewById(R.id.event_callout_title);
			TextView subtitleView = (TextView) calloutLayout.findViewById(R.id.event_callout_subtitle);
			
			titleView.setText(event.title);
			subtitleView.setText(event.getTimeSummary(EventDetailsItem.LONG_DAY_TIME));


			calloutLayout.setOnClickListener(new View.OnClickListener() {
				@Override
		        public void onClick(View v) {
		            EventsDetailsSliderActivity.launch(context, mEvents, event.id);
		        }
		    });
			
			return calloutLayout;
		}

	}

	@Override
	protected NewModule getNewModule() {
		return new EventsModule();
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}

	@Override
	protected List<MITMenuItem> getPrimaryMenuItems() {
		List<MITMenuItem> baseItems = super.getPrimaryMenuItems();		
		ArrayList<MITMenuItem> menuItems = new ArrayList<MITMenuItem>();
		menuItems.addAll(baseItems);
		menuItems.add(new MITMenuItem("list", "List", R.drawable.menu_browse));		
		return menuItems;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		if (optionId.equals("list")) {
			finish();
		}
	}

	@Override
	protected boolean isModuleHomeActivity() {
		return false;
	}

}
