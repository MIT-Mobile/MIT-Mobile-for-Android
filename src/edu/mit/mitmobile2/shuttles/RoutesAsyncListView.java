package edu.mit.mitmobile2.shuttles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.mitmobile2.LoaderBar;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.objs.RouteItem;
import edu.mit.mitmobile2.objs.RouteItem.Stops;

public class RoutesAsyncListView  extends LinearLayout implements SliderInterface, OnItemClickListener   {

	//private static String TAG = "RoutesAsyncListView";
	Activity mActivity;
	
	private List<Stops> mStops;

	LoaderBar lb;
	
	boolean cancelUpdateThread;
	boolean updateThreadRunning = false;
	
	RouteStopsArrayAdapter ra;
	
	RouteItem ri;
	
	ListView lv;
	
	/****************************************************/
	
	RoutesAsyncListView(Context context, String routeId, RouteItem ri) {

		super(context);
		
		mActivity = (Activity) context;
	
		this.ri = ri;
		
		createView();
		
	}
	/****************************************************/
	void terminate() {
		cancelUpdateThread = true;	
	}
	
	/****************************************************/
	private void getData() {
		
		if (updateThreadRunning) {
			lb.errorLoading();
			//cancelUpdateThread = true;
			return;
		}
		
		mStops = new ArrayList<Stops>();
		
		if(ra == null) {
			ra = new RouteStopsArrayAdapter(mActivity, R.layout.routes_row, 0, mStops);
			lv.setAdapter(ra);
		}
		
		final Handler routeUpdateHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				lb.setLastLoaded(new Date());
				lb.endLoading();
				if(msg.arg1 == MobileWebApi.SUCCESS) {
			    	 ri = ShuttleModel.getUpdatedRoute(ri);
			    		mStops = ri.stops;
			    		ra.clear();
				    	for (Stops s : mStops) {
				    		 ra.add(s);
				    	}

			    	 ra.notifyDataSetChanged();
				} else if (msg.arg1 == MobileWebApi.ERROR) {
					Toast.makeText(mActivity, MobileWebApi.NETWORK_ERROR, Toast.LENGTH_LONG).show();
	    			lb.errorLoading();
				}
			}
		};
		
		new Thread() {
			@Override
			public void run() {
				 int refresh_wait = 1000*20;  // refresh every 20 seconds
		    	 while(!cancelUpdateThread) {
		    		 // Update routes...
		    		 Log.d("RoutesAsyncListView","fetchRouteDetails from RoutesAsyncListView");
		    		 ShuttleModel.fetchRouteDetails(mActivity, ri, routeUpdateHandler);
		    		 try {
		    			 Thread.sleep(refresh_wait);
		    		 } catch (InterruptedException e) {
		    			 e.printStackTrace();
		    		 } 
		    	 }
		    	 updateThreadRunning = false;
			}
		}.start();
		
		updateThreadRunning = true;
		
	}
	/****************************************************/
	void createView() {
		

		LayoutInflater vi = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		LinearLayout topView = (LinearLayout) vi.inflate(R.layout.routes_lv, null);
		
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		lv = (ListView) topView.findViewById(R.id.routesLV);
		lv.setOnItemClickListener(this);
		
		TextView tv;
		
		
		tv = (TextView) topView.findViewById(R.id.routesTitleTV);
		tv.setText(ri.title);

		////////////////////
		
		tv = (TextView) topView.findViewById(R.id.routesInfoTV);

		String text = "";
		if (ri.isRunning) {
			text = ri.gpsActive ? MITRoutesSliderActivity.GPS_ONLINE : MITRoutesSliderActivity.GPS_OFFLINE;
		} 
		else text = MITRoutesSliderActivity.NOT_RUNNING;
		text += "\n";
		
		if (ri.summary.endsWith(".")) text += ri.summary + "\n";
		else text += ri.summary + ".\n";
		
		text += "Route loop repeats every " + ri.interval + " minutes.";
		tv.setText(text);

		////////////////////
		
		
		// FIXME HACK!!! neither FILL not layout_weight=1 with WRAP work 
        Display display = mActivity.getWindowManager().getDefaultDisplay(); 
        int height = display.getHeight();
        topView.setMinimumHeight(height-30);

		
		
		
		lb = new LoaderBar(mActivity);
		topView.addView(lb, 0);
		
		addView(topView);
		
	}
	 
	public List<Stops> getStops() {
		return mStops;
	}
	
	/****************************************************/
	@Override
	public void updateView() {
		//if (!updateThreadRunning) getData();
	}
	
	/****************************************************/
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		terminate();
		
		Stops s = (Stops) lv.getItemAtPosition(position);
		
		
		Intent i = new Intent(mActivity, MITStopsSliderActivity.class);  
		i.putExtra(ShuttleModel.KEY_ROUTE_ID, ri.route_id); 
		i.putExtra(ShuttleModel.KEY_STOP_ID, s.id); 
		mActivity.startActivity(i);
		
	}
	
	@Override
	public View getView() {
		return this;
	}
	
	@Override
	public void onSelected() {
		cancelUpdateThread = false;
		if (!updateThreadRunning) {
			lb.startLoading();
			getData();
		}
	}
	@Override
	public LockingScrollView getVerticalScrollView() {
		return null;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}
}
