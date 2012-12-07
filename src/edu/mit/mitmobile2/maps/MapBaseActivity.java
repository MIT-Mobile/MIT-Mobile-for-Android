package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationService;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.ags.query.QueryTask;

import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.MapItem;

public class MapBaseActivity extends NewModuleActivity {
	
	private static final String TAG = "MapBaseActivity";
	public static final String KEY_VIEW_PINS = "view_pins";
	public MITMapView2 map;
	LocationService ls;
    private FullScreenLoader mLoadingView;
	ArcGISTiledMapServiceLayer serviceLayer;
	protected String module;
	//protected List<MapItem> mMapItems;
	Context mContext;
	QueryTask queryTask;
	Query query;
	FeatureSet featureSet;
	Location location;
	Button querybt;
	EditText buildingQuery;
	GraphicsLayer graphicsLayer;
	Bundle extras;
	public static String DEFAULT_GRAPHICS_LAYER = "LAYER_GRAPHICS";
	public static int DEFAULT_PIN = R.drawable.map_red_pin;
	PictureMarkerSymbol pms;
	
	protected ListView mListView;
	ProgressDialog progress;

	protected static final String MAP_ITEMS_KEY = "map_items";
	public static final String MAP_DATA_KEY = "map_data";

	final static int HAS_RESULTS = 1;
	final static int NO_RESULT = 2;
	final static int CLEAR_RESULT = 3;
	
	private static final double INIT_SCALE = 10000;
	
	static int INIT_ZOOM = 17; // DELETE ?
	static int INIT_ZOOM_ONE_ITEM = 18; // DELETE ?

	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"oncreate()");
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.maps);
        mLoadingView = (FullScreenLoader) findViewById(R.id.mapLoading);

        this.extras = this.getIntent().getExtras();
		mapInit();

		map.setOnZoomListener(new MyOnZoomListener());
		map.setOnSingleTapListener(new MyOnSingleTapListener());
		
		Criteria criteria = new Criteria();
		
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
			
    		GraphicsLayer gl = (GraphicsLayer)map.getMapLayer(MapBaseActivity.DEFAULT_GRAPHICS_LAYER);
    		int[] graphicId = gl.getGraphicIDs(x, y, 10,1);
    		
    		Log.d(TAG,"num graphics = " + graphicId.length);
    		if (graphicId.length > 0) {
    			Graphic g = gl.getGraphic(graphicId[0]);
    			
    			// get the index of the mapItem from the GraphicIdMap
    			Integer mapItemIndex = map.getGraphicIdMap().get(Integer.toString(g.getUid()));
    			
    			// get the mapItem
    			MapItem mapItem = map.getMapData().getMapItems().get(mapItemIndex);
    			
    			// Display the Callout
 	    		map.displayCallout(mContext, mapItem);

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
			Log.d(TAG,"onCallback()");
			if (objs != null)  {
				Log.d(TAG,"class = " + objs.getClass());
				FeatureSet featureSet = (FeatureSet)objs;
				Log.d(TAG,featureSet.toString());
	
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
	
	private Handler uiHandler = new Handler() {
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
            		serviceLayer = new ArcGISTiledMapServiceLayer(layer.getUrl());
            		map.addMapLayer(serviceLayer, layer.getLayerIdentifier());
                }
                
                // Add general graphics layer
                graphicsLayer  = new GraphicsLayer();
        		map.addMapLayer(graphicsLayer, MapBaseActivity.DEFAULT_GRAPHICS_LAYER);
                
        		// Define a listener that responds to location updates
        		LocationListener locationListener = new LocationListener() {
        		    public void onLocationChanged(Location location) {
        		      // Called when a new location is found by the network location provider.
        		      makeUseOfNewLocation(location);
        		    }

        		    private void makeUseOfNewLocation(Location location) {
        				// TODO Auto-generated method stub
        				if (location != null) {
	        		    	Log.d(TAG,"lat = " + location.getLatitude());
	        				Log.d(TAG,"lon = " + location.getLongitude());
        				}
        		    }

        			public void onStatusChanged(String provider, int status, Bundle extras) {}

        		    public void onProviderEnabled(String provider) {}

        		    public void onProviderDisabled(String provider) {}
        		};

        		// Initialize location service
        		ls = map.getLocationService();
        		ls.setLocationListener(locationListener);
        		ls.setAllowNetworkLocation(true);
        		ls.start();

        		// zoom and center 
        		if (ls.isStarted()) {
        			map.zoomToScale(ls.getPoint(), MapBaseActivity.INIT_SCALE);
        		}
        		
        		 if (extras!=null){ 
        			 extras.setClassLoader(getClassLoader());
        			 
        	        	if (extras.containsKey(MAP_DATA_KEY)) {
        	        		String mapDataJSON = (String)extras.get(MAP_DATA_KEY);
        	        		map.setMapData(MapData.fromJSON(mapDataJSON));
        	        		map.processMapData();
        	        	}
        		 }
        		        		
            } else if (msg.arg1 == MobileWebApi.ERROR) {
                mLoadingView.showError();
            } else if (msg.arg1 == MobileWebApi.CANCELLED) {
                mLoadingView.showError();
            }
        }
    };
	
    private void mapInit() {
    	Log.d(TAG,"mapInit");

    	if (map == null) {
    		    		
	    	// Retrieve the map and initial extent from XML layout
			map = (MITMapView2)findViewById(R.id.map);

	       	if (map.getLayerIdMap() == null) {
	    		map.setLayerIdMap(new HashMap<String, Long>());
	    	}
	       	
	       	if (map.getGraphicIdMap() == null) {
	       		map.setGraphicIdMap(new HashMap<String, Integer>());
	       	}
	       	
	        mLoadingView = (FullScreenLoader) findViewById(R.id.mapLoading);
	        
			mLoadingView.setVisibility(View.VISIBLE);
	        mLoadingView.showLoading();
	        MapModel.fetchMapServerData(this, uiHandler);    	
    	}
    }

	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return null;
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
	    Bundle extras = intent.getExtras();
	    if (extras!=null){ 
        	if (extras.containsKey(MAP_DATA_KEY)) {
          		String mapDataJSON = (String)extras.get(MAP_DATA_KEY);
        		map.setMapData(MapData.fromJSON(mapDataJSON));
        		//mapData = extras.getParcelable(MAP_DATA_KEY);
        		map.processMapData();
        	}
	    }
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
		 
}