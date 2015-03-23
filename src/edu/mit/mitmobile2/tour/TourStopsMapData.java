package edu.mit.mitmobile2.tour;

import java.util.ArrayList;
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
import edu.mit.mitmobile2.tour.Tour.TourMapItem;
import edu.mit.mitmobile2.tour.Tour.TourSiteStatus;

public class TourStopsMapData extends MapData {
	
	public interface OnTourSiteSelectedListener {
		public void onTourSiteSelected(TourMapItem tourMapItem);
	}
	
	OnTourSiteSelectedListener mSiteListener;
	
	public TourStopsMapData(List<? extends TourMapItem> tourMapItems, OnTourSiteSelectedListener siteListener)  {		
		for (TourMapItem tourMapItem : tourMapItems) {
			TourStopMapItem mapItem = new TourStopMapItem(tourMapItem);
			getMapItems().add(mapItem);
		}
		
		mSiteListener = siteListener;
	}

	public MapItem getMapItem(TourMapItem mapItem) {
		String id = mapItem.getId();
		for(MapItem aMapItem : getMapItems()) {
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
			
			verticalAlign = MapItem.VALIGN_CENTER;
			horizontalAlign = MapItem.ALIGN_CENTER;
			
		}
		
		public String getID() {
			return mTourMapItem.getId();
		}
		
		@Override
		public View getCallout(Context mContext) {

	   		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout calloutLayout = (LinearLayout) inflater.inflate(R.layout.tour_callout, null);

			TextView calloutStopName = (TextView)calloutLayout.findViewById(R.id.tour_callout_name);
			calloutStopName.setText(mTourMapItem.getTitle());
	
			TextView calloutStopDistance = (TextView)calloutLayout.findViewById(R.id.tour_callout_distance);			
			String distanceString = null;
			Float meters = mTourMapItem.distance();
			if (meters != null) {
				distanceString = LocaleMeasurements.getDistance(meters);
			}
			if (distanceString != null) {
				calloutStopDistance.setVisibility(View.VISIBLE);
				calloutStopDistance.setText(distanceString);
			}
			
			calloutLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mSiteListener.onTourSiteSelected(mTourMapItem);					
				}
				
			});
			return calloutLayout;
		}

		@Override
		public View getCallout(Context mContext, ArrayList<? extends MapItem> mapItems) {
			return null;
		}

		@Override
		public View getCallout(Context mContext,  ArrayList<? extends MapItem> mapItems, int position) {
			TourStopMapItem tourStopMapItem = (TourStopMapItem) mapItems.get(position);
			return tourStopMapItem.getCallout(mContext);
		}

		@Override
		public View getCallout(Context mContext, MapItem mapItem) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
