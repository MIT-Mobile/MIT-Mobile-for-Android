package edu.mit.mitmobile.maps;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import edu.mit.mitmobile.objs.RouteItem.Vehicle;

public class VehicleOverlayItem extends OverlayItem {

	public Vehicle v;
	
	public VehicleOverlayItem(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}

}
