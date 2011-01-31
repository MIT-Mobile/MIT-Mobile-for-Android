package edu.mit.mitmobile2.maps;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.MapView.LayoutParams;
import edu.mit.mitmobile2.R;

public abstract class MITBaseItemizedOverlay extends ItemizedOverlay {
	
	protected Context mContext;

	protected MITMapView mapView;
	protected MapController mc;
	
	protected BalloonOverlayView balloonView;
	protected View clickableRegion;
	protected int mBubbleOffset = 0;
	
	protected ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	
	private int selectedIndex = -1;
	private boolean balloonVisible = false;
	public boolean balloonsEnabled = false;
	
	protected Paint textPaint;
	protected Paint textSmallPaint;
	protected Paint backPaint;
	protected Paint linePaint;

	
	public MITBaseItemizedOverlay(Drawable defaultMarker, Context context, MITMapView mv) {

		super(boundCenterBottom(defaultMarker));
		
		defaultMarker = boundCenterBottom(defaultMarker);
		
		mContext = context;
		
		this.mapView = mv;
		this.mc = mv.getController();
	
	    
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
		 
		 	if (!balloonsEnabled) return false;
		 
		 	mapView.tapped_overlay = true;
		 
		 	if (balloonView==null) balloonVisible = false;
		 	else if (!balloonView.isShown()) balloonVisible = false;
		 	
		 	if (selectedIndex == index) {
		        final PinItem item = (PinItem) mOverlays.get(index);
		 		if (!balloonVisible) {
		 			makeBalloon(item);
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
	 protected void makeBalloon(final PinItem p) {
		 
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
					
					 handleTap(p);
					 
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
	 
	 protected abstract void handleTap(PinItem p);
	 
	 
};
