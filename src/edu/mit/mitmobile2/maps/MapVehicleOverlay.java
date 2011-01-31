package edu.mit.mitmobile2.maps;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import edu.mit.mitmobile2.R;

public class MapVehicleOverlay extends ItemizedOverlay {

	protected Drawable[] shuttleMarkers;

	public ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	
	
	public MapVehicleOverlay(Drawable defaultMarker, Context ctx) {
		
		super(boundCenterBottom(defaultMarker));

	    Resources res = ctx.getResources();
	    
	    shuttleMarkers = new Drawable[8];
	    shuttleMarkers[0] = res.getDrawable(R.drawable.shuttle_location_n);
	    shuttleMarkers[0] = boundCenterBottom(shuttleMarkers[0]);
	    shuttleMarkers[1] = res.getDrawable(R.drawable.shuttle_location_ne);
	    shuttleMarkers[1] = boundCenterBottom(shuttleMarkers[1]);
	    shuttleMarkers[2] = res.getDrawable(R.drawable.shuttle_location_e);
	    shuttleMarkers[2] = boundCenterBottom(shuttleMarkers[2]);
	    shuttleMarkers[3] = res.getDrawable(R.drawable.shuttle_location_se);
	    shuttleMarkers[3] = boundCenterBottom(shuttleMarkers[3]);
	    shuttleMarkers[4] = res.getDrawable(R.drawable.shuttle_location_s);
	    shuttleMarkers[4] = boundCenterBottom(shuttleMarkers[4]);
	    shuttleMarkers[5] = res.getDrawable(R.drawable.shuttle_location_sw);
	    shuttleMarkers[5] = boundCenterBottom(shuttleMarkers[5]);
	    shuttleMarkers[6] = res.getDrawable(R.drawable.shuttle_location_w);
	    shuttleMarkers[6] = boundCenterBottom(shuttleMarkers[6]);
	    shuttleMarkers[7] = res.getDrawable(R.drawable.shuttle_location_nw);
	    shuttleMarkers[7] = boundCenterBottom(shuttleMarkers[7]);

	    
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {

        int dir;
        VehicleOverlayItem voi;
        
        for (OverlayItem o : mOverlays) {

        	voi = (VehicleOverlayItem) o;
	        
        	dir = voi.v.heading / 45;
        	
        	if (dir==8) dir = 7;  // not sure if 360 is allowed...
     
        	voi.setMarker(shuttleMarkers[dir]);
        	  
        }
        
        super.draw(canvas, mapView, shadow);  // this draws shadows so do last...
        
	}

	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add((VehicleOverlayItem) overlay);
	    populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		OverlayItem x = mOverlays.get(i);
		if (x==null) {
			Log.e("","x null");
		}
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		 return mOverlays.size();
	}
	

}
