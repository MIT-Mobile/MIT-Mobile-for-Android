package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.LocationService;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
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
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.MapPoint;

public class MapBaseActivity extends NewModuleActivity {
	
	private static final String TAG = "MapBaseActivity";
	public static final String KEY_VIEW_PINS = "view_pins";
	public MapView map;
	LocationService ls;
	SpatialReference mercatorWeb; // spatial reference used by the base map
	SpatialReference wgs84; // spatial reference used by androids location service
    private FullScreenLoader mLoadingView;
	ArcGISTiledMapServiceLayer serviceLayer;
	private String provider;
	protected String module;
	protected List<MapItem> mMapItems;
	Context mContext;
	QueryTask queryTask;
	Query query;
	FeatureSet featureSet;
	Location location;
	Button querybt;
	EditText buildingQuery;
	GraphicsLayer gl;
	GraphicsLayer graphicsLayer;
	Bundle extras;
	//Graphic[] highlightGraphics;  
	//GraphicsLayer locationLayer; // used to show the pin for for the current location
	public static String DEFAULT_GRAPHICS_LAYER = "LAYER_GRAPHICS";
	public static int DEFAULT_PIN = R.drawable.map_red_pin;
	PictureMarkerSymbol pms;
	
	protected static Map<String, Long> layerIdMap;
	private MapData mapData;
	protected ListView mListView;
	
	//boolean blQuery = true;
	//String buildingCriteria;
	ProgressDialog progress;

	protected static final String MAP_ITEMS_KEY = "map_items";
	public static final String MAP_DATA_KEY = "map_data";

	final static int HAS_RESULTS = 1;
	final static int NO_RESULT = 2;
	final static int CLEAR_RESULT = 3;
	
	private static final double INIT_SCALE = 10000;
	private static final double WEST_LONGITUDE_E6  = -71.132698;
	private static final double EAST_LONGITUDE_E6  = -71.006698;
	private static final double NORTH_LATITUDE_E6  =  42.407741;
	private static final double SOUTH_LATITUDE_E6  =  42.331392;
	
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
		
		// Acquire a reference to the system Location Manager
		//LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
				
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
			progress = ProgressDialog.show(MapBaseActivity.this, "",
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

//		protected void onPostExecute(FeatureSet result) {
//			String message = "";
//			if (result != null) {
//
//				if (result.getGraphics() != null) {
//					
//					// clear graphics layer
//					graphicsLayer.removeAll();
//					
//					Graphic graphics[] = result.getGraphics();
//					message = graphics.length + " result(s) found";
//					Log.d(TAG,"num graphics = " + graphics.length);
//					highlightGraphics = new Graphic[graphics.length];
//					for (int i = 0; i < graphics.length; i++) {
//						Graphic graphic = graphics[i];
//						Geometry geometry = graphic.getGeometry();
//						Log.d(TAG,"geometry type = " + geometry.getType());
//						/////////////////////////
//		                Random r = new Random();
//		                int color = Color.rgb(r.nextInt(255), r.nextInt(255), r.nextInt(255));
//
//		                /*
//		                 * Create appropriate symbol, based on geometry type
//		                 */
//		                if (geometry.getType().name().equalsIgnoreCase("point")) {
//		                  SimpleMarkerSymbol sms = new SimpleMarkerSymbol(color, 20, STYLE.SQUARE);
//		                  highlightGraphics[i] = new Graphic(geometry, sms);
//		                } else if (geometry.getType().name().equalsIgnoreCase("polyline")) {
//		                  SimpleLineSymbol sls = new SimpleLineSymbol(color, 5);
//		                  highlightGraphics[i] = new Graphic(geometry, sls);
//		                } else if (geometry.getType().name().equalsIgnoreCase("polygon")) {
//		                  SimpleFillSymbol sfs = new SimpleFillSymbol(color);
//		                  sfs.setAlpha(75);
//		                  highlightGraphics[i] = new Graphic(geometry, sfs);
//		                }
//
//		                
//		                /**
//		                 * set the Graphic's geometry, add it to GraphicLayer and refresh the Graphic Layer
//		                 */
//		                graphicsLayer.addGraphic(highlightGraphics[i]);
//		                Polygon polygon = graphicsLayer.getExtent();
//		                Point point = polygon.getPoint(0);
//		        		point.setX(point.getX()*1000000);
//		        		point.setY(point.getY()*1000000);
//		                //map.centerAt(point,true);
//					}
//				}
//
//				
//			}
//			progress.dismiss();
//
//			Toast toast = Toast.makeText(MapBaseActivity.this, message,
//					Toast.LENGTH_LONG);
//			toast.show();
//			//blQuery = false;
//
//		}

	}
	protected void onPause() {
		super.onPause();
		map.pause();
 }

	
	final class MyOnZoomListener implements OnZoomListener {

