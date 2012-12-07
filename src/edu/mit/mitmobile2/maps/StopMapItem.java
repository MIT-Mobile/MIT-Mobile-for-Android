package edu.mit.mitmobile2.maps;

import java.io.Serializable;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esri.android.map.Callout;
import com.esri.core.geometry.Point;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.R;

public class StopMapItem extends MapItem {
	
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

}
