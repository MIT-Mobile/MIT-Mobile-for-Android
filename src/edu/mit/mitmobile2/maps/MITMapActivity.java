package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.maps.GeoPoint;

import edu.mit.mitmobile2.HomeScreenActivity;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TitleBar;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.RouteItem;
import edu.mit.mitmobile2.shuttles.RoutesParser;
import edu.mit.mitmobile2.shuttles.ShuttlesActivity;
import edu.mit.mitmobile2.shuttles.ShuttlesModule;

public class MITMapActivity extends MapBaseActivity {

	// parameters for shuttles
	public static final String KEY_SHUTTLE_STOPS = "shuttle_stops";
	public static final String KEY_ROUTE = "shuttle_route";

	// sent only by Event:
	public static final String KEY_LON = "lon";
	public static final String KEY_LAT = "lat";
	
	// sent by Stellar and Events
	public static final String KEY_LOCATION = "location";
	
	// TODO may not need (activityForResult)
	public static final String MODULE_SHUTTLE = "shuttle";
	//public static final String MODULE_STELLAR = "stellar";
	//public static final String MODULE_CALENDAR = "calendar";

	// Generic Menu
	static final int MENU_SEARCH = Menu.FIRST+1;
	static final int MENU_MYLOC  = Menu.FIRST+2;
	static final int MENU_BOOKMARKS = Menu.FIRST+3;
	static final int MENU_BROWSE = Menu.FIRST+4;

	// Shuttle Menu
	static final int MENU_SHUTTLES = Menu.FIRST+5;
	//static final int MENU_REFRESH  = Menu.FIRST+6;
	static final int MENU_SHUTTLE_LIST_VIEW = Menu.FIRST+7;
	static final int MENU_MAP_LIST_VIEW = Menu.FIRST+8;
	static final int MENU_CALL_SAFERIDE = Menu.FIRST+9;

	private MITMapShuttlesUpdaterTask mut;
	private MITItemizedOverlay markers;
	private GeoPoint ev_gpt;
	private  RouteItem mRouteItem = null;

	/****************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
	    
    	Bundle extras = getIntent().getExtras();
    	
    	String findLoc = null;
        if (extras!=null){ 

        	findLoc = extras.getString(KEY_LOCATION); 
        	
        	// used by Events:
        	if(extras.containsKey(KEY_LON) && extras.containsKey(KEY_LAT)) {
            	int lon,lat;
            	lon = extras.getInt(KEY_LON);   
            	lat = extras.getInt(KEY_LAT); 
            	ev_gpt = new GeoPoint(lon,lat);
        	}
        	
        	if (ev_gpt!=null) {
        		mMapItems = new ArrayList<MapItem>();
        		MapItem m = new MapItem();
        		m.name = title;
        		m.snippets = snippet;
        		mMapItems.add(m);
        	}
        	
        	
        	if(module != null && module.equals(MODULE_SHUTTLE)) {
        		mRouteItem = extras.getParcelable(KEY_ROUTE);
        	}
        	
        } 
        
        
        // Four cases:
        //
        // 1 - Events sends LAT/LON
        // 2 - Stellar sends findLoc query that should yield ONE building
        // 3 - Map Search sends many buildings
        // 4 - Shuttle sends many stops
        
     
    	center = new GeoPoint(42359238,-71093109);	// MIT
	    
    	
	    mListView = (ListView) findViewById(R.id.mapListView);

	    TitleBar titleBar = (TitleBar) findViewById(R.id.mapTitleBar);
	    if(module != null) {
	    	if(module.equals(MODULE_SHUTTLE)) {
	    		titleBar.setTitle("Route Map");
	    	}
	    } else {
	    	titleBar.setTitle("Campus Map");
	    }
	    

		if (findLoc==null) {
			if (mMapItems==null) {
				
				mMapItems = loadMapItems(getIntent()); // passed from Browse or Search?
				if (mMapItems==null) {
					mMapItems = new ArrayList<MapItem>();  // empty ok
				}
			}
			setOverlays();  	
		} else {
			doSearch(findLoc);
		}
	
	}
	/*
	 * launches a new map activity with pins already set
	 */
	public static void launchNewMapItems(Context context, List<MapItem> mapItems) {
		Intent i = new Intent(context, MITMapActivity.class); 
		i.putParcelableArrayListExtra(MAP_ITEMS_KEY, new ArrayList<MapItem>(mapItems));
		context.startActivity(i);
	}
	
	public static void launchNewMapItem(Context context, MapItem mapItem) {
		ArrayList<MapItem> mapItems = new ArrayList<MapItem>();
		mapItems.add(mapItem);
		launchNewMapItems(context, mapItems);
	}
	
