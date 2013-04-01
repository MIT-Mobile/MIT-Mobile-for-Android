package edu.mit.mitmobile2.tour;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.MITPlainSecondaryTitleBar;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.RemoteImageView;
import edu.mit.mitmobile2.TitleBarSwitch;
import edu.mit.mitmobile2.TitleBarSwitch.OnToggledListener;
import edu.mit.mitmobile2.maps.MITMapView;
import edu.mit.mitmobile2.maps.MITMapActivity;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.tour.Tour.GeoPoint;
import edu.mit.mitmobile2.tour.Tour.SideTripTourMapItem;
import edu.mit.mitmobile2.tour.Tour.SiteTourMapItem;
import edu.mit.mitmobile2.tour.Tour.TourMapItem;
import edu.mit.mitmobile2.tour.Tour.TourSiteStatus;
import edu.mit.mitmobile2.tour.Tour.TourMapItem.LocationSupplier;
import edu.mit.mitmobile2.tour.TourRouteMapData.OnTourSiteSelectedListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class TourMapActivity extends NewModuleActivity implements OnTourSiteSelectedListener {
	
	private static final String TOUR_STOPS_KEY = "tour_stops";
	private static final String TOUR_PATH_KEY = "tour_path";
	private static final String TOUR_ACTIVE_KEY = "tour_active";
	
	private static String LIST = "List";
	private static String MAP = "Map";
	
	ListView mTourListView;
	TitleBarSwitch mMapListSwitch;
	View mMapLegend;
	private MITMapView mMapView;
	TourStartHelpActionRow mStartHelpActionRow;
	boolean mTourActive;
	int mTourCurrentPosition;
	List<SiteTourMapItem> mSiteTourMapItems;
	ArrayList<TourMapItem> mTourMapItems = new ArrayList<TourMapItem>();
	LocationManager mLocationManager;
	String mBestLocationProviderName;
	String mWorstLocationProviderName;
	Long mShowClosestBalloonInitialTime;
	
	private TourItemAdapter mTourListAdapter;
	private ArrayList<GeoPoint> mGeoPoints;
	private MITPlainSecondaryTitleBar mSecondaryTitleBar;
	private TourRouteMapData mMapData;
	
	private static int HELP_SELECT_STOP = 2;
	
	public static void launch(Context context, ArrayList<TourMapItem> tourItems, ArrayList<GeoPoint> geoPoints, boolean tourActive) {
		Intent intent = new Intent(context, TourMapActivity.class);
		intent.putParcelableArrayListExtra(TOUR_STOPS_KEY, tourItems);
		intent.putParcelableArrayListExtra(TOUR_PATH_KEY, geoPoints);
		intent.putExtra(TOUR_ACTIVE_KEY, tourActive);
		context.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		
		Intent i = getIntent();
		
		setContentView(R.layout.tour_map);
		
		mMapView = (MITMapView) findViewById(R.id.tourMapView);
		
		mSiteTourMapItems = i.getParcelableArrayListExtra(TOUR_STOPS_KEY);
		mGeoPoints = i.getParcelableArrayListExtra(TOUR_PATH_KEY);
		mTourActive = i.getBooleanExtra(TOUR_ACTIVE_KEY, false);
		mTourCurrentPosition = getCurrentPosition();
				
		// be default show sidetrips in list if tour not yet active
		mShowingSidetrips = !mTourActive;
		setTourItemsList(mShowingSidetrips);
		
		//setOverlays();
		
		//mapView.getOverlays().add(mSiteMarkers);
		
		//mapView.getController().setCenter(geoRect.getCenter());
		
		mTourListView = (ListView) findViewById(R.id.mapListView);
		mTourListAdapter = new TourItemAdapter(this, mTourMapItems);
		mTourListView.setAdapter(mTourListAdapter);
		mTourListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TourMapItem tourMapItem = mTourMapItems.get(position);
				if(tourMapItem.getClass() == SiteTourMapItem.class) {
					SiteTourMapItem siteItem = (SiteTourMapItem) tourMapItem;
					launchTour(siteItem);	
				} else if(tourMapItem.getClass() == SideTripTourMapItem.class) {
					SideTripTourMapItem sidetripItem = (SideTripTourMapItem) tourMapItem;
					TourSideTripActivity.launch(
						TourMapActivity.this, sidetripItem.getParent().getSiteGuid(), 
						sidetripItem.getSideTripId(), sidetripItem.isOnSideTrip());
				}
			}
		});
		
		mMapData = new TourRouteMapData(mSiteTourMapItems, mGeoPoints, this);
		mMapView.addMapItems(mMapData.getMapItems());
		mMapView.fitMapItems();

		
		mSecondaryTitleBar = new MITPlainSecondaryTitleBar(this);
		mMapListSwitch = new TitleBarSwitch(this);
		mMapListSwitch.setLabels(MAP, LIST);
		mMapListSwitch.setSelected(MAP);
		mMapListSwitch.setOnToggledListener(new OnToggledListener() {
			@Override
			public void onToggled(String selected) {
				toggleMapList(selected);
			}
		});
		getTitleBar().addSecondaryBar(mSecondaryTitleBar);
		
		mStartHelpActionRow = (TourStartHelpActionRow) findViewById(R.id.tourMapStartHelp);
		mStartHelpActionRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TourMapActivity.this, TourStartHelpActivity.class);
				startActivityForResult(intent, HELP_SELECT_STOP);				
			}
		});
		
		mMapLegend = findViewById(R.id.tourMapLegend);
		
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria fineCriteria = new Criteria();
		fineCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		mBestLocationProviderName = mLocationManager.getBestProvider(fineCriteria, true);
		
		Criteria criteria = new Criteria();
		mWorstLocationProviderName = mLocationManager.getBestProvider(criteria, true);
		
		// set the locations manager for tour map items (this allows the tour map items to calculate there distance from user)
		for(TourMapItem mapItem : mSiteTourMapItems) {
			mapItem.setLocationSupplier(mLocationSupplier);
		}
		
		if(mTourActive) {
			mSecondaryTitleBar.setTitle("Tour Overview");
			displayCallout(mSiteTourMapItems.get(mTourCurrentPosition));
		} else {
			
			// tour not active, show information to help the user know where to start
			mShowClosestBalloonInitialTime = System.currentTimeMillis();		
			mStartHelpActionRow.setVisibility(View.VISIBLE);
			mSecondaryTitleBar.setTitle("Select a Starting Point");
			mMapLegend.setVisibility(View.GONE);
		}
		
		mSecondaryTitleBar.addActionView(mMapListSwitch);
	}
	
	@Override
	public void onTourSiteSelected(TourMapItem tourMapItem) {
		if(tourMapItem.getClass() == SiteTourMapItem.class) {
			SiteTourMapItem siteItem = (SiteTourMapItem) tourMapItem;
			launchTour(siteItem);
		}
	}
	
	/*
	@Override 
	protected void onMapLoaded() {
		mMapData = new TourRouteMapData(mSiteTourMapItems, mGeoPoints, this);
		//map.setMapData(mMapData);
		//map.processMapData();
		
		mSecondaryTitleBar.addActionView(mMapListSwitch);
	}
	*/
	
	MapActivityLocationSupplier mLocationSupplier = new MapActivityLocationSupplier();
	private class MapActivityLocationSupplier implements LocationSupplier {
		Location mLocation;
		
		public void setLocation(Location location) {
			if(location.getProvider().equals(mBestLocationProviderName) ||
				mLocation == null ||
				mLocation.getProvider().equals(mWorstLocationProviderName)) {
				
				// only update the location if it comes from the best provider, or the old data is not very good.
				mLocation = location;
			}
		}
		
		@Override
		public Location getLocation() {
			if (mLocation != null) {
				return mLocation;
			} else {
				String provider = mBestLocationProviderName;
				if (provider == null) {
					provider = mWorstLocationProviderName;
				}
				if (provider != null) {
					return mLocationManager.getLastKnownLocation(provider);
				} else {
					return null;
				}
			}
		}
	};
	
	LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			mLocationSupplier.setLocation(location);
			if(mShowClosestBalloonInitialTime != null) {
				// do not wait more than 7 seconds for the initial fix
				if(System.currentTimeMillis() - mShowClosestBalloonInitialTime < 7 * 1000) {
					if(showClosestSiteBalloon(location)) {
						mShowClosestBalloonInitialTime = null;
					}
				} else {
					mShowClosestBalloonInitialTime = null;
				}
			}
			mTourListAdapter.notifyDataSetChanged();
		}

		@Override
		public void onProviderDisabled(String provider) {} 

		@Override
		public void onProviderEnabled(String provider) {} 

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}		
	};
	
	@Override
	public void onResume() {
		super.onResume();
		if(mBestLocationProviderName != null) {
			mLocationManager.requestLocationUpdates(mBestLocationProviderName, 5 * 1000, 0, mLocationListener);
		}
		
		if(mWorstLocationProviderName != null) {
			mLocationManager.requestLocationUpdates(mWorstLocationProviderName, 5 * 1000, 0, mLocationListener);
		}
	}
	
	@Override 
	public void onPause() {
		super.onPause();
		mLocationManager.removeUpdates(mLocationListener);
	}
	
	private boolean showClosestSiteBalloon(Location location) {
		// search for closest site
		// find the two closest sites (see if one is significantly closer than the other)
		// and compare the closeness to the accuracy of the location fix (to see if we trust which one is closer)
		TourMapItem closest = null;
		TourMapItem secondClosest = null;

		for(TourMapItem mapItem : mSiteTourMapItems) {
			if(closest == null) {
				closest = mapItem;
			} else if(mapItem.distance() < closest.distance()) {
				secondClosest = closest;
				closest = mapItem;
			} else if( (secondClosest == null) || (mapItem.distance() < secondClosest.distance()) ) {
				secondClosest = mapItem;
			}
		}
				
		// check if the accuracy of the location is sufficient to distinguish 
		// between the closest and second closest location
		if(closest.distanceBetween(secondClosest) < location.getAccuracy()) {
			return false;
		}
		
		// check to see if we are so far away that we rather use the default first stop
		// dont show closest if we are further than 2km
		if(closest.distance() > 2000) {
			//mSiteMarkers.showBalloon(mSiteTourMapItems.get(0));
			return true;
		}
				
		// now that we have survived all the sanity checks actually show the balloon
		// for the closest tour site;
		//mSiteMarkers.showBalloon(closest);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
		if(requestCode == HELP_SELECT_STOP && resultCode == RESULT_OK) {
			TourMapItem item = resultIntent.getParcelableExtra(TourStartHelpActivity.SELECTED_SITE);
			item.setLocationSupplier(mLocationSupplier);
			displayCallout(item);
		}
	}
	
	private void displayCallout(TourMapItem tourMapItem) {
		if (mMapData != null) {
			MapItem mapItem = mMapData.getMapItem(tourMapItem);
			mMapView.displayCallout(this, mapItem);
		}		
	}
	
	private int getCurrentPosition() {
		for(int i = 0; i < mSiteTourMapItems.size(); i++) {
			TourMapItem item = mSiteTourMapItems.get(i);
			if(item.getStatus() == TourSiteStatus.CURRENT) {
				return i;
			}
		}
		return -1;
	}
	
	private int getTourItemPosition(String siteGuid) {
		for(int i = 0; i < mSiteTourMapItems.size(); i++) {
			SiteTourMapItem item = mSiteTourMapItems.get(i);
			if(item.getSiteGuid().equals(siteGuid)) {
				return i;
			}
		}
		return -1;
	}
	
	void launchTour(final SiteTourMapItem tourItem) {
		int position = getTourItemPosition(tourItem.getSiteGuid());
		
		if(mTourActive) {
			if(position == mTourCurrentPosition) {
				TourStopSliderActivity.launchFromSiteGuid(this, tourItem.getSiteGuid(), false);
			} else if(position == mTourCurrentPosition + 1) {
				TourStopSliderActivity.launchFromSiteGuid(this, tourItem.getSiteGuid(), true);
			} else {
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
				alertBuilder.setTitle("Skip Notification");
				alertBuilder.setNegativeButton("No", null);
				alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						TourStopSliderActivity.launchFromSiteGuid(TourMapActivity.this, tourItem.getSiteGuid(), false);	
					}
				});
				
				String message;
				if(position > mTourCurrentPosition) {
					message = "Are you sure you want to skip " + (position-mTourCurrentPosition) + " stops?";
				} else {
					message = "Are you sure you want to go back " + (mTourCurrentPosition-position) + " stops?";
				}
				alertBuilder.setMessage(message);
				alertBuilder.create().show();
			}
		} else {
			TourStopSliderActivity.launchFromSiteGuid(this, tourItem.getSiteGuid(), false);
		}
	}
	
	private boolean isListVisible() {
		if (mTourListView != null) {
			return (mTourListView.getVisibility() == View.VISIBLE);
		}
		return false;
	}
	
	@Override
	protected List<MITMenuItem> getSecondaryMenuItems() {
	    ArrayList<MITMenuItem> items = new ArrayList<MITMenuItem>();
	    if (isListVisible()) {
	    	if (!mShowingSidetrips) {
	    		items.add(new MITMenuItem("showsidetrips", "Show Side Trips"));
	    	} else {
	    		items.add(new MITMenuItem("hidesidetrips", "Hide Side Trips"));
	    	}
	    }
	    return items;
	}
	
	@Override
	protected void onOptionSelected(String optionId) {
	    if (optionId.equals("showsidetrips") || optionId.equals("hidesidetrips")) {
	    	showOrHideSidetrips(optionId);
	    }
	}
	
	private void toggleMapList(String selected) {
		if(selected.equals(LIST)) {
			mTourListView.setVisibility(View.VISIBLE);
			mMapView.setVisibility(View.GONE);
			
			if(mTourActive) {
				mMapLegend.setVisibility(View.GONE);
			} else {
				mStartHelpActionRow.setVisibility(View.GONE);
			}
		} else if(selected.equals(MAP)) {
			mTourListView.setVisibility(View.GONE);
			mMapView.setVisibility(View.VISIBLE);
			
			if(mTourActive) {
				mMapLegend.setVisibility(View.VISIBLE);
			} else {
				mStartHelpActionRow.setVisibility(View.VISIBLE);
			}
		}
		refreshTitleBarOptions();
	}
	
	/*
	 * methods for hiding and showing the side trips in the list view
	 */
	private boolean mShowingSidetrips = true;
	private void showOrHideSidetrips(String optionId) {
		mShowingSidetrips = optionId.equals("showsidetrips");
		setTourItemsList(mShowingSidetrips);
		mTourListAdapter.notifyDataSetChanged();
		refreshTitleBarOptions();
	}
	
	private void setTourItemsList(boolean includeSidetrips) {
		mTourMapItems.clear();
		for(SiteTourMapItem item : mSiteTourMapItems) {
			mTourMapItems.add(item);
			if(includeSidetrips) {
				mTourMapItems.addAll(item.getSideTrips());
			}
		}
	}
	
	class TourItemAdapter extends ArrayAdapter<TourMapItem> {
		
		// we need to cache the views to prevent the thumbnail images
		// from reloading the bitmap images, when distances change
		class RowViewHashMap extends LinkedHashMap<String, View> {
			private static final long serialVersionUID = 1L;
			protected static final int MAX_ENTRIES = 10;
			
			@Override
			protected boolean removeEldestEntry(Map.Entry<String, View> eldest) {
				return size() > MAX_ENTRIES;
			}
		}
		
		RowViewHashMap mViewCache = new RowViewHashMap();
		
		private Context mContext;
		
		public TourItemAdapter(Context context, List<TourMapItem> items) {
			super(context, 0, 0, items);
			mContext = context;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			TourMapItem item = getItem(position);
			
			int contentId = -1;
			int titleId = -1;
			int distanceId = -1;
			if(item.getClass() == SiteTourMapItem.class) {
				contentId = R.id.tourItemSiteContent;
				titleId = R.id.tourItemSiteTitle;
				distanceId = R.id.tourItemSiteDistance;
			} else if(item.getClass() == SideTripTourMapItem.class) {
				contentId = R.id.tourItemSideTripContent;
				titleId = R.id.tourItemSideTripTitle;
				distanceId = R.id.tourItemSideTripDistance;
			}
			
			if(!mViewCache.containsKey(item.getId())) {
				LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflator.inflate(R.layout.tour_list_item, null);
				
				// fill in most of the views context (except for distance)
			
				RemoteImageView imageView = (RemoteImageView) view.findViewById(R.id.tourItemImage);
				imageView.setURL(item.getPhotoUrl());
			
				view.findViewById(contentId).setVisibility(View.VISIBLE);
				TextView titleView = (TextView) view.findViewById(titleId);
				titleView.setText(item.getTitle());			
			
				ImageView statusImage = (ImageView) view.findViewById(R.id.tourItemStatus);
			
				int resourceId = 0;
				if(item.getStatus() == TourSiteStatus.VISITED) {
					resourceId = R.drawable.map_past;
				} else if(item.getStatus() == TourSiteStatus.CURRENT) {
					resourceId = R.drawable.map_currentstop;
				} else if(item.getStatus() == TourSiteStatus.FUTURE) {
					resourceId = R.drawable.map_future;
				}
			
				statusImage.setImageDrawable(getResources().getDrawable(resourceId));		
			} else {
				view = mViewCache.get(item.getId());
			}
			
			TextView distanceView = (TextView) view.findViewById(distanceId);
			Float distance = item.distance();
			if(distance != null) {
				distanceView.setText(LocaleMeasurements.getDistance(item.distance()));
			} else {
				distanceView.setText(null);
			}
			
			// only cache views which are visible (to prevent flicker)
			// invisible views cant filcker
			if(position >= mTourListView.getFirstVisiblePosition()) { 
				mViewCache.put(item.getId(), view);
			}
			
			return view;
		}
	}

	@Override
	protected NewModule getNewModule() {
		return new TourModule();
	}
                       
	@Override
	public boolean isModuleHomeActivity() {
		return true;
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}
}
