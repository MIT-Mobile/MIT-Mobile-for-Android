package edu.mit.mitmobile2.shuttles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import edu.mit.mitmobile2.CategoryNewModuleActivity;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.SliderView.OnPositionChangedListener;
import edu.mit.mitmobile2.objs.RouteItem;
import edu.mit.mitmobile2.objs.RouteItem.Stops;

public class MITStopsSliderActivity extends CategoryNewModuleActivity {
	
	// Alarm related
	static public HashMap<String,HashMap <String,Long>> alertIdx;  // <Stop,<Routes,Times>>
	
	private List<Stops> mStops;
	static public ArrayList<String> stop_ids = new ArrayList<String>(); 

	private int last_pos;
	private StopsAsyncView curView;

	protected String routeId,stopId;
	
	protected Stops stops;
 
	SharedPreferences pref;
	
	/****************************************************/
   
	@Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);

    	Bundle extras = getIntent().getExtras();

        if (extras!=null){ 
        	routeId = extras.getString(ShuttleModel.KEY_ROUTE_ID);
        	stopId = extras.getString(ShuttleModel.KEY_STOP_ID);
        }
        
        RouteItem route = ShuttleModel.getRoute(routeId);
        if (route == null) {
		    finish();
		    return;
        }        
		mStops = ShuttleModel.getRoute(routeId).stops;

		
		last_pos = ShuttleModel.getStopPosition(mStops, stopId);
       
		pref = getSharedPreferences(Global.PREFS_SHUTTLES,Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE); 
		
        alertIdx = ShuttleModel.getAlerts(pref);
		
    	setTitle("MIT Stops");
    	
    	createViews();
	}
	
	@Override
	public void onNewIntent(Intent intent) {
		stopId = intent.getStringExtra(ShuttleModel.KEY_STOP_ID);
		onOptionItemSelected(stopId);
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
		//saveAlerts(); // TODO
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
        alertIdx = ShuttleModel.getAlerts(pref);
		if (curView!=null) curView.getData();
	}
	/****************************************************/
    void createViews() {

    	StopsAsyncView cv;   
    	
    	// TODO get ALL data ONCE from above layer?
    	
    	for (Stops s : mStops) {
    		cv = new StopsAsyncView(this, s);
    		addCategory(cv, s.id, s.title);  
    	}

    	onOptionItemSelected(stopId);
		curView = (StopsAsyncView) getCategory(stopId);  // need to set here first time to avoid memory leak (otherwise onStop() will find curView==null)
    	
    }	
    
    
	@Override
	public void onOptionItemSelected(String optionId) {
		// TODO Auto-generated method stub
		super.onOptionItemSelected(optionId);
		if (curView != null) {
			curView.terminate();
		}
		curView = (StopsAsyncView) getCategory(optionId);
	}
	
//	MITRoutesSliderActivity.launchShuttleRouteMap(this, ShuttleModel.getRoute(routeId), mStops, getPosition());
	
	@Override
	public boolean isModuleHomeActivity() {
		return false;
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
