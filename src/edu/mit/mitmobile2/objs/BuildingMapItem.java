package edu.mit.mitmobile2.objs;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MITMapBrowseCatsActivity;
import edu.mit.mitmobile2.maps.MITMapBrowseResultsActivity;
import edu.mit.mitmobile2.maps.MITMapDetailsSliderActivity;

public class BuildingMapItem extends MapItem {
	
	public static String TAG = "BuildingMapItem";
	public BuildingMapItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getCallout(final Context mContext) {

		Log.d(TAG,"BuildingMapItem getCallout");
		String buildingName = (String)this.getItemData().get("displayName");
		String buildingNumber = (String)this.getItemData().get("bldgnum");

   		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout calloutLayout = (LinearLayout) inflater.inflate(R.layout.map_building_callout, null);
		
		TextView calloutBuildingNumber = (TextView)calloutLayout.findViewById(R.id.callout_building_number);
		calloutBuildingNumber.setText(buildingNumber);

		TextView calloutBuildingName = (TextView)calloutLayout.findViewById(R.id.callout_building_name);
		calloutBuildingName.setText(buildingName);
		
		//calloutLayout.on
		calloutLayout.setOnClickListener(new View.OnClickListener() {

	            @Override
	            public void onClick(View v) {
	            	Intent i = new Intent(mContext, MITMapDetailsSliderActivity.class); 
	            	mContext.startActivity(i);
	            }
	        });
		
		return calloutLayout;
	}

//	@Override
//	public void initTimer(Context mContext) {
//		// TODO Auto-generated method stub
//		
//	}
	
}
