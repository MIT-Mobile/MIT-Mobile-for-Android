package edu.mit.mitmobile.maps;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.google.android.maps.MapView.LayoutParams;
import edu.mit.mitmobile.R;
import edu.mit.mitmobile.objs.MapItem;
import edu.mit.mitmobile.objs.RouteItem.Stops;
import edu.mit.mitmobile.shuttles.MITStopsSliderActivity;
import edu.mit.mitmobile.shuttles.ShuttleModel;

public class MITItemizedOverlay extends ItemizedOverlay {
	
	protected Context mContext;

	protected MITMapView mapView;
	protected MapController mc;
	
	protected BalloonOverlayView balloonView;
	protected View clickableRegion;
	protected int mBubbleOffset = 0;
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	boolean shuttleMode = false;
	String shuttleRouteId = "";
	
	private int selectedIndex = -1;
	private boolean balloonVisible = false;

	protected boolean dotMode = false;
	protected Drawable dot,upcomingDot;
	protected Drawable defaultMarker;
	protected Drawable upcomingMarker;

	
	protected Paint textPaint;
	protected Paint textSmallPaint;
	protected Paint backPaint;
	protected Paint linePaint;
	
	
	public MITItemizedOverlay(Drawable defaultMarker, Context context, MITMapView mv) {

		super(boundCenterBottom(defaultMarker));
		
		defaultMarker = boundCenterBottom(defaultMarker);
		
		mContext = context;
		
		this.mapView = mv;
		this.mc = mv.getController();
		
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
	

	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add((PinItem) overlay);
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		 return mOverlays.size();
	}
	
	 @Override
	    protected boolean onTap(int index) {
		 
		 	mapView.tapped_overlay = true;
		 
		 	if (balloonView==null) balloonVisible = false;
		 	else if (!balloonView.isShown()) balloonVisible = false;
		 	
		 	if (selectedIndex == index) {

		        final PinItem item = (PinItem) mOverlays.get(index);
		      
		        
		 		if (!balloonVisible) {
		 			
		 			makeBalloon(item,index);

		 		}
		 		
		 		balloonVisible = true;
		 		
		 	} else {
	 			mapView.removeView(balloonView);
		 		balloonVisible = false;
		 	}
		 
		 	selectedIndex = index;

	        return true;
	        
	    }
	 
	 /**********************************************************************/
	 void makeBalloon(final PinItem p, final int index) {
		 

		 if (shuttleMode) {
			 if (dotMode) {
				 if (p.upcoming) mBubbleOffset = 7; 
				 else mBubbleOffset = 3;
			 } else {
				 mBubbleOffset = 45; 
			 }
		 } 
		 else mBubbleOffset = 33;
		 
		 
		 GeoPoint gp = p.getPoint();
		 
		 mapView.removeView(balloonView);
		    
		 balloonView = new BalloonOverlayView(mContext,mBubbleOffset);  
		 
		 clickableRegion = balloonView.findViewById(R.id.balloon_inner_layout);
		 
		 clickableRegion.setOnTouchListener(new OnTouchListener() {
			 @Override
			 public boolean onTouch(View v, MotionEvent event) {
				 View l =  ((View) v.getParent()).findViewById(R.id.balloon_main_layout); 
				 Drawable d = l.getBackground();
				 if (event.getAction() == MotionEvent.ACTION_DOWN) {
					 int[] states = {android.R.attr.state_pressed};
					 if (d.setState(states)) {
						 d.invalidateSelf();
					 }
					 return true;
				 } else if (event.getAction() == MotionEvent.ACTION_UP) {
					 int newStates[] = {};
					 if (d.setState(newStates)) {
						 d.invalidateSelf();
					 }
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
					 return true;
				 } else {
					 return false;
				 }
			 }
		 });

		 balloonView.setData(p);
				 
	     MapView.LayoutParams params = new MapView.LayoutParams(
	                     LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, gp,
	                     MapView.LayoutParams.BOTTOM_CENTER);
	     
	     params.mode = MapView.LayoutParams.MODE_MAP;
	     
	     balloonView.setVisibility(View.VISIBLE);
	
	     balloonView.setLayoutParams(params);
	    
	     mc.animateTo(gp); 

		 mapView.addView(balloonView);
         //mapView.addView(balloonView, params);
			
	 }
	 
	 
	 
};
