package edu.mit.mitmobile2.shuttles;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderListNewModuleActivity;
import edu.mit.mitmobile2.objs.RouteItem;
import edu.mit.mitmobile2.objs.RouteItem.Stops;

public class MITRoutesSliderActivity extends SliderListNewModuleActivity {
	private static final String TAG = "MITRoutesSliderActivity";

	private int selectedPos;
	private int startPos;
	
	static final String KEY_POSITION = "key_position";
	static final String NOT_RUNNING = "Bus not running. Following schedule.";
	static final String GPS_ONLINE = "Real time bus tracking online.";
	static final String GPS_OFFLINE = "Tracking offline. Following Schedule";
	
	/****************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG,"onCreate()");
    	super.onCreate(savedInstanceState);
        
    	setTitle("MIT Routes");
    	
    	if(!ShuttleModel.routesLoaded()) {
    		finish();
    		return;
    	}
    	Bundle bundle = getIntent().getExtras();
    	if (null != bundle) {
    		startPos = bundle.getInt(KEY_POSITION, 0);
    		selectedPos = startPos;
    	}
    	
    	createViews();
	}
    /****************************************************/
	@Override
	protected void onPause() {
		RoutesAsyncListView oldView = (RoutesAsyncListView) getScreen(selectedPos);
		oldView.terminate();
		super.onPause();
	}

	@Override
	protected void onStop() {
		//there is no need to terminate RoutesAsyncListView as onPause takes care of this
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		RoutesAsyncListView oldView = (RoutesAsyncListView) getScreen(startPos);
		if(oldView!=null) oldView.onSelected();
	}
	
	

	/****************************************************/
    void createViews() {

    	RoutesAsyncListView cv;
    	
    	for (int x=0; x < ShuttleModel.getSortedRoutes().size(); x++) {

    		RouteItem r = ShuttleModel.getSortedRoutes().get(x);
    		
    		String routeId = r.route_id; //r.title;
    		
    		cv = new RoutesAsyncListView(this, routeId, r);

    		addScreen(cv, r.title, r.title);
    	}
    	setPosition(startPos);
    }
    
	@Override
	public void onPositionChanged(int newPosition, int oldPosition) {
	    super.onPositionChanged(newPosition, oldPosition);	
	    Log.d("ZZZ","onPositionChanged");
	    if (oldPosition == -1)
	    	return;
	    
	    if (oldPosition >= 0) {
	    	RoutesAsyncListView oldView = (RoutesAsyncListView) getScreen(oldPosition);
	    	oldView.terminate();
	    }
	    selectedPos = newPosition;
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}
    
	static void launchShuttleRouteMap(Context context, RouteItem routeItem, List<Stops> stops, int bubblePos) {
		Intent i = new Intent(context, ShuttlesMapActivity.class);		
		i.putExtra(ShuttlesMapActivity.ROUTE_ID_KEY,routeItem.route_id);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(i);
	}
	@Override
	protected NewModule getNewModule() {
		return new ShuttlesModule();
	}

	@Override
	protected void onOptionSelected(String optionId) {
		if (optionId.equals("viewmap")) {
			Log.d(TAG,"viewmap");
			String routeId = ShuttleModel.getSortedRoutes().get(selectedPos).route_id;
			MITRoutesSliderActivity.launchShuttleRouteMap(this, ShuttleModel.getRoute(routeId), ShuttleModel.getRoute(routeId).stops, getPosition());
		}
	}
	
	@Override
	protected String getHeaderTitle(int position) {
	    return null;
	}
	@Override
	protected List<MITMenuItem> getPrimaryMenuItems() {
		return Arrays.asList(
				new MITMenuItem("viewmap", "View on Map", R.drawable.menu_view_on_map)
		);
	}
	
//	public static MapData toMapData(RouteItem updatedRouteItem) {
//		MapData mapData = new MapData();
//		RouteMapItem route = new RouteMapItem();
//
//		// create a polygon for route
//		// create shuttle pin + callout for each stop
//		// create shuttle location pin for each vehicle location
//		
//		route.setGeometryType(MapItem.TYPE_POLYGON);
//
//		// loop through all the stops
//		for (int s = 0; s < updatedRouteItem.stops.size(); s++) {
//			Stops stop = updatedRouteItem.stops.get(s);
//			
//			MapPoint mapPoint = new MapPoint();
//			mapPoint.lat_wgs84 = Double.valueOf(stop.lat);
//			mapPoint.long_wgs84 = Double.valueOf(stop.lon);
//			
//			// for each stop, add the paths to the route polygon
//			for (int p = 0; p < stop.path.size(); p++) {
//				Loc loc = (Loc)stop.path.get(p);
//				
//				// get a map point for the stop
//				MapPoint pathPoint = new MapPoint();
//				pathPoint.lat_wgs84 = Double.valueOf(loc.lat);
//				pathPoint.long_wgs84 = Double.valueOf(loc.lon);
//				
//				// add the map point to the route polygon
//				route.getMapPoints().add(pathPoint);
//			}
//
//			// Create a map item to show an icon at the stop
//			StopMapItem stopItem = new StopMapItem();
//			
//			// add itemData
//			stopItem.getItemData().put("alertSet", stop.alertSet);
//			stopItem.getItemData().put("direction", stop.direction);
//			stopItem.getItemData().put("gps", stop.gps);
//			stopItem.getItemData().put("id", stop.id);
//			stopItem.getItemData().put("lat", stop.lat);
//			stopItem.getItemData().put("lon", stop.lon);
//			stopItem.getItemData().put("next", stop.next);
//			stopItem.getItemData().put("now", stop.now);
//			stopItem.getItemData().put("route_id", stop.route_id);
//			stopItem.getItemData().put("title", stop.title);
//			stopItem.getItemData().put("upcoming",stop.upcoming);			
//			stopItem.setGeometryType(MapItem.TYPE_POINT);
//			
//			if (updatedRouteItem.isRunning) {
//				if (stop.upcoming) {
//					stopItem.symbol = R.drawable.map_pin_shuttle_stop_complete_next;
//				}
//				else {
//					stopItem.symbol = R.drawable.map_pin_shuttle_stop_complete;
//				}
//			}
//			else {
//				stopItem.symbol = R.drawable.shuttle_off;				
//			}	
//
//			stopItem.getItemData().put("displayName",stop.title);
//
//			stopItem.getMapPoints().add(mapPoint);
//
//			mapData.getMapItems().add(stopItem);
//		}
//
//		// add route
//		mapData.getMapItems().add(route);
//
//		// add vehicle locations
//		for (int v = 0; v < updatedRouteItem.vehicleLocations.size(); v++) {
//			Vehicle vehicle = updatedRouteItem.vehicleLocations.get(v);
//			VehicleMapItem vehicleItem = new VehicleMapItem();
//			vehicleItem.setGeometryType(MapItem.TYPE_POINT);
//			vehicleItem.setSymbol(VehicleMapItem.getShuttleMarkerForHeading(vehicle.heading));
//			vehicleItem.getMapPoints().add(new MapPoint(vehicle.lat,vehicle.lon));
//			vehicleItem.getItemData().put("heading", vehicle.heading);
//			vehicleItem.getItemData().put("lat", vehicle.lat);
//			vehicleItem.getItemData().put("lon", vehicle.lon);
//			mapData.getMapItems().add(vehicleItem);
//		}
//
//		return mapData;
//	}


}
