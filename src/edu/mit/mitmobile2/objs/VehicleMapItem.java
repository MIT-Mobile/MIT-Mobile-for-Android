package edu.mit.mitmobile2.objs;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import edu.mit.mitmobile2.maps.MapAbstractionObject;
import edu.mit.mitmobile2.maps.MapData;

public class VehicleMapItem extends MapItem {
	
	public static String TAG = "VehicleMapItem";
	public VehicleMapItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getCallout(Context mContext) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public View getCallout(Context mContext, MapItem mapItem) {
		return null;
	}
	
	@Override
	public View getCallout(Context mContext, ArrayList<? extends MapItem> mapItems) {
		return null;
	}
	
	@Override
	public View getCallout(Context mContext, ArrayList<? extends MapItem> mapItems, int mapItemIndex) {
		return null;
	}

	private static int[] shuttleMarkers = {
    	                                   R.drawable.shuttle_location_n,
    	                                   R.drawable.shuttle_location_ne,
    	                                   R.drawable.shuttle_location_e,
    	                                   R.drawable.shuttle_location_se,
    	                                   R.drawable.shuttle_location_s,
    	                                   R.drawable.shuttle_location_sw,
    	                                   R.drawable.shuttle_location_w,
    	                                   R.drawable.shuttle_location_nw
    	                                  };
    
	public static int getShuttleMarkerForHeading(String heading) {
		int h = Integer.parseInt(heading);
    	return getShuttleMarkerForHeading(h);
	}

    public static int getShuttleMarkerForHeading(int heading) {
		int dir = heading / 45;
    	
    	if (dir==8) dir = 7; // not sure if 360 is allowed...
    	return shuttleMarkers[dir];	
	}

}
