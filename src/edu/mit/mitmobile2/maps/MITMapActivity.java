package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;

import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.HomeScreenActivity;
import edu.mit.mitmobile2.MITPlainSecondaryTitleBar;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TitleBarSwitch;
import edu.mit.mitmobile2.TitleBarSwitch.OnToggledListener;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.MapUpdater;

public class MITMapActivity extends NewModuleActivity {
	
	private static final String TAG = "MITMapActivity";
	public static final String KEY_VIEW_PINS = "view_pins";
	public MITMapView map;
    private FullScreenLoader mLoadingView;
	protected String module;
	Context mContext;
	Location location;
	Bundle extras;
	ArrayList<MapItem> mapItems;
	private MapUpdater mapUpdater;
	private HashMap params;
	private String query;
	protected ListView mListView;
	MapItemsAdapter adapter;
	ProgressDialog progress;
	TitleBarSwitch mMapListSwitch;
	private MITPlainSecondaryTitleBar mSecondaryTitleBar;

	private static String MENU_HOME = "home";
	private static String MENU_MY_LOCATION = "my_location";
	private static String MENU_BROWSE = "browse";
	private static String MENU_BOOKMARKS = "bookmarks";
	private static String MENU_SEARCH = "search";	
	protected static final String MAP_ITEMS_KEY = "map_items";
	public static final String MAP_DATA_KEY = "map_data";	
	public static final String MAP_ITEM_INDEX_KEY = "map_item_index";	
	public static final String MAP_UPDATER_KEY = "map_updater";
	public static final String MAP_UPDATER_PARAMS_KEY = "map_updater_params";
	
