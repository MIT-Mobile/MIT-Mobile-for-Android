package edu.mit.mitmobile2.maps;

import java.util.List;

import com.google.android.maps.GeoPoint;

public class GeoRect {
	
	private int mMinLongitudeE6;
	private int mMaxLongitudeE6;

	private int mMinLatitudeE6;
	private int mMaxLatitudeE6;
	
	public GeoRect(List<? extends GeoPoint> geoPoints) {
		
		mMaxLongitudeE6 = geoPoints.get(0).getLongitudeE6();
		mMinLongitudeE6 = geoPoints.get(0).getLongitudeE6();
		mMaxLatitudeE6 = geoPoints.get(0).getLatitudeE6();
		mMinLatitudeE6 = geoPoints.get(0).getLatitudeE6();
		
		for(GeoPoint geoPoint : geoPoints) {
			if(geoPoint.getLongitudeE6() > mMaxLongitudeE6) {
				mMaxLongitudeE6 = geoPoint.getLongitudeE6();
			}
			
			if(geoPoint.getLongitudeE6() < mMinLongitudeE6) {
				mMinLongitudeE6 = geoPoint.getLongitudeE6();
			}
			
			if(geoPoint.getLatitudeE6() > mMaxLatitudeE6) {
				mMaxLatitudeE6 = geoPoint.getLatitudeE6();
			}
			
			if(geoPoint.getLatitudeE6() < mMinLatitudeE6) {
				mMinLatitudeE6 = geoPoint.getLatitudeE6();
			}
		}
	}

	public int getLongitudeSpanE6() {
		return mMaxLongitudeE6 - mMinLongitudeE6;
	}
	
	public int getLatitudeSpanE6() {
		return mMaxLatitudeE6 - mMinLatitudeE6;
	}
	
	public GeoPoint getCenter() {
		return new GeoPoint((mMaxLatitudeE6 + mMinLatitudeE6)/2, (mMaxLongitudeE6 + mMinLongitudeE6)/2);
	}
	
	public int getMaxLongitudeE6() {
		return mMaxLongitudeE6;
	}
	
	public int getMinLongitudeE6() {
		return mMinLongitudeE6;
	}
	
	public int getMaxLatitudeE6() {
		return mMaxLatitudeE6;
	}
	
	public int getMinLatitudeE6() {
		return mMinLatitudeE6;
	}
}
