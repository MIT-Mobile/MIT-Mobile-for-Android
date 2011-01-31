package edu.mit.mitmobile2.maps;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import edu.mit.mitmobile2.objs.RouteItem.Vehicle;

public class VehicleOverlayItem extends OverlayItem {

	public Vehicle v;
	
	public VehicleOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}

}
