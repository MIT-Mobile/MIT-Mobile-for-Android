package edu.mit.mitmobile2.tour;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import edu.mit.mitmobile2.maps.MapData;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.tour.Tour.GeoPoint;

public class TourRouteMapData extends MapData {
	
	public TourRouteMapData(List<GeoPoint> geoPoints) {
		// add the route
		TourRouteMapItem routeMapItem = new TourRouteMapItem();
		routeMapItem.setGeometryType(MapItem.TYPE_POLYLINE);
		for (GeoPoint geoPoint : geoPoints) {
			routeMapItem.getMapPoints().add(geoPoint.getMapPoint());
		}
		getMapItems().add(routeMapItem);
	}
	
	private static class TourRouteMapItem extends MapItem {
		@Override
		public View getCallout(Context mContext) {
			return null;
		}

		@Override
		public View getCallout(Context mContext,  ArrayList<? extends MapItem> mapItems) {
			return null;
		}

		@Override
		public View getCallout(Context mContext,  ArrayList<? extends MapItem> mapItems, int position) {
			return null;
		}
	}
}
