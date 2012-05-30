package edu.mit.mitmobile2.tour;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.mit.mitmobile2.HomeScreenActivity;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.RemoteImageView;
import edu.mit.mitmobile2.TitleBar;
import edu.mit.mitmobile2.maps.GeoRect;
import edu.mit.mitmobile2.maps.MapBaseActivity;
import edu.mit.mitmobile2.tour.Tour.ParcelableGeoPoint;
import edu.mit.mitmobile2.tour.Tour.SideTripTourMapItem;
import edu.mit.mitmobile2.tour.Tour.SiteTourMapItem;
import edu.mit.mitmobile2.tour.Tour.TourMapItem;
import edu.mit.mitmobile2.tour.Tour.TourSiteStatus;
import edu.mit.mitmobile2.tour.Tour.TourMapItem.LocationSupplier;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;


public class TourMapActivity extends MapBaseActivity {
	
	private static final String TOUR_STOPS_KEY = "tour_stops";
	private static final String TOUR_PATH_KEY = "tour_path";
	private static final String TOUR_ACTIVE_KEY = "tour_active";
	
	ListView mTourListView;
	MapView mMapView;
	ImageView mMapListSwitch;
	View mMapLegend;
	TourStartHelpActionRow mStartHelpActionRow;
	boolean mTourActive;
	int mTourCurrentPosition;
	List<SiteTourMapItem> mSiteTourMapItems;
	ArrayList<TourMapItem> mTourMapItems = new ArrayList<TourMapItem>();
	LocationManager mLocationManager;
	String mBestLocationProviderName;
	String mWorstLocationProviderName;
	Long mShowClosestBalloonInitialTime;
	
	TourRouteOverlay mSiteMarkers;
	private TourItemAdapter mTourListAdapter;
	
	private static int HELP_SELECT_STOP = 2;
	
	public static void launch(Context context, ArrayList<TourMapItem> tourItems, ArrayList<ParcelableGeoPoint> geoPoints, boolean tourActive) {
		Intent intent = new Intent(context, TourMapActivity.class);
		intent.putParcelableArrayListExtra(TOUR_STOPS_KEY, tourItems);
		intent.putParcelableArrayListExtra(TOUR_PATH_KEY, geoPoints);
		intent.putExtra(TOUR_ACTIVE_KEY, tourActive);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		
		Intent i = getIntent();
		
		mSiteTourMapItems = i.getParcelableArrayListExtra(TOUR_STOPS_KEY);
		List<ParcelableGeoPoint> geoPoints = i.getParcelableArrayListExtra(TOUR_PATH_KEY);
		mTourActive = i.getBooleanExtra(TOUR_ACTIVE_KEY, false);
		mTourCurrentPosition = getCurrentPosition();
				
		// be default show sidetrips in list if tour not yet active
		mShowingSidetrips = !mTourActive;
		setTourItemsList(mShowingSidetrips);
		
		GeoRect geoRect = new GeoRect(geoPoints);
		
		mSiteMarkers = new TourRouteOverlay(this, mapView, mSiteTourMapItems, geoPoints);
		mSiteMarkers.setOnTourItemSelectedListener(new TourRouteOverlay.OnTourItemSelectedListener() {
			@Override
			public void onTourItemSelected(TourMapItem tourItem) {
				if(tourItem.getClass() == SiteTourMapItem.class) {
					SiteTourMapItem siteItem = (SiteTourMapItem) tourItem;
					launchTour(siteItem);
				}
			}
		});
		
		setOverlays();
		
		mapView.getOverlays().add(mSiteMarkers);
		
		mapView.getController().setCenter(geoRect.getCenter());
		mapView.getController().zoomToSpan(geoRect.getLatitudeSpanE6(), geoRect.getLongitudeSpanE6());		
		
		TitleBar titleBar = (TitleBar) findViewById(R.id.mapTitleBar);
		
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
		
		mMapView = (MapView) findViewById(R.id.mapview);
		mMapListSwitch = (ImageView) findViewById(R.id.tourMapListSwitchImage);
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
			titleBar.setTitle("Tour Overview");
			mSiteMarkers.showBalloon(mSiteTourMapItems.get(mTourCurrentPosition));
		} else {
			
			// tour not active, show information to help the user know where to start
			mShowClosestBalloonInitialTime = System.currentTimeMillis();		
			mStartHelpActionRow.setVisibility(View.VISIBLE);
			titleBar.setTitle("Starting Point");
			mMapLegend.setVisibility(View.GONE);
		}
		
