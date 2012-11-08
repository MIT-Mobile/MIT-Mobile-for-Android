package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.LocationService;
import com.esri.core.geometry.Point;
import com.google.android.maps.GeoPoint;

import edu.mit.mitmobile2.HomeScreenActivity;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TitleBar;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.RouteItem;
import edu.mit.mitmobile2.people.PeopleDB;
import edu.mit.mitmobile2.people.PeopleModule;
import edu.mit.mitmobile2.shuttles.RoutesParser;
import edu.mit.mitmobile2.shuttles.ShuttlesActivity;
import edu.mit.mitmobile2.shuttles.ShuttlesModule;

public class MITMapActivity2 extends MapBaseActivity2 {

	private static final String TAG = "MITMapActivity2";
	
	// parameters for shuttles
	public static final String KEY_SHUTTLE_STOPS = "shuttle_stops";
	public static final String KEY_ROUTE = "shuttle_route";

	// sent only by Event:
	public static final String KEY_LON = "lon";
	public static final String KEY_LAT = "lat";
	public static final String KEY_LOCATION = "location";
	
	// TODO may not need (activityForResult)
	public static final String MODULE_SHUTTLE = "shuttle";
	//public static final String MODULE_CALENDAR = "calendar";

	private static String MENU_HOME = "home";
	private static String MENU_MY_LOCATION = "my_location";
	private static String MENU_BROWSE = "browse";
	private static String MENU_BOOKMARKS = "bookmarks";
	private static String MENU_SEARCH = "search";

	// Generic Menu
	static final int MENU_MYLOC  = Menu.FIRST+2;

	// Shuttle Menu


	private MITMapShuttlesUpdaterTask mut;
	private MITItemizedOverlay markers;
	private GeoPoint ev_gpt;
	private  RouteItem mRouteItem = null;
    private Context mContext;
	/****************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		mContext = this;
		Log.d(TAG,"onCreate() " + this.hashCode());
	    super.onCreate(savedInstanceState);
	}
	
	protected List<MITMenuItem> getSecondaryMenuItems() {
		return Arrays.asList(
			new MITMenuItem("browse", "Browse", R.drawable.menu_browse),
	     	new MITMenuItem("search", "Search", R.drawable.menu_search)
		);
	}
	
	@Override
	protected NewModule getNewModule() {
		return new MapsModule2();
	}
	
	@Override
	protected void onOptionSelected(String id) {
		Log.d(TAG,"option selected = " + id);
	    if (id.equals(MITMapActivity2.MENU_HOME)) {
	    	onHomeRequested();
	    }
		if (id.equals(MITMapActivity2.MENU_MY_LOCATION)) {
	    	onMyLocationRequested();
	    }
	    if (id.equals(MITMapActivity2.MENU_BROWSE)) {
	    	onBrowseRequested();
	    }
	    if (id.equals(MITMapActivity2.MENU_BOOKMARKS)) {
		    Intent i = new Intent(this,MITMapBrowseCatsActivity.class);  
			startActivity(i);
	    }
	    if (id.equals(MITMapActivity2.MENU_SEARCH)) {
		    Intent i = new Intent(this,MITMapBrowseCatsActivity.class);  
			startActivity(i);
	    }

	}
	
	protected void onHomeRequested() {
		Intent i = new Intent(this, HomeScreenActivity.class);  
		startActivity(i);
		finish();
	}
	
	protected void onBrowseRequested() {
	    Intent i = new Intent(this,MITMapBrowseCatsActivity.class);  
		startActivity(i);
	}

	protected void onMyLocationRequested() {

		ls.setAccuracyCircleOn(true);
		if (ls.isStarted()) {
			ls.stop();
		}
		else {
			ls.start();
			map.centerAt(ls.getPoint(),true);
		}		
	}
	
	public boolean onSearchRequested() {
		if (MODULE_SHUTTLE.equals(module)) return false;
		return super.onSearchRequested();
	}

	public static void viewMapItem(Context context, MapItem focusedMapItem) {	
		ArrayList<MapItem> mapItems = new ArrayList<MapItem>();
		mapItems.add(focusedMapItem);
		Intent i = new Intent(context, MITMapActivity2.class);  
		i.putParcelableArrayListExtra(MAP_ITEMS_KEY, mapItems);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.putExtra(KEY_VIEW_PINS, true);
		context.startActivity(i);
	};	
	
	protected void processExtras(Bundle extras) {
		Log.d(TAG,"processExtras");
		debugLayerIdMap(TAG);
		
        if (extras!=null){ 

        	Iterator it = extras.keySet().iterator();
	   		while (it.hasNext()) {
	   			Object o = it.next();
	   			Log.d(TAG,o.getClass().getCanonicalName());
	   			//Map.Entry pairs = (Bundle.Entry)it.next();
	   			//Log.d(TAG,"extras: " + pairs.getKey() + " = " + pairs.getValue());
	   		}
	   		
	   		String findLoc = null;
        	Point point;
//
        	findLoc = extras.getString(KEY_LOCATION); 
        	
	
        }
        else {
        	Log.d(TAG,"extras is null");
        }
	}
		
}
