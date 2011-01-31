package edu.mit.mitmobile2.shuttles;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderActivity;
import edu.mit.mitmobile2.maps.MITMapActivity;
import edu.mit.mitmobile2.objs.RouteItem;
import edu.mit.mitmobile2.objs.RouteItem.Stops;

public class MITRoutesSliderActivity extends SliderActivity {

	static String KEY_ROUTE_ID = "route_id";
	
	private int last_pos = -1;
	
	private RoutesAsyncListView curView;

	//static final int MENU_REFRESH  = Menu.FIRST+2;
	static final int MENU_MAP_LIST_VIEW = MENU_LAST + 1;

	static final String NOT_RUNNING = "Bus not running. Following schedule.";
	static final String GPS_ONLINE = "Real time bus tracking online.";
	static final String GPS_OFFLINE = "Tracking offline. Following Schedule";
	
	/****************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
        
    	setTitle("MIT Routes");
    	
    	createViews();

	}
    /****************************************************/
	@Override
	protected void onPause() {
		if (curView!=null) curView.terminate();
		super.onPause();
	}

	@Override
	protected void onStop() {
		if (curView!=null) curView.terminate();
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// FIXME
		//if (curView!=null) curView.getData();
	}
	/****************************************************/
    void createViews() {

    	RoutesAsyncListView cv;
    	
    	// TODO get ALL data ONCE from above layer?
    	
    	for (int x=0; x < ShuttleModel.getSortedRoutes().size(); x++) {

    		RouteItem r = ShuttleModel.getSortedRoutes().get(x);
    		
    		String routeId = r.title;
    		
    		cv = new RoutesAsyncListView(this, routeId, r);

    		addScreen(cv, r.title, "Route Detail");
   		
    	}
    	last_pos = getPositionValue();
    	setPosition(last_pos);
		curView = (RoutesAsyncListView) getScreen(last_pos);  // need to set here first time to avoid memory leak (otherwise onStop() will find curView==null)
    }
    
	@Override
	protected void setPosition(int position) {
		
		super.setPosition(position);
		
		int curPos = getPosition();
		
		if (last_pos!=curPos) {
			if (curView!=null) curView.terminate();
			curView = (RoutesAsyncListView) getScreen(curPos);
			last_pos = curPos;
		}
		
	}
    

	/****************************************************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case MENU_MAP_LIST_VIEW: 
			RoutesAsyncListView view = (RoutesAsyncListView) getScreen(getPosition());
			launchShuttleRouteMap(this, view.ri, view.getStops(), -1);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		menu.add(0, MENU_MAP_LIST_VIEW, Menu.NONE, "View on Map")
		  .setIcon(R.drawable.menu_view_on_map);
	}
	
	@Override
	protected Module getModule() {
		return new ShuttlesModule();
	}
	
	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}
    
	static void launchShuttleRouteMap(Context context, RouteItem routeItem, List<Stops> stops, int bubblePos) {
		Intent i = new Intent(context, MITMapActivity.class);
		i.putExtra(MITMapActivity.KEY_MODULE, MITMapActivity.MODULE_SHUTTLE);  
		if (bubblePos>-1) i.putExtra(MITMapActivity.KEY_POSITION, bubblePos);  
		
		// prefetch to speed up first draw call
		ShuttleModel.fetchRouteDetails(context, routeItem, new Handler());
		
		RouteItem updatedRouteItem = ShuttleModel.getUpdatedRoute(routeItem);
		i.putExtra(MITMapActivity.KEY_HEADER_TITLE, updatedRouteItem.title);
		String subtitle = updatedRouteItem.gpsActive ? GPS_ONLINE : GPS_OFFLINE;
		i.putExtra(MITMapActivity.KEY_HEADER_SUBTITLE, subtitle);
		i.putExtra(MITMapActivity.KEY_SHUTTLE_STOPS, stops.toArray());
		i.putExtra(MITMapActivity.KEY_ROUTE, routeItem);
		
		context.startActivity(i);
	}
}
