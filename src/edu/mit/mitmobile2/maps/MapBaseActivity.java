package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.esri.android.map.event.OnZoomListener;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;

import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.MapUpdater;

public abstract class MapBaseActivity extends NewModuleActivity {
	
	private static final String TAG = "MapBaseActivity";
	public static final String KEY_VIEW_PINS = "view_pins";
	public MITMapView2 map;
    private FullScreenLoader mLoadingView;
	protected String module;
	Context mContext;
	Location location;
	GraphicsLayer gl;
	//GraphicsLayer graphicsLayer;
	Bundle extras;
	private MapUpdater mapUpdater;
	private HashMap params;
	
	protected ListView mListView;
	ProgressDialog progress;

	protected static final String MAP_ITEMS_KEY = "map_items";
	public static final String MAP_DATA_KEY = "map_data";	
	public static final String MAP_UPDATER_KEY = "map_updater";
	public static final String MAP_UPDATER_PARAMS_KEY = "map_updater_params";
	
	private static final double INIT_RESOLUTION = 1.205;
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
		mapInit();

		map.setOnZoomListener(new MyOnZoomListener());
		map.setOnSingleTapListener(new MyOnSingleTapListener());
				
		//Retrieve the non-configuration instance data that was previously returned. 
		Object init = getLastNonConfigurationInstance();
		if (init != null) {
			map.restoreState((String) init);
		}
		
	}
	
	
	final class MyOnZoomListener implements OnZoomListener {

		private static final long serialVersionUID = 1L;

		@Override
		public void postAction(float pivotX, float pivotY, double factor) {
			Log.d(TAG,"zoom x:" + pivotX + " y:" + pivotY);
			Log.d(TAG,"scale = " + map.getScale());
			Log.d(TAG,"resolution = " + map.getResolution());
			// TODO Auto-generated method stub
			
		}

		@Override
		public void preAction(float pivotX, float pivotY, double factor) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	final class MyOnSingleTapListener implements OnSingleTapListener {

		private static final long serialVersionUID = 1L;

		@Override
		public void onSingleTap(float x, float y) {

			Callout callout = map.getCallout(); 

			if (!map.isLoaded()) {
				return;
			}
			
    		GraphicsLayer gl = (GraphicsLayer)map.getMapLayer(MITMapView2.DEFAULT_GRAPHICS_LAYER);
    		int[] graphicId = gl.getGraphicIDs(x, y, 10);
    		
    		if (graphicId.length > 0) {
    			for (int i = 0; i < graphicId.length; i++) {
	    			Graphic g = gl.getGraphic(graphicId[i]);
	    			
	    			// get the index of the mapItem from the GraphicIdMap
	    			Integer mapItemIndex = map.getGraphicIdMap().get(Integer.toString(g.getUid()));
	    			
	    			// get the mapItem
	    			MapItem mapItem = map.getMapData().getMapItems().get(mapItemIndex);
	    			
	    			// Display the Callout if it is defined
	    			if (mapItem.getCallout(mContext) != null) {
	    				map.displayCallout(mContext, mapItem);
	    				return; // quit after the first callout is displayed
	    			}
    			}
    		}
    		else {
    			callout.hide();
    		}
    		
		}
		
	}

	final class MyCallbackListener implements CallbackListener {

		@Override
		public void onCallback(Object objs) {
			// TODO Auto-generated method stub
			if (objs != null)  {
				FeatureSet featureSet = (FeatureSet)objs;
	
				if (featureSet.getObjectIds() != null) {
					Integer objectIds[] = featureSet.getObjectIds();
					for (int i = 0; i < objectIds.length; i++) {
						Log.d(TAG,"objectId " + i + ": " + objectIds[i]);
					}
				}
				Graphic graphics[] = featureSet.getGraphics();
				for (int i = 0; i < graphics.length; i++) {
					Graphic graphic = graphics[i];
					Geometry geometry = graphic.getGeometry();
					Log.d(TAG,"geometry type = " + geometry.getType());
				}
			}
		}

		@Override
		public void onError(Throwable e) {
			// TODO Auto-generated method stub
			Log.d(TAG,"error: " + e.getMessage());
		}
		
	}
	
	private Handler mapUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mLoadingView.setVisibility(View.GONE);

            if (msg.arg1 == MobileWebApi.SUCCESS) {
            	Log.d(TAG,"MobileWebApi success");
                @SuppressWarnings("unchecked")
                MapServerData mapServerData = (MapServerData)msg.obj;
                // get the layers for the default group
                String defaultBasemap = mapServerData.getDefaultBasemap();
                
                // add the base layers to the map
                ArrayList<MapBaseLayer> baseMaps = mapServerData.getBaseLayerGroup().get(defaultBasemap);
                for (int i = 0; i < baseMaps.size(); i++) {
                	MapLayer layer = baseMaps.get(i);
                	ArcGISTiledMapServiceLayer serviceLayer = new ArcGISTiledMapServiceLayer(layer.getUrl());
            		map.addMapLayer(serviceLayer, layer.getLayerIdentifier());
                }
                
                // Add general graphics layer
                gl  = new GraphicsLayer();
        		map.addMapLayer(gl, MITMapView2.DEFAULT_GRAPHICS_LAYER);
                
        		// Define a listener that responds to location updates
        		LocationListener locationListener = new LocationListener() {
        		    public void onLocationChanged(Location location) {
        		      // Called when a new location is found by the network location provider.
        		      makeUseOfNewLocation(location);
        		    }

        		    private void makeUseOfNewLocation(Location location) {
        				// TODO Auto-generated method stub
        				if (location != null) {
	        		    	//Log.d(TAG,"lat = " + location.getLatitude());
	        				//Log.d(TAG,"lon = " + location.getLongitude());
        				}
        		    }

        			@Override
					public void onStatusChanged(String provider, int status, Bundle extras) {}

        		    @Override
					public void onProviderEnabled(String provider) {}

        		    @Override
					public void onProviderDisabled(String provider) {}
        		};

        		// Initialize location service
        		map.ls = map.getLocationService();
        		map.ls.setLocationListener(locationListener);
        		map.ls.setAllowNetworkLocation(true);
        		map.ls.start();

        		// zoom and center 
        		
        		processExtras();
        		
        		// if there are no map items, zoom and center to the init resolution
          		if (map.ls.isStarted()) {
        			if (map.getMapData() == null || map.getMapData().getMapItems().isEmpty()) {
            			map.zoomToResolution(map.ls.getPoint(), MapBaseActivity.INIT_RESOLUTION);
        			}
        		}
        		
        		
        		onMapLoaded();
       		        		
            } else if (msg.arg1 == MobileWebApi.ERROR) {
                mLoadingView.showError();
            } else if (msg.arg1 == MobileWebApi.CANCELLED) {
                mLoadingView.showError();
            }
        }
    };
	
    
    private Handler mapUpdateUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.arg1 == MobileWebApi.SUCCESS) {
            	try {
            		map.setMapData((MapData)msg.obj);
            		processMapData();
            	}
            	catch (Exception e) {
            		
            	}
            }
            else if (msg.arg1 == MobileWebApi.ERROR) {

            } 
            else if (msg.arg1 == MobileWebApi.CANCELLED) {

            }
        }
    };
    
    private void mapInit() {
    	Log.d(TAG,"mapInit");

    	if (map == null) {
    		    		
	    	// Retrieve the map and initial extent from XML layout
			map = (MITMapView2)findViewById(getMapViewID());

	       	if (map.getLayerIdMap() == null) {
	    		map.setLayerIdMap(new HashMap<String, Long>());
	    	}
	       	
	       	if (map.getGraphicIdMap() == null) {
	       		map.setGraphicIdMap(new HashMap<String, Integer>());
	       	}
	       	
	        mLoadingView = (FullScreenLoader) findViewById(getMapLoadingViewID());
	        
			mLoadingView.setVisibility(View.VISIBLE);
	        mLoadingView.showLoading();
	        MapModel.fetchMapServerData(this, mapUiHandler);    	
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
	
		
	protected Point getMyLocation() {
		double lat = location.getLatitude();
		double lon = location.getLongitude();
		Point myLocation = MITMapView2.toWebmercator(lat,lon);
		return myLocation;
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
	
	// debug layerIdMap
	public void debugLayerIdMap(String tag) {
//		Layer[] layers = map.getLayers();
//		for (int i = 0; i < layers.length; i++) {
//			Log.d(tag,"map layer " + i + " = " + layers[i].getID());
//		}
//	   	if (layerIdMap == null) {
//    		Log.d(TAG,"layerIdMap is null");
//    	}
//	   	else if (layerIdMap.isEmpty()) {
//	   		Log.d(TAG,"LayerIdMap is empty");
//    	}
//	   	else {
//	   		Iterator it = layerIdMap.entrySet().iterator();
//	   		while (it.hasNext()) {
//	   			Map.Entry pairs = (Map.Entry)it.next();
//	   			Log.d(TAG,"layerIdMap: " + pairs.getKey() + " = " + pairs.getValue());
//	   			long id = Long.parseLong(((Long)pairs.getValue()).toString());
//	   			Layer layer = map.getLayerByID(id);
//	   			if (layer == null) {
//	   				Log.d(tag,"layer " + id + " is null");
//	   			}
//	   			else {
//	   				Log.d(tag,"layer " + id + " is not null");
//	   			}
//
//	   			
//	   		}
//	   	}
	}
	

	 @Override
	 protected void onNewIntent(Intent intent) {
		 Log.d(TAG,"onNewIntent");
	    this.extras = intent.getExtras();
	    processExtras();
	 } // End of onN

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG,"onResume");
		map.unpause();
	}

	//This is the view that will get added to the callout
	//Create a text view and assign the text that should be visible in the callout		
	public View getView(int position, View convertView, ViewGroup parent) {
		String outputVal = null;
		TextView txtView;
		
		txtView = new TextView(this);
		txtView.setText(outputVal);
		txtView.setTextColor(Color.BLACK);
		txtView.setGravity(Gravity.CENTER_VERTICAL);
	
		return txtView;
	}

	public MapUpdater getMapUpdater() {
		return mapUpdater;
	}

	public void setMapUpdater(MapUpdater mapUpdater) {
		this.mapUpdater = mapUpdater;
	}
	
	protected void processExtras() {
		 if (extras!=null){ 
			 
	        	if (extras.containsKey(MAP_DATA_KEY)) {
	        		String mapDataJSON = (String)extras.get(MAP_DATA_KEY);
	        		map.setMapData(MapData.fromJSON(mapDataJSON));
	        		processMapData();
	        	}
	        	else if (extras.containsKey(MAP_UPDATER_KEY)) {
	        		Log.d("ZZZ","display map from updater");
	   			 	extras.setClassLoader(getClassLoader());
	    			try {
	    				@SuppressWarnings("unchecked")
	    				Class<Object> cls = (Class<Object>) Class.forName((String)extras.get(MapBaseActivity.MAP_UPDATER_KEY));
	    				try {
	    					Object o = cls.newInstance();
	    					mapUpdater = (MapUpdater)o;
	    					
	    					// Get the map updater params
	    					if (extras.containsKey(MAP_UPDATER_PARAMS_KEY)) {
	    						params = (HashMap<String,Object>)extras.get(MAP_UPDATER_PARAMS_KEY); 
	    					}
	    					mapUpdater.init(mContext,params,mapUpdateUiHandler);
	    				} catch (InstantiationException e) {
	    					Log.d(TAG,"InstantiationException");
	    					e.printStackTrace();
	    				} catch (IllegalAccessException e) {
	    					Log.d(TAG,"IllegalAccessException");
	    					e.printStackTrace();
	    				}
	    			} catch (ClassNotFoundException e1) {
	    				// TODO Auto-generated catch block
	    				Log.d(TAG,"ClassNotFoundException");
	    				Log.d(TAG,e1.getMessage());
	    				e1.printStackTrace();
	    			}
	        		
	        	}
		 }
	}
	
	protected void processMapData() {
		map.getCallout().hide();
		map.pause();

		Log.d(TAG,"processMapData");
		
		int gId = 0; // ID of graphic object created by displayMapItem

		// get Graphics Layer
		gl = (GraphicsLayer)map.getMapLayer(map.getMapData().getLayerName());
		Log.d(TAG,"test id of gl = " + gl.getID());
		
		// clear the layer if mode == MODE_OVERWRITE
		if (map.getMapData().getMode() == MapData.MODE_OVERWRITE) {
			gl.removeAll();	
		}
    	
    	for (int i = 0; i < map.getMapData().getMapItems().size(); i++) {
    		MapItem mapItem = map.getMapData().getMapItems().get(i);

    		// get the ID of the graphic once it has been added to the graphics layer
    		gId = map.dislayMapItem(map.getMapData().getLayerName(),mapItem);

    		// store the index (i) of the mapItem in the graphicIdMap with the key of the graphic ID
    		// this will let ut use the ID of the tapped graphic to get the corresponding mapItem and create the callout
    		map.graphicIdMap.put(Integer.toString(gId),Integer.valueOf(i));
    	}
    	
    	// Get the extent of the map item graphics and zoom to that extent
    	map.setExtent(map.getGraphicExtent(),MapBaseActivity.MAP_PADDING);
  
    	map.unpause();

    	// If there is only one mapItem, display the callout
    	if (map.getMapData().getMapItems().size() == 1) {
    		MapItem mapItem = map.getMapData().getMapItems().get(0);
    		map.displayCallout(mContext, mapItem);
    	}
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
	
	protected int getMapLoadingViewID() {
		return R.id.mapLoading;
	}
	
}