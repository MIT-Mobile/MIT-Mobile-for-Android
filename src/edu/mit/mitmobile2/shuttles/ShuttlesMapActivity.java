package edu.mit.mitmobile2.shuttles;

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
import edu.mit.mitmobile2.maps.MITMapView2;
import edu.mit.mitmobile2.maps.MapsModule;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.MapUpdater;
import edu.mit.mitmobile2.objs.SearchResults;

public class ShuttlesMapActivity extends NewModuleActivity {
	
	private static final String TAG = "MapBaseActivity";
	public MITMapView2 map;
    private FullScreenLoader mLoadingView;
	protected String module;
	Context mContext;
	Location location;
	GraphicsLayer gl;
	//GraphicsLayer graphicsLayer;
	Bundle extras;
	ArrayList<MapItem> mapItems;
	private MapUpdater mapUpdater;
	private HashMap params;
	
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
        map.init(mContext,map);
		
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
	        String query = intent.getStringExtra(SearchManager.QUERY);
	        Log.d(TAG,"query = " + query);
	        //MITMapsDataModel.executeSearch(query, map.mapSearchUiHandler, mContext); 
	        //doMySearch(query);
	    }
	    else if(extras.containsKey(MITMapView2.MAP_DATA_KEY)) {
			mapItems = (ArrayList)extras.getParcelableArrayList(MITMapView2.MAP_DATA_KEY);
	    	map.addMapItems(mapItems);
	    	map.syncGraphicsLayers();
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

	protected int getMapLoadingViewID() {
		return R.id.mapLoading;
	}

    /* override this to handle the on map loaded event */
	protected void onMapLoaded() { }

	@Override
	protected NewModule getNewModule() {
		return new ShuttlesModule();
	}
	
}