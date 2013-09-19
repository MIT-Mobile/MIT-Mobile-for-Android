package edu.mit.mitmobile2.objs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.shuttles.ShuttleModel;

public class ShuttleMapUpdater extends MapUpdater {

	Timer timer; 
	RouteItem routeItem;
	private static final String TAG = "ShuttleMapUpdater";
	
	@Override
	public void updateMap(Context mContext) {
		Log.d(TAG,"updateMap()");
		routeItem = new RouteItem();
		routeItem.route_id = (String)params.get("route_id"); // debug
		Log.d(TAG,"fetchRouteDetails from ShuttleMapUpdater");
		ShuttleModel.fetchRouteDetails(mContext, routeItem, uiHandler);
	}

	@Override
	public void init(Context mContext, HashMap<String, Object> mParams,Handler mHandler) {
		// TODO Auto-generated method stub
		Log.d(TAG,"init()");
		super.init(mContext, mParams,mHandler);
		this.context = mContext;
		MyTimerTask myTask = new MyTimerTask();
		timer = new Timer();
		timer.schedule(myTask, 1, 10000);        		
	}

	class MyTimerTask extends TimerTask {
		 public void run() {
			 // ERROR
			 Log.d(TAG,"MyTimerTask: run");
			 updateMap(context);
		 }
	}
	
	

	@Override
	public void stop() {
		timer.cancel();
	}

	public Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.arg1 == MobileWebApi.SUCCESS) {
            	// get the route item returned by fetchRouteDetails
            	routeItem = (RouteItem)msg.obj;	

            	//RouteItem updatedRouteItem = ShuttleModel.getUpdatedRoute(routeItem);		
        		// Convert the routeItem to a mapData object
            	HashMap<String,ArrayList<? extends MapItem>> layers = ShuttleModel.buildShuttleItems(routeItem);
            
            	// create a new message with the mapData object
            	Message mapMessage = new Message();
            	mapMessage.arg1 = MobileWebApi.SUCCESS;
            	mapMessage.obj = layers;
            	
            	// send the mapMessage to the mapUpdateUiHandler
        		handler.sendMessage(mapMessage);
            } 
            else if (msg.arg1 == MobileWebApi.ERROR) {	
            } 
            else if (msg.arg1 == MobileWebApi.CANCELLED) {
            }
        }
    };
    
}