	public static final double INIT_RESOLUTION = 1.205;
	private static String LIST = "List";
	private static String MAP = "Map";
	static int INIT_ZOOM = 17; // DELETE ?
	static int INIT_ZOOM_ONE_ITEM = 18; // DELETE ?

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"oncreate()");
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(getLayoutID());
		
		mSecondaryTitleBar = new MITPlainSecondaryTitleBar(this);

		// hide the title bar initially since there are no results to display
		mSecondaryTitleBar.setVisibility(View.GONE);
		mMapListSwitch = new TitleBarSwitch(this);
		mMapListSwitch.setLabels(MAP, LIST);
		mMapListSwitch.setSelected(MAP);
		mMapListSwitch.setOnToggledListener(new OnToggledListener() {
			@Override
			public void onToggled(String selected) {
				toggleMapList(selected);
			}
		});
		
		mSecondaryTitleBar.addActionView(mMapListSwitch);
		
		getTitleBar().addSecondaryBar(mSecondaryTitleBar);
		
        mLoadingView = (FullScreenLoader) findViewById(getMapLoadingViewID());

        this.extras = this.getIntent().getExtras();
		
        map = (MITMapView)findViewById(getMapViewID());
        
        mListView = (ListView) findViewById(R.id.mapListView);

        //map.init(mContext);
				
		//Retrieve the non-configuration instance data that was previously returned. 
		Object init = getLastNonConfigurationInstance();
		if (init != null) {
			map.restoreState((String) init);
		}
		
	}
		
	
	@Override
	protected boolean isScrollable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isModuleHomeActivity() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/*
	 * launches a new map activity with pins already set
	 */
	public static void launchNewMapItems(Context context, List<MapItem> mapItems) {
		Log.d(TAG,"launchNewMapItems");
		Intent i = new Intent(context, MITMapActivity.class); 
		i.putExtra(MAP_ITEMS_KEY, new ArrayList<MapItem>(mapItems));
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(i);
	}
	
	public static void launchNewMapItem(Context context, MapItem mapItem) {
		ArrayList<MapItem> mapItems = new ArrayList<MapItem>();
		mapItems.add(mapItem);
		launchNewMapItems(context, mapItems);
	}

	@Override
	public void onDestroy() {
	    Log.i(TAG, "onDestroy()");
	    super.onDestroy();
	}
	
	 @Override
	 protected void onNewIntent(Intent intent) {
		Log.d(TAG,"onNewIntent");
	    this.extras = intent.getExtras();
	    
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	Log.d(TAG,"do search");
	        query = intent.getStringExtra(SearchManager.QUERY);
	        Log.d(TAG,"query = " + query);
	        MITMapsDataModel.executeSearch(query, mapSearchUiHandler, mContext);
	    }
	    else if(extras != null && extras.containsKey(MITMapView.MAP_DATA_KEY)) {
			mapItems = (ArrayList)extras.getParcelableArrayList(MITMapView.MAP_DATA_KEY);
			if (mapItems.size() > 0) {
				mSecondaryTitleBar.setTitle("\"" + query + "\":" + mapItems.size() + " results");
				mSecondaryTitleBar.setVisibility(View.VISIBLE);
			}
	    	map.addMapItems(mapItems);
	    	map.syncGraphicsLayers();
	    	map.fitMapItems();
	    }
	    
	 } // End of onN

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG,"onResume");
		map.unpause();
		
	}

	public MapUpdater getMapUpdater() {
		return mapUpdater;
	}

	public void setMapUpdater(MapUpdater mapUpdater) {
		this.mapUpdater = mapUpdater;
	}
	
	public HashMap getParams() {
		return params;
	}

	public void setParams(HashMap params) {
		this.params = params;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.d(TAG,"onPause()");
	}

	protected int getLayoutID() {
		return R.layout.maps;
	}
	
	protected int getMapViewID() {
		return R.id.map;
	}
	
    /* override this to handle the on map loaded event */
	protected void onMapLoaded() { }
	
    /* override this to set the extent of the map */
	protected void setExtent(double minX,double minY,double maxX, double maxY) { 

		Polygon initialExtent = new Polygon();

		// set start point
		initialExtent.startPath(new Point(minX,minY));

		// left side
		initialExtent.lineTo(minX,maxY);

		// top
		initialExtent.lineTo(maxX,maxY);

		// right
		initialExtent.lineTo(maxX,minY);
	
		//bottom
		initialExtent.lineTo(minX,minY);

		map.setExtent(initialExtent);

	}

	protected int getMapLoadingViewID() {
		return R.id.mapLoading;
	}
	
	@Override
	protected NewModule getNewModule() {
		return new MapsModule();
	}

	@Override
	protected void onOptionSelected(String id) {
		Log.d(TAG,"option selected = " + id);
	    if (id.equals(MENU_HOME)) {
	    	onHomeRequested();
	    }
		if (id.equals(MENU_MY_LOCATION)) {
	    	onMyLocationRequested();
	    }
	    if (id.equals(MENU_BROWSE)) {
	    	onBrowseRequested();
	    }
	    if (id.equals(MENU_BOOKMARKS)) {
			Intent i = new Intent(mContext, MapBookmarksActivity.class); 
			//i.putExtra(MAP_ITEMS_KEY, new ArrayList<MapItem>(mapItems));
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mContext.startActivity(i);

			startActivity(i);
	    }
	    if (id.equals(MENU_SEARCH)) {
		    onSearchRequested();
	    }

	}
	
	protected void onHomeRequested() {
		Intent i = new Intent(this, HomeScreenActivity.class);  
		startActivity(i);
		finish();
	}
	
	protected void onBrowseRequested() {
		if (this.getMapUpdater() != null) {
			this.getMapUpdater().stop();
		}
	    Intent i = new Intent(this,MITMapBrowseCatsActivity.class);  
		startActivity(i);
	}

	protected void onMyLocationRequested() {
		// location is always displayed in Map. Selecting my location just centers map to that point
		map.centerAt(map.ls.getPoint(),true);
	}
	
	@Override
	public boolean onSearchRequested() {
		Log.d(TAG,"onSearchRequested");
		if (this.getMapUpdater() != null) {
			this.getMapUpdater().stop();
		}
		return super.onSearchRequested();
	}

    public Handler mapSearchUiHandler = new Handler() {
        @SuppressWarnings("unchecked")
		@Override
        public void handleMessage(Message msg) {
        	Log.d(TAG,"mapSearchUiHandler success");
            if (msg.arg1 == MobileWebApi.SUCCESS) {
            	
            	try {
            		Log.d(TAG,"search results class = " + msg.obj.getClass().toString());
            		map.clearMapItems();
            		ArrayList mapItems = (ArrayList)msg.obj;
         			mSecondaryTitleBar.setTitle("\"" + query + "\":" + mapItems.size() + "  results");
         			mSecondaryTitleBar.setVisibility(View.VISIBLE);
            		
         			adapter = new MapItemsAdapter(mContext, mapItems);
         			mListView.setAdapter(adapter);
         			
         			mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

         				@Override
         				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         					ArrayList<MapItem> mapItems = map.getMao().getGraphicsLayers().get(MITMapView.DEFAULT_GRAPHICS_LAYER).getMapItems(); 					
         					MapItem mapItem = mapItems.get(position);
         		  			Intent i = new Intent(mContext, MITMapDetailsSliderActivity.class); 
        	            	i.putParcelableArrayListExtra(MITMapView.MAP_DATA_KEY, (ArrayList<? extends Parcelable>) mapItems);
        	            	i.putExtra(MITMapView.MAP_ITEM_INDEX_KEY, position);
        	            	mContext.startActivity(i);
         				}
         			});

         			map.addMapItems(mapItems);
            		map.syncGraphicsLayers();
                	map.fitMapItems();
            	}
            	catch (Exception e) {
            		Log.d(TAG,"mapSearchUiHander exception");
            		Log.d(TAG,e.getStackTrace().toString());
            	}
            }
            else if (msg.arg1 == MobileWebApi.ERROR) {

            } 
            else if (msg.arg1 == MobileWebApi.CANCELLED) {

            }
        }
    };
	
    private void toggleMapList(String selected) {
		if(selected.equals(LIST)) {
			mListView.setVisibility(View.VISIBLE);
			map.setVisibility(View.GONE);			
		} else if(selected.equals(MAP)) {
			mListView.setVisibility(View.GONE);
			map.setVisibility(View.VISIBLE);
		}
	}
//	protected Point getMyLocation() {
//		double lat = location.getLatitude();
//		double lon = location.getLongitude();
//		Point myLocation = MITMapView.toWebmercator(lat,lon);
//		return myLocation;
//	}	

}