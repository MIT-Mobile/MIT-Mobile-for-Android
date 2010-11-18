package edu.mit.mitmobile.maps;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;
import edu.mit.mitmobile.LoaderBar;
import edu.mit.mitmobile.MITNewsWidgetActivity;
import edu.mit.mitmobile.MITSearchRecentSuggestions;
import edu.mit.mitmobile.MobileWebApi;
import edu.mit.mitmobile.Module;
import edu.mit.mitmobile.R;
import edu.mit.mitmobile.TitleBar;
import edu.mit.mitmobile.objs.MapItem;
import edu.mit.mitmobile.objs.RouteItem;
import edu.mit.mitmobile.shuttles.RoutesParser;
import edu.mit.mitmobile.shuttles.ShuttlesActivity;
import edu.mit.mitmobile.shuttles.ShuttlesModule;

public class MITMapActivity extends MapActivity {

	MITMapView mapView;
	
	
	// used to reset out of List Mode
	public static final String KEY_VIEW_PINS = "view_pins";
	
	// sent only by Event:
	public static final String KEY_LON = "lon";
	public static final String KEY_LAT = "lat";
	
	public static final String KEY_TITLE = "title";
	public static final String KEY_SNIPPET = "snippet";
	public static final String KEY_MODULE = "module";
	public static final String KEY_HEADER_TITLE = "header_title";
	public static final String KEY_HEADER_SUBTITLE = "header_subtitle";
	
	// parameters for shuttl
	public static final String KEY_SHUTTLE_STOPS = "shuttle_stops";
	public static final String KEY_ROUTE = "shuttle_route";
	
	// sent by Stellar and Events
	public static final String KEY_LOCATION = "location";
	
	// TODO may not need (activityForResult)
	public static final String MODULE_SHUTTLE = "shuttle";
	public static final String MODULE_STELLAR = "stellar";
	public static final String MODULE_CALENDAR = "calendar";

	static final int MENU_HOME   = Menu.FIRST;

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
	

	public static final String KEY_POSITION = "pos";
	protected static final String SEARCH_TERM_KEY = "search";

	MapUpdaterTask mut;
	
	FixedMyLocation myLocationOverlay;
	MapController mctrl;
	MITItemizedOverlay markers;
	List<MapItem> mMapItems;
	GeoPoint ev_gpt;
	GeoPoint center;
	
	String title;
	String snippet;
	String module;
	String findLoc;
	int bubble_pos = -1;
	
	String mHeaderTitle = null;
	String mHeaderSubtitle = null;
	
	RouteItem mRouteItem = null;
	
	MITMapsDataModel mdm;
	
	ListView mListView;
	
	static int INIT_ZOOM = 17;
	static int INIT_ZOOM_ONE_ITEM = 18;
	
	Context ctx;

	static final String MAP_ITEMS_KEY = "map_items";
	
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
		Intent i = new Intent(context, MITMapActivity.class);  
		
		ArrayList<MapItem> mapItems = new ArrayList<MapItem>();
		mapItems.add(focusedMapItem);
		
