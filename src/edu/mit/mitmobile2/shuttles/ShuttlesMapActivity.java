package edu.mit.mitmobile2.shuttles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import com.esri.android.map.GraphicsLayer;

import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MITMapView;
import edu.mit.mitmobile2.maps.MapGraphicsLayer;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.MapPoint;
import edu.mit.mitmobile2.objs.MapUpdater;
import edu.mit.mitmobile2.objs.RouteItem;
import edu.mit.mitmobile2.objs.RouteItem.Loc;
import edu.mit.mitmobile2.objs.RouteItem.Stops;
import edu.mit.mitmobile2.objs.RouteItem.Vehicle;
import edu.mit.mitmobile2.objs.RouteMapItem;
import edu.mit.mitmobile2.objs.ShuttleMapUpdater;
import edu.mit.mitmobile2.objs.StopMapItem;
import edu.mit.mitmobile2.objs.VehicleMapItem;

public class ShuttlesMapActivity extends NewModuleActivity {

	private static final String TAG = "ShuttlesMapActivity";
	public MITMapView map;
	private FullScreenLoader mLoadingView;
	protected String module;
	Context mContext;
	Location location;
	Bundle extras;
	ArrayList<MapItem> mapItems;
	String routeId = "";
	RouteItem routeItem;
	private MapUpdater mapUpdater;

	protected ListView mListView;
	ProgressDialog progress;