	/*
	 *  goes to currently running map activity and puts pins on it
	 */
	public static void viewMapItem(Context context, MapItem focusedMapItem) {	
		ArrayList<MapItem> mapItems = new ArrayList<MapItem>();
		mapItems.add(focusedMapItem);
		Intent i = new Intent(context, MITMapActivity.class);  
		i.putParcelableArrayListExtra(MAP_ITEMS_KEY, mapItems);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.putExtra(KEY_VIEW_PINS, true);
		context.startActivity(i);
	};
	/****************************************************/
	@Override
	public boolean onSearchRequested() {
		if (MODULE_SHUTTLE.equals(module)) return false;
		return super.onSearchRequested();
	}

	/****************************************************/
	@Override
	protected void setOverlays() {

		super.setOverlays();

		if (markers!=null) {
			mapView.removeAllViews();
			ovrlys.remove(markers);
		}
		
		int size = 0;
		
		// Building or Shuttle?
		if (MODULE_SHUTTLE.equals(module)) {
			
		    setTitle("Shuttles Map");

			if (mut!=null) mut.cancel(true); 
			
		    mut = new MITMapShuttlesUpdaterTask(ctx, mapView, mRouteItem);
		    
		    RoutesParser rp = new RoutesParser();
		    mut.execute(rp.getBaseUrl()+"?command=routeInfo&full=true", null, null);
		    
		    markers = mut.stopsMarkers;
		    
			size = markers.size();
			
		} else {

			Drawable pin;
			GeoPoint gpt;
			
			// handles Events, Map search, and Stellar...
			
			pin = this.getResources().getDrawable(R.drawable.map_red_pin);
			markers = new MITItemizedOverlay(pin, this, mapView);


			// Convert MapItem to PinItems
			int lat,lon;
			String title,name;
			for (MapItem m : mMapItems) {
				lat = (int) (m.lat_wgs84 * 1000000.0);
				lon = (int) (m.long_wgs84 * 1000000.0);
				gpt = new GeoPoint(lat,lon);
				
				if ("".equals(m.bldgnum)) title = m.name;
				else title = "Building " + m.bldgnum;
				
				if (title.equals(m.name)) name = "";
				else name = m.name;
				
				PinItem p = new PinItem(gpt, title, name, m);
				markers.addOverlay(p);
			}
			
			size = markers.size();
			
			if (size>0) ovrlys.add(markers);
			
		}
		
		
		// Show balloon if single item or direct from shuttle stop details view
		if (size==1) {
			PinItem p = (PinItem) markers.getItem(0);
			markers.makeBalloon(p); 
		} else if (bubble_pos>-1) {
			PinItem p = (PinItem) markers.getItem(bubble_pos);
			markers.makeBalloon(p); 
		}
		
		// Try to center map...
		if (size>1) {
			int latSpanE6 = 10000;
			int lonSpanE6 = 10000;
			// #1 TODO? seems unreliable (only computes after rendered?)
			//center = markers.getCenter();
			//int latSpan = markers.getLatSpanE6();
			//int lonSpan = markers.getLonSpanE6();
			// #2
			PinItem p = (PinItem) markers.getItem(0);
			GeoPoint g = p.getPoint();
			int maxLat = g.getLatitudeE6();
			int minLat = maxLat;
			int maxLon = g.getLongitudeE6();
			int minLon = maxLon;
			int lat,lon;
			for (int x=1; x<size; x++) {
				p = (PinItem) markers.getItem(x);
				g = p.getPoint();
				lat = g.getLatitudeE6();
				lon = g.getLongitudeE6();
				if (lat<minLat) minLat = lat;
				if (lat>maxLat) maxLat = lat;
				if (lon<minLon) minLon = lon;
				if (lon>maxLon) maxLon = lon;
			}
			lat = (maxLat-minLat)/2 + minLat;
			lon = (maxLon-minLon)/2 + minLon;
	    	center = new GeoPoint(lat,lon);
			mctrl.setCenter(center);
			if (maxLat-minLat>0) latSpanE6 = maxLat-minLat;
			if (maxLon-minLon>0) lonSpanE6 = maxLon-minLon;
			//mctrl.zoomToSpan(latSpanE6, lonSpanE6);
			final int latSpan = (int) (latSpanE6*0.90);
			final int lonSpan = (int) (lonSpanE6*0.90);
			mapView.post(new Runnable() {
				@Override
				public void run() {
					// MapView ignores following unless using post() (needs to render first)
					mctrl.zoomToSpan(latSpan, lonSpan);
					int z = mapView.getZoomLevel();
					if (z<15) {
						mctrl.setZoom(15);
					}
				}
			});
			
		} else if (size==1) {
			mctrl.setCenter(center);
			mapView.post(new Runnable() {
				@Override
				public void run() {
					// MapView ignores following unless using post() (needs to render first)
					mctrl.setZoom(INIT_ZOOM_ONE_ITEM);
				}
			});
		} else {
			
			// Initial zoom out
			mctrl.setCenter(center);
			mapView.post(new Runnable() {
				@Override
				public void run() {
					mctrl.setZoom(INIT_ZOOM_ONE_ITEM);
				}
			});
			
			myLocationOverlay.snapFirstTime = true;
		}
		
		
	}
	/****************************************************/
	@Override
	public void onResume() {
		super.onResume();
		if (mut!=null) mut.pause = false;  
		//if (mut!=null) mut.notify();
	}