		View.OnClickListener mapListSwitchListener = new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				toggleMapList();
			}
		};
		
		mMapListSwitch.setOnClickListener(mapListSwitchListener);
		findViewById(R.id.tourMapListSwitchListLabel).setOnClickListener(mapListSwitchListener);
		findViewById(R.id.tourMapListSwitchMapLabel).setOnClickListener(mapListSwitchListener);
	}
	
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
			return mLocation;
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
		public void onProviderDisabled(String provider) {} // TODO Auto-generated method stub

		@Override
		public void onProviderEnabled(String provider) {} // TODO Auto-generated method stub

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {} // TODO Auto-generated method stub		
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
			mSiteMarkers.showBalloon(mSiteTourMapItems.get(0));
			return true;
		}
				
		// now that we have survived all the sanity checks actually show the balloon
		// for the closest tour site;
		mSiteMarkers.showBalloon(closest);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
		if(requestCode == HELP_SELECT_STOP && resultCode == RESULT_OK) {
			TourMapItem item = resultIntent.getParcelableExtra(TourStartHelpActivity.SELECTED_SITE);
			item.setLocationSupplier(mLocationSupplier);
			mSiteMarkers.showBalloon(item);
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
	
	private static final int MENU_HOME = 0;
	private static final int MENU_TOUR_HOME = 1;
	private static final int MENU_MAP_LIST = 2;
	private static final int MENU_MY_LOCATION = 3;
	private static final int MENU_SHOW_OR_HIDE_SIDETRIPS = 4;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case MENU_HOME:
				HomeScreenActivity.goHome(this);
				return true;
				
			case MENU_TOUR_HOME:
				Intent intent = new Intent(this, new TourModule().getModuleHomeActivity());
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				this.startActivity(intent);
				return true;
				
			case MENU_MAP_LIST:
				toggleMapList();
				return true;
				
			case MENU_MY_LOCATION: 
				GeoPoint myLocation = myLocationOverlay.getMyLocation();
				if (myLocation != null) mctrl.animateTo(myLocation);
				return true;
				
			case MENU_SHOW_OR_HIDE_SIDETRIPS:
				showOrHideSidetrips();
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Module tourModule = new TourModule();
		
		menu.clear();
		
		menu.add(0, MENU_HOME, Menu.NONE, "Home")
			.setIcon(R.drawable.menu_home);
		
		menu.add(0, MENU_TOUR_HOME, Menu.NONE, tourModule.getMenuOptionTitle())
			.setIcon(tourModule.getMenuIconResourceId());
		
		if(mListView.getVisibility() == View.GONE) {
			// only show my location menu option, when in map mode
			menu.add(0, MENU_MY_LOCATION, Menu.NONE, "My Location")
				.setIcon(R.drawable.menu_mylocation);
			
			menu.add(0, MENU_MAP_LIST, Menu.NONE, "List")
			  .setIcon(R.drawable.menu_view_as_list);
			
		} else {
			menu.add(0, MENU_MAP_LIST, Menu.NONE, "Map")
			  .setIcon(R.drawable.menu_view_on_map);
			
			String sidetripsAction = mShowingSidetrips ? "Hide Side Trips" : "Show Side Trips";
			menu.add(0, MENU_SHOW_OR_HIDE_SIDETRIPS, Menu.NONE, sidetripsAction)
				.setIcon(R.drawable.menu_sidetrips);
		}
		
		return super.onPrepareOptionsMenu(menu);
		
	}
	
	private void toggleMapList() {
		if(mListView.getVisibility() == View.GONE) {
			mListView.setVisibility(View.VISIBLE);
			mMapView.setVisibility(View.GONE);
			mMapListSwitch.setImageDrawable(getResources().getDrawable(R.drawable.tour_toggle_right));
			
			if(mTourActive) {
				mMapLegend.setVisibility(View.GONE);
			} else {
				mStartHelpActionRow.setVisibility(View.GONE);
			}
		} else {
			mListView.setVisibility(View.GONE);
			mMapView.setVisibility(View.VISIBLE);
			mMapListSwitch.setImageDrawable(getResources().getDrawable(R.drawable.tour_toggle_left));
			
			if(mTourActive) {
				mMapLegend.setVisibility(View.VISIBLE);
			} else {
				mStartHelpActionRow.setVisibility(View.VISIBLE);
			}
		}
	}
	
	/*
	 * methods for hiding and showing the side trips in the list view
	 */
	private boolean mShowingSidetrips = true;
	private void showOrHideSidetrips() {
		mShowingSidetrips = !mShowingSidetrips;
		setTourItemsList(mShowingSidetrips);
		mTourListAdapter.notifyDataSetChanged();
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
	protected int getLayoutId() {
		return R.layout.tour_map;
	}
}
