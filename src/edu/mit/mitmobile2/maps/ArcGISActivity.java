package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
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
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.CallbackListener;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.ags.query.QueryTask;

import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;

import edu.mit.mitmobile2.objs.FineListItem;

public class ArcGISActivity extends Activity {
	
	private static final String TAG = "ArcGISActivity";
	MapView map = null;
    private FullScreenLoader mLoadingView;
	ArcGISTiledMapServiceLayer serviceLayer;
	ArcGISTiledMapServiceLayer layer1; //debug
	ArcGISTiledMapServiceLayer layer2; //debug
	String targetLayer;
	QueryTask queryTask;
	Query query;
	FeatureSet featureSet;
	Location location;
	Button querybt;
	EditText buildingQuery;
	GraphicsLayer gl;
	GraphicsLayer graphicsLayer;
	Graphic[] highlightGraphics;  

	boolean blQuery = true;
	String buildingCriteria;
	ProgressDialog progress;

	final static int HAS_RESULTS = 1;
	final static int NO_RESULT = 2;
	final static int CLEAR_RESULT = 3;
	
	private static final int WEST_LONGITUDE_E6  = -71132698;
	private static final int EAST_LONGITUDE_E6  = -71006698;
	private static final int NORTH_LATITUDE_E6  =  42407741;
	private static final int SOUTH_LATITUDE_E6  =  42331392;
	
	String targetServerURL = "http://ims-pub.mit.edu/ArcGIS/rest/services/mobile/WhereIs_Base_Topo_Mobile/MapServer";

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.arcgis);
        mLoadingView = (FullScreenLoader) findViewById(R.id.mapLoading);

		// Retrieve the map and initial extent from XML layout
		map = (MapView)findViewById(R.id.map);

		mapInit();

		queryTask = new QueryTask(targetServerURL + "/17");

		query = new Query();
		query.setReturnGeometry(true);
		query.setReturnIdsOnly(false);
		
		querybt = (Button) findViewById(R.id.queryButton);
		buildingQuery = (EditText) findViewById(R.id.buildingQuery);
		
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

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

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

		location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		
		//location = map.getLocationService().getLocation();
		double lat = location.getLatitude() *1000000;
		double lon = location.getLongitude() *1000000;
		Log.d(TAG,"lat:" + lat + "\nlon:" + lon);
		Point centerPt = new Point();
		centerPt.setX(lat);
		centerPt.setY(lon);
		//map.centerAt(centerPt, true);

		map.setOnZoomListener(new MyOnZoomListener());
		map.setOnSingleTapListener(new MyOnSingleTapListener());
		
		querybt.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				targetLayer = targetServerURL.concat("/17");
				Log.d(TAG,"targetLayer = " + targetLayer);
				buildingCriteria = buildingQuery.getEditableText().toString();
				query.setText(buildingCriteria);
				String[] queryParams = { targetLayer};
				AsyncQueryTask asyncQuery = new AsyncQueryTask();
				asyncQuery.execute(queryParams);
			}
		});

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
			progress = ProgressDialog.show(ArcGISActivity.this, "",
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

			Toast toast = Toast.makeText(ArcGISActivity.this, message,
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
                	Log.d(TAG,"layer = " + layer.getUrl());
                	                	
            		serviceLayer = new ArcGISTiledMapServiceLayer(layer.getUrl());            		
                    map.addLayer(serviceLayer);
                }
            } else if (msg.arg1 == MobileWebApi.ERROR) {
                mLoadingView.showError();
            } else if (msg.arg1 == MobileWebApi.CANCELLED) {
                mLoadingView.showError();
            }
        }
    };
	
    private void mapInit() {
        mLoadingView = (FullScreenLoader) findViewById(R.id.mapLoading);

		// Retrieve the map and initial extent from XML layout
		map = (MapView)findViewById(R.id.map);

		mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.showLoading();
        MapModel.fetchMapServerData(this, uiHandler);    	
    }
}