	@Override
	public void onPause() {
		super.onPause();
		//if (mut!=null) mut.cancel(true); // would need to recreate in onResume()
		if (mut!=null) mut.pause = true;  // TODO maybe wait()
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mut!=null) mut.cancel(true); 
	}
	/****************************************************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		
		switch (item.getItemId()) {
		case MENU_HOME:
			i = new Intent(this,HomeScreenActivity.class);  
			startActivity(i);
			finish();
			break;
		case MENU_SEARCH: 
			//SearchManager sm = (SearchManager) ctx.getSystemService(Context.SEARCH_SERVICE);
			//sm.startSearch(null, false, null, false);
			onSearchRequested();
			break;
		case MENU_MYLOC: 
			GeoPoint me = myLocationOverlay.getMyLocation();
			if (me!=null) mctrl.animateTo(me);
			break;
		case MENU_BOOKMARKS: 
			i = new Intent(this,MITMapBrowseResultsActivity.class);  
			startActivity(i);
			break;
		case MENU_BROWSE: 
			i = new Intent(this,MITMapBrowseCatsActivity.class);  
			startActivity(i);
			break;
			
		case MENU_SHUTTLES: 
			i = new Intent(this,ShuttlesActivity.class);  
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
			break; 
			
		case MENU_SHUTTLE_LIST_VIEW:
			finish();
			break;
			
		case MENU_MAP_LIST_VIEW:
			if(mListView.getVisibility() == View.GONE) {
				MapItemsAdapter adapter = new MapItemsAdapter(this, mMapItems);
				mListView.setAdapter(adapter);
				mListView.setOnItemClickListener(adapter.showMapDetailsOnItemClickListener());
				mapView.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);
			} else {
				mListView.setVisibility(View.GONE);
				mapView.setVisibility(View.VISIBLE);
			}
			break;
			// FIXME
			/*
		case MENU_MAP_LIST_VIEW: 
			i = new Intent(this, MITMapActivity.class);
			i.putExtra(MITMapActivity.KEY_MODULE, MITMapActivity.MODULE_SHUTTLE); 
			RoutesAsyncListView sv = (RoutesAsyncListView) getScreen(getSelectedIndex());
			i.putExtra(MITMapActivity.KEY_HEADER_TITLE, sv.ri.title);
			Global.curStops = (ArrayList<Stops>) sv.m_stops;
			startActivity(i);
			break;
			*/
		case MENU_CALL_SAFERIDE: 
			i = new Intent(Intent.ACTION_DIAL);
			i.setData(Uri.parse("tel:617-253-2997"));
			startActivity(i);
			break;

		}

		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		menu.clear();
		
		menu.add(0, MENU_HOME, Menu.NONE, "Home")
		  .setIcon(R.drawable.menu_home);
		
		if (module != null && module.equals(MODULE_SHUTTLE)) {
			Module shuttleModule = new ShuttlesModule();
			menu.add(0, MENU_SHUTTLES, Menu.NONE, shuttleModule.getMenuOptionTitle())
			  .setIcon(shuttleModule.getMenuIconResourceId());
			menu.add(0, MENU_SHUTTLE_LIST_VIEW, Menu.NONE, "List View")
			  .setIcon(R.drawable.menu_browse);
			//menu.add(0, MENU_CALL_SAFERIDE, Menu.NONE, "Saferide")
			//	.setIcon(android.R.drawable.ic_menu_call);
		} else {
			menu.add(0, MENU_SEARCH, Menu.NONE, "Search")
			  .setIcon(R.drawable.menu_search);
			menu.add(0, MENU_MYLOC, Menu.NONE, "My Location") 
			  .setIcon(R.drawable.menu_mylocation);
			menu.add(0, MENU_BOOKMARKS, Menu.NONE, "Bookmarks")
			  .setIcon(R.drawable.menu_bookmarks);
			menu.add(0, MENU_BROWSE, Menu.NONE, "Browse")
			  .setIcon(R.drawable.menu_browse);
			
			if(mMapItems.size() > 0) {
				if(mListView.getVisibility() == View.GONE) {
					menu.add(0, MENU_MAP_LIST_VIEW, Menu.NONE, "List")
					  .setIcon(R.drawable.menu_view_as_list);
				} else {
					menu.add(0, MENU_MAP_LIST_VIEW, Menu.NONE, "Map")
					  .setIcon(R.drawable.menu_view_on_map);
				}
			}
		}
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	
}
