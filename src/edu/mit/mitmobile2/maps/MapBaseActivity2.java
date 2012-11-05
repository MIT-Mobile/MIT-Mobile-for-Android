package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.LocationService;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.ags.query.QueryTask;

import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.people.PeopleModule;
import edu.mit.mitmobile2.R;

public class MapBaseActivity2 extends NewModuleActivity {
	
	private static final String TAG = "MapBaseActivity2";
	public static final String KEY_VIEW_PINS = "view_pins";
	protected MapView map = null;
	LocationService ls;
	SpatialReference mercatorWeb; // spatial reference used by the base map
	SpatialReference wgs84; // spatial reference used by androids location service
    private FullScreenLoader mLoadingView;
	ArcGISTiledMapServiceLayer serviceLayer;
	private String provider;
	protected String module;
	protected List<MapItem> mMapItems;
	QueryTask queryTask;
	Query query;
	FeatureSet featureSet;
	Location location;
	Button querybt;
	EditText buildingQuery;
	GraphicsLayer gl;
	GraphicsLayer graphicsLayer;
	Graphic[] highlightGraphics;  
	GraphicsLayer locationLayer; // used to show the pin for for the current location
	protected static Map<String, Long> layerIdMap;

	boolean blQuery = true;
	String buildingCriteria;
	ProgressDialog progress;

	protected static final String MAP_ITEMS_KEY = "map_items";

	final static int HAS_RESULTS = 1;
	final static int NO_RESULT = 2;
	final static int CLEAR_RESULT = 3;
	