	public static final String ROUTE_ID_KEY = "routeID";
	public static final String SHUTTLE_ROUTE_LAYER = "shuttle_route_layer";
	public static final String SHUTTLE_STOPS_LAYER = "shuttle_stops_layer";

	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "oncreate()");
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(getLayoutID());
		//mLoadingView = (FullScreenLoader) findViewById(getMapLoadingViewID());
		this.extras = this.getIntent().getExtras();
		map = (MITMapView) findViewById(getMapViewID());
		//map.init(mContext);
		
		if (extras.containsKey(ShuttlesMapActivity.ROUTE_ID_KEY)) {
			routeId = extras.getString(ShuttlesMapActivity.ROUTE_ID_KEY);
			displayRoute(routeId);
			this.mapUpdater = new ShuttleMapUpdater();
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("route_id", routeId);
			this.mapUpdater.init(mContext, params, mapUpdateUiHandler);
		}

		// Retrieve the non-configuration instance data that was previously
		// returned.
		Object init = getLastNonConfigurationInstance();
		if (init != null) {
			map.restoreState((String) init);
		}

	}

	@Override
	protected List<MITMenuItem> getPrimaryMenuItems() {
		ArrayList<MITMenuItem> menuItems = new ArrayList<MITMenuItem>();
		menuItems.add(new MITMenuItem("list", "List", R.drawable.menu_browse));		
		return menuItems;
	}
	
	@Override
	protected boolean isScrollable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		if (optionId.equals("list")) {
			finish();
		}
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
		Log.d(TAG, "onNewIntent");
		this.extras = intent.getExtras();

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			Log.d(TAG, "do search");
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.d(TAG, "query = " + query);
			// MITMapsDataModel.executeSearch(query, map.mapSearchUiHandler,
			// mContext);
			// doMySearch(query);
		} else if (extras.containsKey(MITMapView.MAP_DATA_KEY)) {
			mapItems = (ArrayList) extras
					.getParcelableArrayList(MITMapView.MAP_DATA_KEY);
			map.addMapItems(mapItems);
			map.syncGraphicsLayers();
		}

	} // End of onN

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume");
		map.unpause();

	}

	public MapUpdater getMapUpdater() {
		return mapUpdater;
	}

	public void setMapUpdater(MapUpdater mapUpdater) {
		this.mapUpdater = mapUpdater;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		this.getMapUpdater().stop();
		Log.d(TAG, "onPause()");
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
	protected void onMapLoaded() {
	}

	@Override
	protected NewModule getNewModule() {
		return new ShuttlesModule();
	}

	private void displayRoute(String routeId) {
		routeItem = new RouteItem();
		routeItem.id = routeId;
		ShuttleModel.fetchRouteDetails(mContext, routeItem, routeUiHandler);
	}


	public void addShuttleItems(RouteItem routeItem) {
		HashMap<String,ArrayList<? extends MapItem>> layers = ShuttleModel.buildShuttleItems(routeItem);
		//debug layers
    	Iterator it = layers.entrySet().iterator();
		while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        String key = (String)pairs.getKey();
	        ArrayList<MapItem> m = (ArrayList<MapItem>) layers.get(key);
	        Log.d(TAG,"DEBUG: layer " + key + " has " + m.size() + " map items");
		}
		
		map.addMapItems(layers.get(SHUTTLE_ROUTE_LAYER),SHUTTLE_ROUTE_LAYER);
		map.addMapItems(layers.get(SHUTTLE_STOPS_LAYER),SHUTTLE_STOPS_LAYER);
		map.addMapItems(layers.get(MITMapView.DEFAULT_GRAPHICS_LAYER),MITMapView.DEFAULT_GRAPHICS_LAYER);		
	}
	
	public Handler routeUiHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "mapSearchUiHandler success");
			if (msg.arg1 == MobileWebApi.SUCCESS) {

				try {
					RouteItem updatedRouteItem = ShuttleModel
							.getUpdatedRoute(routeItem);
					addShuttleItems(updatedRouteItem);
					map.syncGraphicsLayers();
					map.fitMapItems();
				} catch (Exception e) {
					Log.d(TAG, "mapSearchUiHander exception");
					Log.d(TAG, e.getStackTrace().toString());
				}
			} else if (msg.arg1 == MobileWebApi.ERROR) {

			} else if (msg.arg1 == MobileWebApi.CANCELLED) {

			}
		}
	};

	public Handler vehicleUpdateUiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (msg.arg1 == MobileWebApi.SUCCESS) {
				// get the route item returned by fetchRouteDetails
				routeItem = (RouteItem) msg.obj;

				RouteItem updatedRouteItem = ShuttleModel.getUpdatedRoute(routeItem);

				HashMap<String,ArrayList<? extends MapItem>> layers = ShuttleModel.buildShuttleItems(updatedRouteItem);
					
				// create a new message with the mapData object
				Message mapMessage = new Message();
				mapMessage.arg1 = MobileWebApi.SUCCESS;
				mapMessage.obj = layers;

				// send the mapMessage to the mapUpdateUiHandler
				mapUpdateUiHandler.sendMessage(mapMessage);
			} else if (msg.arg1 == MobileWebApi.ERROR) {
				Log.d("ZZZ", "ShuttleMapUpdater error");
			} else if (msg.arg1 == MobileWebApi.CANCELLED) {
				Log.d("ZZZ", "ShuttleMapUpdater cancelled");
			}
		}
	};

	public Handler mapUpdateUiHandler = new Handler() {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "mapSearchUiHandler success");
			if (msg.arg1 == MobileWebApi.SUCCESS) {

				try {
					HashMap<String,ArrayList<? extends MapItem>> layers = (HashMap<String,ArrayList<? extends MapItem>>)msg.obj;
					Log.d(TAG,"adding vehicle locations from updater");
					map.addMapItems(layers.get(SHUTTLE_ROUTE_LAYER),SHUTTLE_ROUTE_LAYER);
					map.addMapItems(layers.get(SHUTTLE_STOPS_LAYER),SHUTTLE_STOPS_LAYER);
					map.addMapItems(layers.get(MITMapView.DEFAULT_GRAPHICS_LAYER),MITMapView.DEFAULT_GRAPHICS_LAYER);		
					map.syncGraphicsLayers();
				} catch (Exception e) {
					Log.d(TAG, "mapSearchUiHander exception");
					Log.d(TAG, e.getStackTrace().toString());
				}
			} else if (msg.arg1 == MobileWebApi.ERROR) {

			} else if (msg.arg1 == MobileWebApi.CANCELLED) {

			}
		}
	};

}