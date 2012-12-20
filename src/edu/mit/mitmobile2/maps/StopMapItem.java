package edu.mit.mitmobile2.maps;

<<<<<<< HEAD
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.Log;
=======
import android.content.Context;
>>>>>>> b1d41f7cd04b520a273992af5761a791f573e2ba
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.MapItem;
<<<<<<< HEAD
import edu.mit.mitmobile2.objs.RouteItem;
import edu.mit.mitmobile2.shuttles.ShuttleModel;
=======
>>>>>>> b1d41f7cd04b520a273992af5761a791f573e2ba

public class StopMapItem extends MapItem {

//	public StopMapItem() {
//		super();
//		timer = new Timer();
//	}

	public View getCallout(Context mContext) {

		String buildingName = (String)this.getItemData().get("buildingName");
		String buildingNumber = (String)this.getItemData().get("buildingNumber");


   		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout calloutLayout = (LinearLayout) inflater.inflate(R.layout.map_building_callout, null);
		
		TextView calloutBuildingNumber = (TextView)calloutLayout.findViewById(R.id.callout_building_number);
		calloutBuildingNumber.setText(buildingNumber);

		TextView calloutBuildingName = (TextView)calloutLayout.findViewById(R.id.callout_building_name);
		calloutBuildingName.setText(buildingName);
		
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

}