	private static final int WEST_LONGITUDE_E6  = -71132698;
	private static final int EAST_LONGITUDE_E6  = -71006698;
	private static final int NORTH_LATITUDE_E6  =  42407741;
	private static final int SOUTH_LATITUDE_E6  =  42331392;
		
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"oncreate()");
		super.onCreate(savedInstanceState);
		
		onNewIntent(getIntent());
    		
		setContentView(R.layout.maps2);
        mLoadingView = (FullScreenLoader) findViewById(R.id.mapLoading);

		mapInit();

		// Acquire a reference to the system Location Manager
		//LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		      makeUseOfNewLocation(location);
		    }

		    private void makeUseOfNewLocation(Location location) {
				// TODO Auto-generated method stub
				Log.d(TAG,"lat = " + location.getLatitude());
				Log.d(TAG,"lon = " + location.getLongitude());
		    }

			public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		};
		
	
		// Initialize location service
		ls = map.getLocationService();
		ls.setLocationListener(locationListener);
		ls.setAllowNetworkLocation(true);
		
		// Register the listener with the Location Manager to receive location updates
		Criteria criteria = new Criteria();
		//provider = locationManager.getBestProvider(criteria, false);
		//locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
		//locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

		//location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		//Log.d(TAG,"lat = " + location.getLatitude());
		//Log.d(TAG,"lon = " + location.getLongitude());
		
		//Retrieve the non-configuration instance data that was previously returned. 
		Object init = getLastNonConfigurationInstance();
		if (init != null) {
			map.restoreState((String) init);
		}	
		
	}
	
	/**
	 * 
	 * Query Task executes asynchronously.
	 * 
	 */
	private class AsyncQueryTask extends AsyncTask<String, Void, FeatureSet> {

		protected void onPreExecute() {
			progress = ProgressDialog.show(MapBaseActivity2.this, "",
					"Please wait....query task is executing");

		}

		/**
		 * First member in parameter array is the query URL; second member is
		 * the where clause.
		 */
		protected FeatureSet doInBackground(String... queryParams) {
			Log.d(TAG,"doInBackground()");			

			try {
				featureSet = queryTask.execute(query);
				//ArcGISFeatureLayer featureLayer = (ArcGISFeatureLayer) map.getLayer(0);
				//featureLayer.queryFeatures(query, new  MyCallbackListener());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return featureSet;
			}
			return featureSet;

		}

		protected void onPostExecute(FeatureSet result) {
			String message = "";
			if (result != null) {

				if (result.getGraphics() != null) {
					
					// clear graphics layer
					graphicsLayer.removeAll();
					
					Graphic graphics[] = result.getGraphics();
					message = graphics.length + " result(s) found";
					Log.d(TAG,"num graphics = " + graphics.length);
					highlightGraphics = new Graphic[graphics.length];
					for (int i = 0; i < graphics.length; i++) {
						Graphic graphic = graphics[i];
						Geometry geometry = graphic.getGeometry();
						Log.d(TAG,"geometry type = " + geometry.getType());
						/////////////////////////
		                Random r = new Random();
		                int color = Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255));

		                /*
		                 * Create appropriate symbol, based on geometry type
		                 */
		                if (geometry.getType().name().equalsIgnoreCase("point")) {
		                  SimpleMarkerSymbol sms = new SimpleMarkerSymbol(color, 20, STYLE.SQUARE);
		                  highlightGraphics[i] = new Graphic(geometry, sms);
		                } else if (geometry.getType().name().equalsIgnoreCase("polyline")) {
		                  SimpleLineSymbol sls = new SimpleLineSymbol(color, 5);
		                  highlightGraphics[i] = new Graphic(geometry, sls);
		                } else if (geometry.getType().name().equalsIgnoreCase("polygon")) {
		                  SimpleFillSymbol sfs = new SimpleFillSymbol(color);
		                  sfs.setAlpha(75);
		                  highlightGraphics[i] = new Graphic(geometry, sfs);
		                }

		                
		                /**
		                 * set the Graphic's geometry, add it to GraphicLayer and refresh the Graphic Layer
		                 */
		                graphicsLayer.addGraphic(highlightGraphics[i]);
		                Polygon polygon = graphicsLayer.getExtent();
		                Point point = polygon.getPoint(0);
		        		point.setX(point.getX()*1000000);
		        		point.setY(point.getY()*1000000);
		                //map.centerAt(point,true);
					}
				}

				
			}
			progress.dismiss();

			Toast toast = Toast.makeText(MapBaseActivity2.this, message,
					Toast.LENGTH_LONG);
			toast.show();
			//blQuery = false;

		}

	}
	protected void onPause() {
		super.onPause();
		map.pause();
 }

	protected void onResume() {
		super.onResume(); 
		map.unpause();
	}	
	
	final class MyOnZoomListener implements OnZoomListener {

		@Override
		public void postAction(float pivotX, float pivotY, double factor) {
			Log.d(TAG,"zoom x:" + pivotX + " y:" + pivotY);
			// TODO Auto-generated method stub
			
		}

		@Override
		public void preAction(float pivotX, float pivotY, double factor) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	final class MyOnSingleTapListener implements OnSingleTapListener {

		@Override
		public void onSingleTap(float x, float y) {
			// TODO Auto-generated method stub
			Log.d(TAG,"tap x:" + x + " y:" + y);
			Point point = new Point();
			point.setX(x);
			point.setY(y);
			//map.centerAt(point, true);
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
                for (int i = 0; i < mapServerData.getBaseMaps().size(); i++) {
                	MapLayer layer = (MapLayer)mapServerData.getBaseMaps().get(i);
            		serviceLayer = new ArcGISTiledMapServiceLayer(layer.getUrl());
            		addMapLayer(serviceLayer, layer.getLayerIdentifier());
                }
                
                // Add general graphics layer
                graphicsLayer = new GraphicsLayer();
        		addMapLayer(graphicsLayer, "layer_graphics");
                
                // Add graphics layer to show the current location
                //locationLayer = new GraphicsLayer();
                //locationLayer.setVisible(false);
        		//addMapLayer(locationLayer, "layer_location");

        		// zoom to extents of Campus map
        		Layer layer = getMapLayer("edu.mit.mobile.map.Base");
        		if (layer != null) {
        			Log.d(TAG,"extent = " + layer.getExtent());
        			Log.d(TAG,"full extent = " + layer.getFullExtent());        			
        			//Point point = polygon..getCenter();
        			//map.centerAt(point, true);
        		}        		
        		// add location marker
        		//addPicture("layer_location", R.drawable.ic_maps_indicator_current_position_anim1, getMyLocation());
        		
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
			map = (MapView)findViewById(R.id.map);
	
	       	if (layerIdMap == null) {
	    		layerIdMap = new HashMap<String, Long>();
	    	}
	       	
	    	mercatorWeb = SpatialReference.create(102100);
	    	wgs84 = SpatialReference.create(4326);	
	
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
	
	
	protected Point toWebmercator(double lat, double lon) {
		Point point = new Point();
		point = (Point)GeometryEngine.project(new Point(lon,lat), wgs84,mercatorWeb);
		return point;
	}
		
	/*
	 * addMapLayer adds a layer to the ArcGIS map object
	 * creates an entry in the layerIdMap hashmap where the key is a string and the value is the layer id
	 * this makes it possible to access the layers by calling getLayerByID
	 */
	protected void addMapLayer(Layer layer, String layerName) {
		Long id = layer.getID();
		Log.d(TAG,"layer id from addMapLayer = " + id);
		layerIdMap.put(layerName, id);                	                	                   
        map.addLayer(layer);
	}
	
	protected Layer getMapLayer(String layerName) {
		if (layerIdMap != null) {
			Long id = layerIdMap.get(layerName);                	                	                   
			return map.getLayerByID(id);
		}
		else {
			return null;
		}
	}
	
	protected int addPicture(String layerName, int pictureResource, Point point) {
		Bitmap libImage = BitmapFactory.decodeResource(getResources(), pictureResource);
		BitmapDrawable libDrawable = new BitmapDrawable(libImage);
        PictureMarkerSymbol pms = new PictureMarkerSymbol(libDrawable);
       
        Graphic g = new Graphic(point, pms);
        Long layerId = layerIdMap.get("layer_location");
        gl = (GraphicsLayer)map.getLayerByID(layerId);
        gl.addGraphic(g);
        return g.getUid();
	}

	protected int dislayMapItem(MapItem mapItem) {
		Log.d(TAG,"displayMapItem");
		debugLayerIdMap(TAG);
		Bitmap libImage = BitmapFactory.decodeResource(getResources(), R.drawable.map_red_pin);
		BitmapDrawable libDrawable = new BitmapDrawable(libImage);
		PictureMarkerSymbol pms = new PictureMarkerSymbol(libDrawable);       
		Graphic g = new Graphic(toWebmercator(mapItem.lat_wgs84,mapItem.long_wgs84), pms);
        gl = (GraphicsLayer)getMapLayer("layer_graphics");
        gl.addGraphic(g);
        return g.getUid();	
	}
	
	protected Point getMyLocation() {
		double lat = location.getLatitude();
		double lon = location.getLongitude();
		Point myLocation = toWebmercator(lat,lon);
		return myLocation;
	}
	
	/*
	 * launches a new map activity with pins already set
	 */
	public static void launchNewMapItems(Context context, List<MapItem> mapItems) {
		Log.d(TAG,"launchNewMapItems");
		Intent i = new Intent(context, MITMapActivity2.class); 
		i.putParcelableArrayListExtra(MAP_ITEMS_KEY, new ArrayList<MapItem>(mapItems));
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
		Layer[] layers = map.getLayers();
		for (int i = 0; i < layers.length; i++) {
			Log.d(tag,"map layer " + i + " = " + layers[i].getID());
		}
	   	if (layerIdMap == null) {
    		Log.d(TAG,"layerIdMap is null");
    	}
	   	else if (layerIdMap.isEmpty()) {
	   		Log.d(TAG,"LayerIdMap is empty");
    	}
	   	else {
	   		Iterator it = layerIdMap.entrySet().iterator();
	   		while (it.hasNext()) {
	   			Map.Entry pairs = (Map.Entry)it.next();
	   			Log.d(TAG,"layerIdMap: " + pairs.getKey() + " = " + pairs.getValue());
	   			long id = Long.parseLong(((Long)pairs.getValue()).toString());
	   			Layer layer = map.getLayerByID(id);
	   			if (layer == null) {
	   				Log.d(tag,"layer " + id + " is null");
	   			}
	   			else {
	   				Log.d(tag,"layer " + id + " is not null");
	   			}

	   			
	   		}
	   	}
	}
	
	public void onNewIntent(Intent todo) {
		Log.d(TAG,"onNewIntent");
	}

	
}