		i.putParcelableArrayListExtra(MAP_ITEMS_KEY, mapItems);
		
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.putExtra(KEY_VIEW_PINS, true);
		context.startActivity(i);
	};
	
	/*******************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
	    
	    ctx = this;

    	Bundle extras = getIntent().getExtras();
    	
        if (extras!=null){ 

        	title   = extras.getString(KEY_TITLE);   
        	snippet = extras.getString(KEY_SNIPPET);   
        	
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
        	
        	/////////////////////
        	
        	module  = extras.getString(KEY_MODULE); 
        	
        	bubble_pos = extras.getInt(KEY_POSITION,-1);
        	
        	findLoc = extras.getString(KEY_LOCATION); 
        	
        	String action = getIntent().getAction();
    		if(action != null && action.equals(Intent.ACTION_SEARCH)) {
    			findLoc = extras.getString(SearchManager.QUERY);
    		}
        	
        	mHeaderTitle = extras.getString(KEY_HEADER_TITLE);
        	mHeaderSubtitle = extras.getString(KEY_HEADER_SUBTITLE);
        	
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
        
        
     
       //gpt = new GeoPoint(42365362,-71103473);	// Porter
    	center = new GeoPoint(42359238,-71093109);	// MIT
	    
	    setContentView(R.layout.maps);
	    
	    mListView = (ListView) findViewById(R.id.mapListView);

	    TitleBar titleBar = (TitleBar) findViewById(R.id.mapTitleBar);
	    if(module != null) {
	    	if(module.equals(MODULE_SHUTTLE)) {
	    		titleBar.setTitle("Route Map");
	    	}
	    } else {
	    	titleBar.setTitle("Campus Map");
	    }
	    
	    if(mHeaderTitle != null) {
	    	findViewById(R.id.mapHeader).setVisibility(View.VISIBLE);
	    	
	    	TextView headerTitleTV = (TextView) findViewById(R.id.mapHeaderTitle);
	    	headerTitleTV.setText(mHeaderTitle);
	    	
	    	TextView headerSubtitleTV = (TextView) findViewById(R.id.mapHeaderSubtitle);
	    	if(mHeaderSubtitle != null) {
	    		headerSubtitleTV.setText(mHeaderSubtitle);
	    	} else {
	    		headerSubtitleTV.setVisibility(View.GONE);
	    	}
	    }
	    
	    mdm = new MITMapsDataModel();
	    
		mapView = (MITMapView) findViewById(R.id.mapview);
		
		mapView.setBuiltInZoomControls(true);	
		
		
		if (findLoc==null) {
			if (mMapItems==null) {
				
				mMapItems = loadMapItems(getIntent()); // passed from Browse or Search?
				if (mMapItems==null) {
					mMapItems = new ArrayList<MapItem>();  // empty ok
				}
			}
			setOverlays();  	
		} else {
			doSearch();
		}
	
	}

	private List<MapItem> loadMapItems(Intent intent) {
		return intent.getParcelableArrayListExtra(MAP_ITEMS_KEY);
	}
	
	@Override
	public void onNewIntent(Intent newIntent) {
		super.onNewIntent(newIntent);
				
		String action = newIntent.getAction();
		
		if(action != null && action.equals(Intent.ACTION_SEARCH)) {
			findLoc = newIntent.getStringExtra(SearchManager.QUERY);
			doSearch();
		} else {			
			mListView.setVisibility(View.GONE);
			mapView.setVisibility(View.VISIBLE);
			
			if(newIntent.hasExtra(KEY_VIEW_PINS)) {
				mMapItems = loadMapItems(newIntent);
				setOverlays();
			}
		}
		
	}
	
	@Override
	public boolean onSearchRequested() {
		if (MODULE_SHUTTLE.equals(module)) return false;
		return super.onSearchRequested();
	}

	private void doSearch() {
		final LoaderBar loaderBar = (LoaderBar) findViewById(R.id.mapSearchLoader);
		loaderBar.setLoadingMessage("Searching for " + findLoc);
		loaderBar.setFailedMessage("Search failed!");
		loaderBar.enableAnimation();
		loaderBar.startLoading();
		
		final Handler updateResultsUI = new Handler() {
			
			@Override
			public void handleMessage(Message message) {
				if(message.arg1 == MobileWebApi.SUCCESS) {
					mMapItems = MITMapsDataModel.getSearchResults(findLoc);
					if(mMapItems.size() == 0) {
						Toast.makeText(MITMapActivity.this, "No matches found", Toast.LENGTH_LONG).show();
					}
					setOverlays();
					loaderBar.setLastLoaded(new Date());
				} else {
					Toast.makeText(MITMapActivity.this, MobileWebApi.NETWORK_ERROR, Toast.LENGTH_LONG).show();
					loaderBar.errorLoading();
				}
			}
		};
				
		MITSearchRecentSuggestions suggestions = new MITSearchRecentSuggestions(this, MapsSearchSuggestionsProvider.AUTHORITY, MapsSearchSuggestionsProvider.MODE);
		suggestions.saveRecentQuery(findLoc.toLowerCase(), null);
		
		MITMapsDataModel.executeSearch(findLoc, updateResultsUI, this);
	}
	/****************************************************/
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.lowMemory = true;
	}

	/****************************************************/
	void setOverlays() {

		Drawable pin;
		GeoPoint gpt = center;

		mctrl = mapView.getController();
		
		List<Overlay>  ovrlys = mapView.getOverlays();

		if (markers!=null) {
			mapView.removeAllViews();
			ovrlys.remove(markers);
		}
		
		// My Location
	    myLocationOverlay = new FixedMyLocation(this, mapView);
		ovrlys.add(myLocationOverlay);
		myLocationOverlay.enableMyLocation();

	    
		int size = 0;
		
		// Building or Shuttle?
		if (MODULE_SHUTTLE.equals(module)) {
			
		    setTitle("Shuttles Map");

			if (mut!=null) mut.cancel(true); 
			
		    mut = new MapUpdaterTask(ctx, mapView, mRouteItem);
		    
		    mut.execute(RoutesParser.ROUTES_BASE_URL+"?command=routeInfo&full=true", null, null);
		    
		    markers = mut.stopsMarkers;
		    
			size = markers.size();
			
		} else {

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
			markers.makeBalloon(p,0); 
		} else if (bubble_pos>-1) {
			PinItem p = (PinItem) markers.getItem(bubble_pos);
			markers.makeBalloon(p,bubble_pos); 
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
		
		
		
		// possibly catch memory leaks?
		ZoomButtonsController zoomctrl = mapView.getZoomButtonsController(); 
		zoomctrl.setOnZoomListener(new ZoomButtonsController.OnZoomListener() {
		        public void onZoom(boolean zoomIn) {
		            try{
		                System.gc();
		                if(zoomIn) mctrl.zoomIn();
		                else mctrl.zoomOut();
		                System.gc();
		            }
		            catch(OutOfMemoryError e)
		            {
		                e.printStackTrace();
		            }
		            catch (Exception e)
		            {
		            }               
		        }
		        public void onVisibilityChanged(boolean visible) {
		        	
		        }
		    }
		);
		
		
		
	}
	
	/****************************************************/
	@Override
	public void onResume() {
		super.onResume();
		if (myLocationOverlay!=null) myLocationOverlay.enableMyLocation();
		if (mut!=null) mut.pause = false;  
		//if (mut!=null) mut.notify();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (myLocationOverlay!=null) myLocationOverlay.disableMyLocation();
		//if (mut!=null) mut.cancel(true); // would need to recreate in onResume()
		if (mut!=null) mut.pause = true;  // TODO maybe wait()
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.stop();
		if (mut!=null) mut.cancel(true); 
	}
	
	/****************************************************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		
		switch (item.getItemId()) {
		case MENU_HOME:
			i = new Intent(this,MITNewsWidgetActivity.class);  
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
	/*******************************/
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	/*******************************/
	// TODO set configChanges attrib
	/*
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    // Checks the orientation of the screen
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	    	//
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	        //
	    }
	}
	*/
}
