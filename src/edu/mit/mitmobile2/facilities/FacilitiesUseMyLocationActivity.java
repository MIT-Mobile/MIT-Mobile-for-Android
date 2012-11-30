package edu.mit.mitmobile2.facilities;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TitleBar;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationRecord;


public class FacilitiesUseMyLocationActivity extends NewModuleActivity {

	public static final String TAG = "FacilitiesLocationsNearByActivity";
	private static final int REASONABLE_LOCATION_AGE = 90 * 1000; // 90 seconds
	private static final int SUFFICIENT_ACCURACY = 100; // 100 meters;
	private static final int MAX_WAIT_TIME = 6000; // do not wait more than 5000 miliseconds
	
	private Location mLocation;
	private LocationListener mLocationListener;
	boolean mLocationSet = false;
	boolean mLocationErrorShown = false;
	
	Context mContext;
	ListView mListView;
	final FacilitiesDB db = FacilitiesDB.getInstance(this);
	FullScreenLoader mLoader;
	private Handler uiHandler;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate()");
		
		mContext = this;
		uiHandler = new Handler();
		uiHandler.postDelayed(
			new Runnable() {
				@Override
				public void run() {
					loadLocations();
				}
			}, 
		MAX_WAIT_TIME);
		
		
		createViews();		
		
		LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new MyLocationListener();

		Location lastKnownLocation = lastKnownLocation();
		if(isReasonableLocation(lastKnownLocation)) {
			mLocation = lastKnownLocation;
			loadLocations();
		} else {
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
			locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locManager.removeUpdates(mLocationListener);
	}
	
	private boolean isRecentLocation(Location location) {
		if(location != null) {
			return System.currentTimeMillis() - location.getTime() < REASONABLE_LOCATION_AGE;
		}
		return false;
	}
	
	private boolean isReasonableLocation(Location location) {
		if(location != null) {
			if(isRecentLocation(location)) {
				return location.getAccuracy() < SUFFICIENT_ACCURACY;
			}
		}
		return false;
	}
	
	private Location lastKnownLocation() {
		LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Location lastGPS = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location lastNetwork = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		if(isRecentLocation(lastGPS)) {
			return lastGPS;
		}
		if(isRecentLocation(lastNetwork)) {
			return lastNetwork;
		}
		
		// for simplicity fallback to the lastGPS
		return lastGPS;
	}
	
	public void loadLocations() {
		if(!mLocationSet) {
			mLocationSet = true;
			LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			locManager.removeUpdates(mLocationListener);
			
			// if no location found attempt to fall back
			// to last known location
			if(mLocation == null) {
				mLocation = lastKnownLocation();
			}
			
			if(mLocation == null) {
				FacilitiesUseMyLocationActivity.this.locationNotFound();
				return; // early exit 
			}
			
			new Thread() {
				@Override
				public void run() {					
					FacilitiesDB db = FacilitiesDB.getInstance(mContext);
					List<LocationRecord> allLocations = db.getLocationsNearLocation(mLocation);
					final List<LocationRecord>  closestLocations = allLocations.subList(0, 10);
					
					uiHandler.post(new Runnable() {

						@Override
						public void run() {
							mListView.setAdapter(new LocationArrayAdapter(mContext, 0, closestLocations));
							mListView.setVisibility(View.VISIBLE);
							mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
									LocationRecord location = closestLocations.get(position);
									FacilitiesActivity.launchActivityForLocation(mContext, location);
								}
							});
							
							mLoader.setVisibility(View.GONE);
						}
					});
				}
			}.start();
		}
	}
	
	public void createViews() {
		setContentView(R.layout.boring_list_layout);
		TitleBar titleBar = (TitleBar) findViewById(R.id.boringListTitleBar);
		titleBar.setVisibility(View.GONE);
		
		addSecondaryTitle("Nearby Locations");
		
		mLoader = (FullScreenLoader) findViewById(R.id.boringListLoader);
		mLoader.showLoading();
		
		mListView = (ListView) findViewById(R.id.boringListLV);

	}

	private void locationNotFound() {
		if(!mLocationErrorShown) {
			Toast.makeText(this, "No location found please check your settings", Toast.LENGTH_LONG).show();
			finish();
			mLocationErrorShown = true;
		}
	}
	
	/* Class My Location Listener */

	public class MyLocationListener implements LocationListener {


			@Override
			public void onLocationChanged(Location loc){
				loc.getLatitude();
				loc.getLongitude();

				if (mLocation == null) {
					mLocation = loc;
				} else if (loc.getAccuracy() < mLocation.getAccuracy()) {
					mLocation = loc;
				}
				
				if (mLocation.getAccuracy() < SUFFICIENT_ACCURACY) {
					loadLocations();
				}
			}

			@Override
			public void onProviderDisabled(String arg0) {
				FacilitiesUseMyLocationActivity.this.locationNotFound();				
			}
	
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}
	
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
	     		
	}

	@Override
	public boolean isModuleHomeActivity() {
		// TODO Auto-generated method stub
		return false;
	}

	
	private static class LocationArrayAdapter extends ArrayAdapter<LocationRecord> {

		private Context mContext;
		public LocationArrayAdapter(Context context, int textViewResourceId,
				List<LocationRecord> objects) {
			super(context, textViewResourceId, objects);
			mContext = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				convertView = new TwoLineActionRow(mContext);
			}
			
			LocationRecord item = getItem(position);
			LocationAdapter.populateView(item, convertView);
			return convertView;
		}
	}




	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return new FacilitiesModule();
	}

	@Override
	protected boolean isScrollable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}
}
	