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
import edu.mit.mitmobile2.maps.MapBaseActivity;
import edu.mit.mitmobile2.maps.MapData;

public class BuildingMapItem extends MapItem {
	
	public static String TAG = "BuildingMapItem";
	public BuildingMapItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getCallout(final Context mContext, final MapData mapData, final int position) {

		Log.d(TAG,"BuildingMapItem getCallout");
		Log.d(TAG,"map item index = " + position);
		Log.d(TAG,"displayName = " + this.getItemData().get("displayName"));
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
	            	i.putExtra(MapBaseActivity.MAP_DATA_KEY, mapData.toJSON());
	            	i.putExtra(MapBaseActivity.MAP_ITEM_INDEX_KEY, position);
	            	mContext.startActivity(i);
	            }
	        });
		
		return calloutLayout;
	}

	@Override
	public View getCallout(Context mContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getCallout(Context mContext, MapData mapData) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public void initTimer(Context mContext) {
//		// TODO Auto-generated method stub
//		
//	}
	
}
