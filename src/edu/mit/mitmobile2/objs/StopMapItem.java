package edu.mit.mitmobile2.objs;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MITMapDetailsSliderActivity;
import edu.mit.mitmobile2.maps.MITMapView2;
import edu.mit.mitmobile2.maps.MapAbstractionObject;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.RouteItem;
import edu.mit.mitmobile2.shuttles.MITStopsSliderActivity;
import edu.mit.mitmobile2.shuttles.ShuttleModel;

public class StopMapItem extends MapItem {

	@Override
	public View getCallout(Context mContext) {
		return null;
	}
	
	@Override
	public View getCallout(Context mContext, ArrayList<? extends MapItem> mapItems) {
		return null;
	}

	@Override
	public View getCallout(final Context mContext, final ArrayList<? extends MapItem> mapItems, final int position) {
		
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
		
		//calloutLayout.on
		calloutLayout.setOnClickListener(new View.OnClickListener() {
		
			@Override
	        public void onClick(View v) {
				Log.d("ZZZ","click stopMapItem");
				Intent i = new Intent(mContext, MITStopsSliderActivity.class);  
				i.putExtra(ShuttleModel.KEY_ROUTE_ID, (String)mapItems.get(position).getItemData().get("route_id")); 
				i.putExtra(ShuttleModel.KEY_STOP_ID, (String)mapItems.get(position).getItemData().get("id")); 
				Log.d("ZZZ","starting intent MITStopsSliderActivity");
				mContext.startActivity(i);
	        }
	    });

		
		return calloutLayout;
	}
	
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

}