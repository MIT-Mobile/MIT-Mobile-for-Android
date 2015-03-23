package edu.mit.mitmobile2.shuttles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderListAdapter.OnPositionChangedListener;
import edu.mit.mitmobile2.SliderListNewModuleActivity;
import edu.mit.mitmobile2.objs.RouteItem;
import edu.mit.mitmobile2.objs.RouteItem.Stops;

public class MITStopsSliderActivity extends SliderListNewModuleActivity implements OnPositionChangedListener {
	
	final static String TAG = "MITStopsSliderActivity";
	// Alarm related
	static public HashMap<String,HashMap <String,Long>> alertIdx;  // <Stop,<Routes,Times>>
	
	private List<Stops> mStops;
	static public ArrayList<String> stop_ids = new ArrayList<String>(); 

	private int last_pos;
	private static StopsAsyncView curView;

	protected String routeId,stopId;
	
	protected Stops stops;
	
	SharedPreferences pref;
	
	/****************************************************/
   
	@SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles" })
	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate()");
    	super.onCreate(savedInstanceState);

    	Bundle extras = getIntent().getExtras();

        if (extras!=null){ 
        	routeId = extras.getString(ShuttleModel.KEY_ROUTE_ID);
        	stopId = extras.getString(ShuttleModel.KEY_STOP_ID);
        	Log.d(TAG,"routeId = " + routeId);
        	Log.d(TAG,"stopID = " + stopId);
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
    	
    	setOnPositionChangedListener(this);

	}
	
	
	@Override
	public void onNewIntent(Intent intent) {
		stopId = intent.getStringExtra(ShuttleModel.KEY_STOP_ID);
		setPosition(ShuttleModel.getStopPosition(mStops, stopId));
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
    	for (int i = 0; i < mStops.size(); i++) {
    	    Stops s = mStops.get(i);
    	    cv = new StopsAsyncView(this, s);
    	    addScreen(cv, "" + (i+1) + " of " + mStops.size());
    	}

    	setPosition(last_pos);
		curView = (StopsAsyncView) getScreen(last_pos);  // need to set here first time to avoid memory leak (otherwise onStop() will find curView==null)
    	
    }	
    

	@Override
	public void onPositionChanged(int newPosition, int oldPosition) {
		if (curView != null) {
			curView.terminate();
		}
		curView = (StopsAsyncView) getScreen(newPosition);
	}
	
	@Override
	protected List<MITMenuItem> getPrimaryMenuItems() {
	    ArrayList<MITMenuItem> items = new ArrayList<MITMenuItem>();
	    items.add(new MITMenuItem("viewmap", "View on Map", R.drawable.menu_view_on_map));
	    return items;
	}
	
	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected NewModule getNewModule() {
	    return new ShuttlesModule();
	}

	@Override
	protected void onOptionSelected(String optionId) {
	    if (optionId.equals("viewmap")) {
		MITRoutesSliderActivity.launchShuttleRouteMap(this, ShuttleModel.getRoute(routeId), mStops, getPosition());
	    }
	}
}
