package edu.mit.mitmobile2.shuttles;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import edu.mit.mitmobile2.CategoryNewModuleActivity;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.OnMITMenuItemListener;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MITMapActivity;
import edu.mit.mitmobile2.objs.RouteItem;
import edu.mit.mitmobile2.objs.RouteItem.Stops;

public class MITRoutesSliderActivity extends CategoryNewModuleActivity {
	private RoutesAsyncListView curView;
	
	private int position;

	static final String KEY_POSITION = "key_position";
	static final String NOT_RUNNING = "Bus not running. Following schedule.";
	static final String GPS_ONLINE = "Real time bus tracking online.";
	static final String GPS_OFFLINE = "Tracking offline. Following Schedule";
	
	/****************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
        
    	setTitle("MIT Routes");
    	
    	if(!ShuttleModel.routesLoaded()) {
    		finish();
    		return;
    	}
    	Bundle bundle = getIntent().getExtras();
    	if (null != bundle) {
    		position = bundle.getInt(KEY_POSITION, 0);
    	}
    	
    	getSecondaryBar().addMenuItem(new MITMenuItem("LIST_MAP", "", R.drawable.menu_view_as_list));
    	getSecondaryBar().setOnMITMenuItemListener(new OnMITMenuItemListener() {
			@Override
			public void onOptionItemSelected(String optionId) {
				// TODO Auto-generated method stub
				if (optionId.equals("LIST_MAP")) {
					RoutesAsyncListView view = (RoutesAsyncListView) getCurrentCategory();
					launchShuttleRouteMap(MITRoutesSliderActivity.this, view.ri, view.getStops(), -1);
				} 
			}
		});
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
		if (curView!=null) curView.onSelected();
	}
	
	/****************************************************/
    void createViews() {

    	RoutesAsyncListView cv;
    	
    	for (int x=0; x < ShuttleModel.getSortedRoutes().size(); x++) {

    		RouteItem r = ShuttleModel.getSortedRoutes().get(x);
    		
    		String routeId = r.title;
    		
    		cv = new RoutesAsyncListView(this, routeId, r);

    		addCategory(cv, r.title, r.title);
    	}
    	onOptionItemSelected(ShuttleModel.getSortedRoutes().get(position).title);
    }
    
	@Override
	public void onOptionItemSelected(String optionId) {
		// TODO Auto-generated method stub
		super.onOptionItemSelected(optionId);
		
		if(curView != null) {
			curView.terminate();
		}
		curView = (RoutesAsyncListView) getCategory(optionId);
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
	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return new ShuttlesModule();
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
}
