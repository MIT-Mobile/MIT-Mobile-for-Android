package edu.mit.mitmobile.maps;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Projection;
import edu.mit.mitmobile.R;

public class FixedMyLocation extends MyLocationOverlay {
	
	private boolean bugged = false;

	private MapView mView;
	private Paint accuracyPaint;
	private Point center;
	private Point left;
	private Drawable drawable;
	private int width;
	private int height;

	public boolean snapFirstTime = false;
	
	private static final int WEST_LONGITUDE_E6  = -71132698;
	private static final int EAST_LONGITUDE_E6  = -71006698;
	private static final int NORTH_LATITUDE_E6  =  42407741;
	private static final int SOUTH_LATITUDE_E6  =  42331392;
	
	@Override
	public synchronized void onLocationChanged(Location location) {
		
		super.onLocationChanged(location);
		
		if (snapFirstTime) {
			MapController mc = mView.getController();
			double lat = location.getLatitude() *1000000;
			double lon = location.getLongitude() *1000000;
			GeoPoint gp = new GeoPoint((int)lat,(int)lon);
			
			//http://mobile-dev.mit.edu/~sonya/martinez/api/map/?f=json
			//"initialExtent":{"xmin":-7917385.7999173,"ymin":5212844.4790179,"xmax":-7910779.1236727,"ymax":5217229.6735046,"spatialReference":{"wkid":102113}},
			//"fullExtent":{"xmin":-7920689.3209994,"ymin":5211048.1193302,"xmax":-7907475.6025907,"ymax":5219026.0331923,
			//if ((lat>5212844)&&(lat<5217229)&&(lon>-7917385)&&(lon<-7910779)) {
			if ((lat>SOUTH_LATITUDE_E6)&&(lat<NORTH_LATITUDE_E6)&&(lon>WEST_LONGITUDE_E6)&&(lon<EAST_LONGITUDE_E6)) {
				mc.setZoom(MITMapActivity.INIT_ZOOM_ONE_ITEM);
				mc.setCenter(gp);	
			}

			snapFirstTime = false;
		}
		
	}

	public FixedMyLocation(Context context, MapView mapView) {
		super(context, mapView);
		mView = mapView;
	}

	@Override
	protected void drawMyLocation(Canvas canvas, MapView mapView, Location lastFix, GeoPoint myLoc, long when) {
		if (!bugged) {
			try {
				super.drawMyLocation(canvas, mapView, lastFix, myLoc, when);
			} catch (Exception e) {
				bugged = true;
			}
		}

		if (bugged) {
			if (drawable == null) {
				accuracyPaint = new Paint();
				accuracyPaint.setAntiAlias(true);
				accuracyPaint.setStrokeWidth(2.0f);
				
				drawable = mapView.getContext().getResources().getDrawable(R.drawable.mylocation);
				width = drawable.getIntrinsicWidth();
				height = drawable.getIntrinsicHeight();
				center = new Point();
				left = new Point();
			}
			Projection projection = mapView.getProjection();
			
			double latitude = lastFix.getLatitude();
			double longitude = lastFix.getLongitude();
			float accuracy = lastFix.getAccuracy();
			
			float[] result = new float[1];

			Location.distanceBetween(latitude, longitude, latitude, longitude + 1, result);
			float longitudeLineDistance = result[0];

			GeoPoint leftGeo = new GeoPoint((int)(latitude*1e6), (int)((longitude-accuracy/longitudeLineDistance)*1e6));
			projection.toPixels(leftGeo, left);
			projection.toPixels(myLoc, center);
			int radius = center.x - left.x;
			
			accuracyPaint.setColor(0xff6666ff);
			accuracyPaint.setStyle(Style.STROKE);
			canvas.drawCircle(center.x, center.y, radius, accuracyPaint);

			accuracyPaint.setColor(0x186666ff);
			accuracyPaint.setStyle(Style.FILL);
			canvas.drawCircle(center.x, center.y, radius, accuracyPaint);
						
			drawable.setBounds(center.x - width / 2, center.y - height / 2, center.x + width / 2, center.y + height / 2);
			drawable.draw(canvas);
		}
	}

}
