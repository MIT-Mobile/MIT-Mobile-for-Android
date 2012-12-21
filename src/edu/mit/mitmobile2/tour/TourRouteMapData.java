package edu.mit.mitmobile2.tour;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MapData;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.tour.Tour.GeoPoint;
import edu.mit.mitmobile2.tour.Tour.TourMapItem;
import edu.mit.mitmobile2.tour.Tour.TourSiteStatus;

public class TourRouteMapData extends MapData {
	
	public TourRouteMapData(List<? extends TourMapItem> tourMapItems, List<GeoPoint> geoPoints)  {
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
	}

	
	private static class TourStopMapItem extends MapItem {

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
		
		@Override
		public View getCallout(Context mContext) {

	   		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout calloutLayout = (LinearLayout) inflater.inflate(R.layout.tour_callout, null);

			TextView calloutBuildingName = (TextView)calloutLayout.findViewById(R.id.tour_callout_name);
			calloutBuildingName.setText(mTourMapItem.getTitle());
			
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
