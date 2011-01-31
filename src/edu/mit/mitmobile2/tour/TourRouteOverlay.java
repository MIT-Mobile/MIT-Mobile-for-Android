package edu.mit.mitmobile2.tour;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MITBaseItemizedOverlay;
import edu.mit.mitmobile2.maps.MITMapView;
import edu.mit.mitmobile2.maps.PinItem;
import edu.mit.mitmobile2.tour.Tour.ParcelableGeoPoint;
import edu.mit.mitmobile2.tour.Tour.TourMapItem;
import edu.mit.mitmobile2.tour.Tour.TourSiteStatus;

public class TourRouteOverlay extends MITBaseItemizedOverlay {

	List<ParcelableGeoPoint> mGeoPoints;
	Paint mLinePaint;
	
	public interface OnTourItemSelectedListener {
		public void onTourItemSelected(TourMapItem tourItem);
	}
	
	OnTourItemSelectedListener mItemSelectedListener;
	
	public TourRouteOverlay(Context context, MITMapView mapView, List<TourMapItem> tourMapItems, List<ParcelableGeoPoint> geoPoints) {
		super(defaultMarker(context), context, mapView);
		
		mGeoPoints = geoPoints;

		// configure the path width/color of the tour route line
		mLinePaint = new Paint();
		mLinePaint.setColor(mContext.getResources().getColor(R.color.tourPathColor));
		float lineWidth = mContext.getResources().getDimension(R.dimen.tourPathWidth);
		mLinePaint.setStrokeWidth(lineWidth);
		mLinePaint.setStyle(Paint.Style.STROKE);
		
		for(TourMapItem tourMapItem : tourMapItems) {
			OverlayItem item = new TourSitePinItem(tourMapItem.getGeoPoint(), tourMapItem.getTitle(), tourMapItem);
			
			TourSiteStatus status = tourMapItem.getStatus();
			int markerId = -1;
			if(status == TourSiteStatus.VISITED) {
				markerId = R.drawable.map_past;
			} else if(status == TourSiteStatus.CURRENT) {
				markerId = R.drawable.map_currentstop;
			} else if(status == TourSiteStatus.FUTURE) {
				markerId = R.drawable.map_future;
			}
			item.setMarker(boundCenter(context.getResources().getDrawable(markerId)));
	
			addOverlay(item);
		}
		
		balloonsEnabled = true;
	}

    private class TourSitePinItem extends PinItem {

		public TourSitePinItem(GeoPoint point, String title, TourMapItem mapItem) {
			super(point, title, null, mapItem);
		}
		
		@Override
		public String getSnippet() {
			TourMapItem mapItem = (TourMapItem) getUserData();
			if(mapItem.distance() == null) {
				return null;
			}
			
			return LocaleMeasurements.getDistance(mapItem.distance());
		}
    	
    }
    
	public void showBalloon(TourMapItem mapItem) {
		makeBalloon(new TourSitePinItem(mapItem.getGeoPoint(), mapItem.getTitle(), mapItem));
	}
	
	public void setOnTourItemSelectedListener(OnTourItemSelectedListener itemSelectedListener) {
		 mItemSelectedListener = itemSelectedListener;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if(!shadow) {
			
			// first draw the path of the tour
			Path path = new Path();
			Point start = new Point();
			mapView.getProjection().toPixels(mGeoPoints.get(0), start);
			path.moveTo(start.x, start.y);
			
			for(int i=1; i < mGeoPoints.size(); i++) {
				Point next = new Point();
				mapView.getProjection().toPixels(mGeoPoints.get(i), next);
				path.lineTo(next.x, next.y);
			}
			
			canvas.drawPath(path, mLinePaint);
			
			// now draw the markers for each stop, this is done automatically by the super class
			super.draw(canvas, mapView, shadow); 
		}
	}
	
	
	@Override
	protected void handleTap(PinItem p) {
		TourMapItem tourMapItem = (TourMapItem) p.getUserData();
		mItemSelectedListener.onTourItemSelected(tourMapItem);
	}
	
	private static Drawable defaultMarker(Context context) {
		return context.getResources().getDrawable(R.drawable.map_currentstop);
	}

}
