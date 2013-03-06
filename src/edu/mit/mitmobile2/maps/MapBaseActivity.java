package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.android.map.event.OnStatusChangedListener.STATUS;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.tasks.ags.geoprocessing.Geoprocessor;

import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.MapUpdater;
import edu.mit.mitmobile2.objs.SearchResults;

public abstract class MapBaseActivity extends NewModuleActivity {
	
	private static final String TAG = "MapBaseActivity";
	public static final String KEY_VIEW_PINS = "view_pins";
	public MITMapView2 map;
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
	ProgressDialog progress;

	protected static final String MAP_ITEMS_KEY = "map_items";
	public static final String MAP_DATA_KEY = "map_data";	
	public static final String MAP_ITEM_INDEX_KEY = "map_item_index";	
	public static final String MAP_UPDATER_KEY = "map_updater";
	public static final String MAP_UPDATER_PARAMS_KEY = "map_updater_params";
	
	public static final double INIT_RESOLUTION = 1.205;
	private static int MAP_PADDING = 100;
	static int INIT_ZOOM = 17; // DELETE ?
	static int INIT_ZOOM_ONE_ITEM = 18; // DELETE ?

	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"oncreate()");
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(getLayoutID());
        mLoadingView = (FullScreenLoader) findViewById(getMapLoadingViewID());

        this.extras = this.getIntent().getExtras();
		
        map = (MITMapView2)findViewById(getMapViewID());
        map.init(mContext);
				
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
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
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
	        MITMapsDataModel.executeSearch(query, map.mapSearchUiHandler, mContext); 
	        //doMySearch(query);
	    }
	    else if(extras.containsKey(MITMapView2.MAP_DATA_KEY)) {
			mapItems = (ArrayList)extras.getParcelableArrayList(MITMapView2.MAP_DATA_KEY);
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
	
//	protected Point getMyLocation() {
//		double lat = location.getLatitude();
//		double lon = location.getLongitude();
//		Point myLocation = MITMapView2.toWebmercator(lat,lon);
//		return myLocation;
//	}	

}