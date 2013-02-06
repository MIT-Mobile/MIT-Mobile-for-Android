package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.RouteItem;
import edu.mit.mitmobile2.shuttles.ShuttleModel;

public class StopMapItem extends MapItem {

//	public StopMapItem() {
//		super();
//		timer = new Timer();
//	}

	public View getCallout(Context mContext, MapAbstractionObject mao) {

		String title = (String)this.getItemData().get("title");	
		String arriving = null;
		long curTime = System.currentTimeMillis();
		int next = Integer.parseInt(this.getItemData().get("next").toString());

		int mins = (int) (next*1000 - curTime)/1000/60;
		int hours = mins / 60;

		if (next==0) {
			arriving = "";
		} else if (hours>1) {
			mins = mins - (hours*60);
			arriving = "arriving in " + String.valueOf(hours) + " hrs " + String.valueOf(mins) + " mins";
		} else if (hours>0) {
			mins = mins - (hours*60);
			arriving = "arriving in " + String.valueOf(hours) + " hr " + String.valueOf(mins) + " mins";
		} else {
			if (mins==0) arriving = "arriving now!";
			else if (mins==1) arriving = "arriving in 1 min";
			else arriving = "arriving in " + String.valueOf(mins) + " mins";
		}

   		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout calloutLayout = (LinearLayout) inflater.inflate(R.layout.map_shuttle_callout, null);
		
		TextView calloutShuttleTitle = (TextView)calloutLayout.findViewById(R.id.callout_shuttle_title);
		calloutShuttleTitle.setText(title);

		TextView calloutShuttleArriving = (TextView)calloutLayout.findViewById(R.id.callout_shuttle_arriving);
		calloutShuttleArriving.setText(arriving);
		
		return calloutLayout;
	}
	
//	@Override
//	public void initTimer(Context mContext) {
//		// TODO Auto-generated method stub
//		MyTimerTask myTask = new MyTimerTask();
//		timer.schedule(myTask, 3000, 3000);        
//	}

	class MyTimerTask extends TimerTask {
		 public void run() {
			 // ERROR
			 Log.d("ZZZ","timer");
			 try {
				 RouteItem route = ShuttleModel.getRoute((String)itemData.get("route_id"));
			 }
			 catch (Exception e) {
				 Log.d("ZZZ","exception = " + e.getMessage());
			 }
		 }
	}

	@Override
	public View getCallout(Context mContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getCallout(Context mContext, ArrayList<MapItem> mapItems, int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getCallout(Context mContext, ArrayList<MapItem> mapItems) {
		// TODO Auto-generated method stub
		return null;
	}

}