		/**
		 * 
		 */
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

		@Override
		public void onSingleTap(float x, float y) {

			Callout callout = map.getCallout(); 

			if (!map.isLoaded()) {
				return;
			}
			// TODO Auto-generated method stub
			Log.d(TAG,"tap x:" + x + " y:" + y);
			Point point = map.toMapPoint(new Point(x,y));
			Log.d(TAG,"map x:" + point.getX() + " y:" + point.getY());
			
    		gl = (GraphicsLayer)getMapLayer(MapBaseActivity.DEFAULT_GRAPHICS_LAYER);
    		int[] graphicId = gl.getGraphicIDs(x, y, 10,1);
    		
    		if (graphicId.length > 0) {
    			Graphic g = gl.getGraphic(graphicId[0]);
    			displayCallout(g);
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
            		addMapLayer(serviceLayer, layer.getLayerIdentifier());
            		//map.addLayer(serviceLayer);
                }
                
                // Add general graphics layer
                graphicsLayer  = new GraphicsLayer();
        		addMapLayer(graphicsLayer, MapBaseActivity.DEFAULT_GRAPHICS_LAYER);
                
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
        		//gl = (GraphicsLayer)getMapLayer(MapBaseActivity.DEFAULT_GRAPHICS_LAYER);
        		Point upperleft = toWebmercator(NORTH_LATITUDE_E6,WEST_LONGITUDE_E6);
        		Point lowerleft = toWebmercator(SOUTH_LATITUDE_E6,WEST_LONGITUDE_E6);
        		Point upperright = toWebmercator(NORTH_LATITUDE_E6,EAST_LONGITUDE_E6);
        		Point lowerright = toWebmercator(SOUTH_LATITUDE_E6,EAST_LONGITUDE_E6);
        		Log.d(TAG,"upperleft x=" + upperleft.getX() + " y=" + upperleft.getY());
        		Log.d(TAG,"lowerleft x=" + lowerleft.getX() + " y=" + lowerleft.getY());
        		Log.d(TAG,"upperright x=" + upperright.getX() + " y=" + upperright.getY());
        		Log.d(TAG,"lowerright x=" + lowerright.getX() + " y=" + lowerright.getY());
        		
        		//SimpleMarkerSymbol sms = new SimpleMarkerSymbol(Color.RED, 20, STYLE.CROSS);
				//Graphic graphic = new Graphic(point,sms);
				//gl.addGraphic(graphic);
    			//map.centerAt(point, true);

        		//        		Layer layer = getMapLayer("edu.mit.mobile.map.Base");
//        		if (layer != null) {
//        			Polygon polygon = layer.getExtent();
//        			Point point = polygon.
//        			int points = polygon.getPointCount();
//        			for (int p = 0; p < points; p++) {
//        				Point point = polygon.getPoint(p);
//        				SimpleMarkerSymbol sms = new SimpleMarkerSymbol(Color.RED, 20, STYLE.CROSS);
//        				Graphic graphic = new Graphic(point,sms);
//        				gl.addGraphic(graphic);
//        			}
//        			//Poinpoint = polygon..getCenter();
//        			//map.centerAt(point, true);
//        		}        		
        		// add location marker
        		//addPicture("layer_location", R.drawable.ic_maps_indicator_current_position_anim1, getMyLocation());
        		
        		processMapData(extras);

        		
        		
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
			Log.d(TAG,"get map layer id = " + id);
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

	protected int dislayMapItem(String layerName, MapItem mapItem) {
		int gId = 0;
		Log.d(TAG,"displayMapItem");
		//Log.d(TAG,"mapItem:lat = " + mapItem.lat_wgs84 + "");
		//Log.d(TAG,"mapItem:long = " + mapItem.long_wgs84 + "");
		Log.d(TAG,"mapItem:displayName = " + mapItem.displayName + "");
		Log.d(TAG,"mapItem:bldgnum = " + mapItem.bldgnum + "");
		Log.d(TAG,"mapItem:name = " + mapItem.name + "");
		Log.d(TAG,"mapItem:snippets = " + mapItem.snippets + "");
	
		
		switch (mapItem.geometryType) {
			case MapItem.TYPE_POINT:
				gId = displayMapPoint(mapItem);
			break;
			
			case MapItem.TYPE_POLYLINE:
				gId = displayMapPolyline(mapItem);
			break;
			
			case MapItem.TYPE_POLYGON:
			break;
			
			default:
				gId = 0;
			break;
			
		}

		return gId;

	}
	
	protected int displayMapPoint(MapItem mapItem) {
		if (mapItem.mapPoints.size() > 0) {
			MapPoint mapPoint = mapItem.mapPoints.get(0);
			Point point = toWebmercator(mapPoint.lat_wgs84,mapPoint.long_wgs84);

			Bitmap libImage = BitmapFactory.decodeResource(getResources(), MapBaseActivity.DEFAULT_PIN);
			BitmapDrawable libDrawable = new BitmapDrawable(libImage);
			PictureMarkerSymbol pms = new PictureMarkerSymbol(libDrawable);       

			Map attributes = new HashMap();
			attributes.put("displayName", mapItem.displayName);
			attributes.put("bldgnum", mapItem.bldgnum);
			attributes.put("name", mapItem.name);
			attributes.put("snippets", mapItem.snippets);
			attributes.put("pointX", point.getX() + "");
			attributes.put("pointY", point.getY() + "");
			
			Graphic g = new Graphic(point, pms,attributes, null);

			gl = (GraphicsLayer)getMapLayer(MapBaseActivity.DEFAULT_GRAPHICS_LAYER); // this should be a parameter
			gl.addGraphic(g);
	        return g.getUid();	
			
		}
		else {
			return 0;
		}
	}
	
	protected int displayMapPolyline(MapItem mapItem) {
		Log.d(TAG,"displayMapPolyline");
		Point point;
		Point startPoint;
		Polyline polyline = new Polyline();
	   
		if (mapItem.mapPoints.size() > 0) {
			
			startPoint = toWebmercator(mapItem.mapPoints.get(0).lat_wgs84,mapItem.mapPoints.get(0).long_wgs84);
			polyline.startPath(startPoint);
			for (int p = 0; p < mapItem.mapPoints.size(); p++) {
				MapPoint mapPoint = mapItem.mapPoints.get(p);
				Log.d(TAG,"polyline point x:" + mapPoint.long_wgs84 + " point y:" + mapPoint.lat_wgs84);
				point = toWebmercator(mapPoint.lat_wgs84,mapPoint.long_wgs84);
				polyline.lineTo(point);
			}
			
			Graphic g = new Graphic(polyline,new SimpleLineSymbol(Color.RED,5));

			gl = (GraphicsLayer)getMapLayer(MapBaseActivity.DEFAULT_GRAPHICS_LAYER); // this should be a parameter
			gl.addGraphic(g);
	        return g.getUid();	
			
		}
		else {
			return 0;
		}
	}
	
	protected int displayMapPolygon(MapItem mapItem) {
		return 0;
	}

	protected void displayCallout(MapItem mapItem) {
		if (!map.isLoaded()) {
			return;
		}

		Callout callout = map.getCallout(); 
		gl = (GraphicsLayer)getMapLayer(MapBaseActivity.DEFAULT_GRAPHICS_LAYER);
		Point point = toWebmercator(mapItem.mapPoints.get(0).lat_wgs84,mapItem.mapPoints.get(0).long_wgs84);
   		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		LinearLayout calloutLayout = (LinearLayout) inflater.inflate(R.layout.map_callout, null);
		TextView calloutTitle = (TextView)calloutLayout.findViewById(R.id.callout_item_title);
		TextView calloutSnippet = (TextView)calloutLayout.findViewById(R.id.callout_item_snippet);
		String bldgnum = mapItem.bldgnum;
		String displayName = mapItem.displayName;
		
		if (bldgnum.trim().length() == 0) {
			calloutTitle.setVisibility(View.GONE);
		}
		else {
			calloutTitle.setText(bldgnum);
		}
		calloutSnippet.setText(displayName);

		callout.setContent(calloutLayout);
    	callout.setCoordinates(point);
    	callout.setStyle(R.xml.callout);
		callout.refresh();
		callout.show();
	}
	
	protected void displayCallout(Graphic g) {
		if (!map.isLoaded()) {
			return;
		}

		Callout callout = map.getCallout(); 
		gl = (GraphicsLayer)getMapLayer(MapBaseActivity.DEFAULT_GRAPHICS_LAYER);
		Point point = new Point();
		Double x = new Double((String)g.getAttributeValue("pointX"));
		Double y = new Double((String)g.getAttributeValue("pointY"));
		point.setX(x.doubleValue());
		point.setY(y.doubleValue());
			
		Log.d(TAG,"point tostring = " + point.toString());
		Log.d(TAG,"point x = " + point.getX());
   		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		LinearLayout calloutLayout = (LinearLayout) inflater.inflate(R.layout.map_callout, null);
		TextView calloutTitle = (TextView)calloutLayout.findViewById(R.id.callout_item_title);
		TextView calloutSnippet = (TextView)calloutLayout.findViewById(R.id.callout_item_snippet);
		String bldgnum = (String)g.getAttributeValue("bldgnum");
		String displayName = (String)g.getAttributeValue("displayName");
		
		if (bldgnum.trim().length() == 0) {
			calloutTitle.setVisibility(View.GONE);
		}
		else {
			calloutTitle.setText(bldgnum);
		}
		calloutSnippet.setText(displayName);

		callout.setContent(calloutLayout);
    	callout.setCoordinates(point);
    	callout.setStyle(R.xml.callout);
		callout.refresh();
		callout.show();
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
		Intent i = new Intent(context, MITMapActivity.class); 
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
	
	protected void processMapData(Bundle extras) {
		Log.d(TAG,"processMapData");
		
        if (extras!=null){ 
        	if (extras.containsKey(MAP_DATA_KEY)) {
        		Log.d(TAG,"extras contain map data");
        		
        		// get mapData
        		mapData = (MapData)extras.get(MAP_DATA_KEY);
        		
        		int gId = 0; // ID of graphic object created by displayMapItem

        		// get Graphics Layer
        		gl = (GraphicsLayer)getMapLayer(mapData.getLayerName());
        		Log.d(TAG,"test id of gl = " + gl.getID());
        		
        		// clear the layer if mode == MODE_OVERWRITE
        		if (mapData.getMode() == MapData.MODE_OVERWRITE) {
        			gl.removeAll();	
        		}
        		
        		ArrayList<MapItem> mapItems = mapData.getMapItems();
            	
	        	for (int i = 0; i < mapItems.size(); i++) {
	        		MapItem mapItem = mapItems.get(i);
	        		gId = dislayMapItem(mapData.getLayerName(),mapItem);
	        	}
	        	
	        	// If there is only one mapItem, display the callout
	        	if (mapItems.size() == 1) {
	        		displayCallout((MapItem)mapItems.get(0));
	        		
	        		// this should be replaced with logic to find the extent and center of all map items
	        		Point centerPt = toWebmercator(mapItems.get(0).mapPoints.get(0).lat_wgs84,mapItems.get(0).mapPoints.get(0).long_wgs84);
	        		map.centerAt(centerPt,false);

	        	}
        	}
        	
        }
	}

	 @Override
	 protected void onNewIntent(Intent intent) {
		 Log.d(TAG,"onNewIntent");
	    Bundle extras = intent.getExtras();
	    processMapData(extras);
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
