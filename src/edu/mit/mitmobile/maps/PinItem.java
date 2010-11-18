package edu.mit.mitmobile.maps;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class PinItem extends OverlayItem {

	public boolean selected = false;  // used for bubble...
	
	public boolean twoLines = false;
	public boolean upcoming = false;
	
	ArrayList<GeoPoint> detailed_path;
	
	private Object mUserData;
	
	public PinItem(GeoPoint point, String title, String snippet, Object userData) {
		super(point, title, snippet);
		detailed_path = new ArrayList<GeoPoint>();
		mUserData = userData;
	}
	
	public Object getUserData() {
		return mUserData;
	}

}
