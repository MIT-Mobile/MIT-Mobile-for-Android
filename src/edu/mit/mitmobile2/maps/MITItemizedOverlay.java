package edu.mit.mitmobile2.maps;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import com.google.android.maps.MapView.LayoutParams;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.RouteItem.Stops;
import edu.mit.mitmobile2.shuttles.MITStopsSliderActivity;
import edu.mit.mitmobile2.shuttles.ShuttleModel;

public class MITItemizedOverlay extends MITBaseItemizedOverlay {
	

	boolean shuttleMode = false;
	String shuttleRouteId = "";
	

	protected boolean dotMode = false;
	protected Drawable dot,upcomingDot;
	protected Drawable defaultMarker;
	protected Drawable upcomingMarker;

	
	
	public MITItemizedOverlay(Drawable defaultMarker, Context context, MITMapView mv) {

		super(defaultMarker,context,mv);
		
		
		balloonsEnabled = true;
		
        textPaint = new Paint(); 
        textPaint.setARGB(255, 0, 0, 0);
        textPaint.setAntiAlias(true); 
        textPaint.setFakeBoldText(true); 
        float textSize = textPaint.getTextSize();
        textPaint.setTextSize(textSize*1.5f);

        textSmallPaint = new Paint(); 
        textSmallPaint.setARGB(255, 102, 102, 102);
        textSmallPaint.setAntiAlias(true); 
        
        backPaint = new Paint(); 
        backPaint.setARGB(164, 255, 255, 255); 
        backPaint.setAntiAlias(true); 
        
	    linePaint = new Paint(); 
	    linePaint.setARGB(204, 204, 0, 0);  //  CCCC0000
        linePaint.setStyle(Style.STROKE);
	    linePaint.setStrokeWidth(3);  // 0 is special
	    //linePaint.setAlpha(120); 

	    Resources res = mContext.getResources();
	    
	    dot = res.getDrawable(R.drawable.shuttle_stop_dot);
	    dot = boundCenter(dot);
	    upcomingDot = res.getDrawable(R.drawable.shuttle_stop_dot_next);
	    upcomingDot = boundCenter(upcomingDot);
	    
	    upcomingMarker = res.getDrawable(R.drawable.map_pin_shuttle_stop_complete_next);
	    upcomingMarker = boundCenterBottom(upcomingMarker);
	    
	    
	}

	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
	
		Projection p = mapView.getProjection();

	    Point pt = new Point();

        PinItem item;
       

		int size = mOverlays.size();
	    if(size == 0) return;

		if (shuttleMode) {

	        // Set markers to dots if zoomed out...
			
			int zLevel = mapView.getZoomLevel();
			boolean useDots = false;
			if (zLevel<16) useDots = true;
			if (zLevel>17) linePaint.setStrokeWidth(4); 
			else linePaint.setStrokeWidth(3); 

			Log.d("MITMapView","zoom="+zLevel);

			
			// #1
			
			for (OverlayItem o : mOverlays) {
				item = (PinItem) o;
				if (useDots) {
					if (item.upcoming) o.setMarker(upcomingDot);
					else o.setMarker(dot);
				} else {
					if (item.upcoming) o.setMarker(upcomingMarker);
					else o.setMarker(defaultMarker);
				}
			}
			
			// #2
			/*
			//if (useDots^dotMode) {
			if ((useDots&&!dotMode)||(!useDots&&dotMode)) {
				for (OverlayItem o : mOverlays) {
					item = (PinItem) o;
					if (useDots) {
						if (item.upcoming) o.setMarker(upcomingDot);
						else o.setMarker(dot);
					} else {
						if (item.upcoming) {
							o.setMarker(upcomingMarker);
						}
						else o.setMarker(defaultMarker);
					}
				}
			}
			dotMode = useDots;
	        */
			
	        // Connect the markers

	        
	        item = (PinItem) mOverlays.get(0);
		    if (item.detailed_path.size()>0) {
			    Path path = new Path();
			    boolean first = true;
		        for (GeoPoint g : item.detailed_path) {
		        	p.toPixels(g, pt);
		        	if (first) {
		        		first = false;
		        		path.moveTo(pt.x, pt.y);
		        	}
		        	else path.lineTo(pt.x,pt.y);
		        }
		        canvas.drawPath(path, linePaint);
		    }
	       
			
		}

		super.draw(canvas, mapView, shadow);  // this draws shadows so do last...
   
		
	}
	
	 /**********************************************************************/
	@Override
	 protected void makeBalloon(final PinItem p) {
		 
		 if (shuttleMode) {
			 if (dotMode) {
				 if (p.upcoming) mBubbleOffset = 7; 
				 else mBubbleOffset = 3;
			 } else {
				 mBubbleOffset = 45; 
			 }
		 } 
		 else mBubbleOffset = 33;
		 
		 super.makeBalloon(p);
			
	 }


	 /**********************************************************************/
	@Override
	protected void handleTap(PinItem p) {
	
		if (shuttleMode) {
			 Intent i = new Intent(mContext, MITStopsSliderActivity.class);
			 i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);  
			 i.putExtra(ShuttleModel.KEY_ROUTE_ID, shuttleRouteId);
			 Stops stop = (Stops) p.getUserData();
			 i.putExtra(ShuttleModel.KEY_STOP_ID, stop.id);
			 mContext.startActivity(i);
		 } else if (p.getUserData() != null) {
			ArrayList<MapItem> mapItems = new ArrayList<MapItem>();
			mapItems.add((MapItem) p.getUserData());
			MITMapDetailsSliderActivity.launchMapDetails(mContext, mapItems, 0);
		 }
		
	}
	 
	 
	 
};
