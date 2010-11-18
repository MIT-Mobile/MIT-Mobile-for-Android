package edu.mit.mitmobile.shuttles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import edu.mit.mitmobile.Global;
import edu.mit.mitmobile.Module;
import edu.mit.mitmobile.R;
import edu.mit.mitmobile.SliderActivity;
import edu.mit.mitmobile.objs.RouteItem.Stops;

public class MITStopsSliderActivity extends SliderActivity {
	
	// Alarm related
	static public HashMap<String,HashMap <String,Long>> alertIdx;  // <Stop,<Routes,Times>>
	
	private List<Stops> mStops;
	static public ArrayList<String> stop_ids = new ArrayList<String>(); 

	private int last_pos;
	private StopsAsyncView curView;

	protected String routeId,stopId;
	
	protected Stops stops;
 
	static final int MENU_VIEW_MAP = MENU_LAST + 1;
	
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
        
		mStops = ShuttleModel.getRoute(routeId).stops;
		last_pos = ShuttleModel.getStopPosition(mStops, stopId);
       
		pref = getSharedPreferences(Global.PREFS_STELLAR,Context.MODE_WORLD_READABLE|Context.MODE_WORLD_READABLE); 
		
        alertIdx = ShuttleModel.getAlerts(pref);
		
    	setTitle("MIT Stops");
    	
    	createViews();

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
    	
    	for (Stops s : mStops) {
    		cv = new StopsAsyncView(this, s);
    		addScreen(cv, s.title, "Shuttle Stop");  
    	}

    	setPosition(last_pos);
		curView = (StopsAsyncView) getScreen(last_pos);  // need to set here first time to avoid memory leak (otherwise onStop() will find curView==null)
    	
    }	
    

	/****************************************************/
	@Override
	protected void setPosition(int position) {
		
		super.setPosition(position);

		int curPos = getPosition();
		
		if (last_pos!=curPos) {
			
			curView = (StopsAsyncView) getScreen(curPos);
			
			last_pos = curPos;
		}
		
	}

	/****************************************************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case MENU_VIEW_MAP: 
			MITRoutesSliderActivity.launchShuttleRouteMap(this, ShuttleModel.getRoute(routeId), mStops, getPosition());
			break;
		
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		menu.add(0, MENU_VIEW_MAP, Menu.NONE, "View on Map")
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
}
