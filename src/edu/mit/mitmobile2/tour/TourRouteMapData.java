package edu.mit.mitmobile2.tour;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MapData;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.tour.Tour.GeoPoint;
import edu.mit.mitmobile2.tour.Tour.SiteTourMapItem;
import edu.mit.mitmobile2.tour.Tour.TourMapItem;
import edu.mit.mitmobile2.tour.Tour.TourSiteStatus;

public class TourRouteMapData extends MapData {
	
	public interface OnTourSiteSelectedListener {
		public void onTourSiteSelected(TourMapItem tourMapItem);
	}
	
	OnTourSiteSelectedListener mSiteListener;
	
	public TourRouteMapData(List<? extends TourMapItem> tourMapItems, List<GeoPoint> geoPoints, OnTourSiteSelectedListener siteListener)  {
		// add the route
		TourRouteMapItem routeMapItem = new TourRouteMapItem();
		routeMapItem.setGeometryType(MapItem.TYPE_POLYLINE);
		for (GeoPoint geoPoint : geoPoints) {
			routeMapItem.getMapPoints().add(geoPoint.getMapPoint());
		}
		mapItems.add(routeMapItem);
		
		for (TourMapItem tourMapItem : tourMapItems) {
			TourStopMapItem mapItem = new TourStopMapItem(tourMapItem);
			mapItems.add(mapItem);
		}
		
		mSiteListener = siteListener;
	}

	public MapItem getMapItem(TourMapItem mapItem) {
		String id = mapItem.getId();
		for(MapItem aMapItem : mapItems) {
			if (aMapItem instanceof TourStopMapItem) {
				TourStopMapItem aTourStopMapItem = (TourStopMapItem) aMapItem;
				
				if (aTourStopMapItem.getID().equals(id)) {
					return aTourStopMapItem;
				}
			}
		}
		return null;
	}
	
	private class TourStopMapItem extends MapItem {

		private TourMapItem mTourMapItem;
		
		public TourStopMapItem(TourMapItem tourMapItem) {
			mTourMapItem = tourMapItem;
			
			mapPoints.add(mTourMapItem.getGeoPoint().getMapPoint());
			
			TourSiteStatus status = tourMapItem.getStatus();
			int markerId = -1;
			if(status == TourSiteStatus.VISITED) {
				markerId = R.drawable.map_past;
			} else if(status == TourSiteStatus.CURRENT) {
				markerId = R.drawable.map_currentstop;
			} else if(status == TourSiteStatus.FUTURE) {
				markerId = R.drawable.map_future;
			}
			setSymbol(markerId);
			setGeometryType(MapItem.TYPE_POINT);
			
		}
		
		public String getID() {
			return mTourMapItem.getId();
		}
		
		@Override
		public View getCallout(Context mContext) {

	   		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout calloutLayout = (LinearLayout) inflater.inflate(R.layout.tour_callout, null);

			TextView calloutBuildingName = (TextView)calloutLayout.findViewById(R.id.tour_callout_name);
			calloutBuildingName.setText(mTourMapItem.getTitle());
			
			calloutLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mSiteListener.onTourSiteSelected(mTourMapItem);					
				}
				
			});
			return calloutLayout;
		}
		
	}
	
	private static class TourRouteMapItem extends MapItem {

		@Override
		public View getCallout(Context mContext) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
