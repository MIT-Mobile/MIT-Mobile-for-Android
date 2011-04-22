package edu.mit.mitmobile2.tour;

import java.util.ArrayList;
import java.util.List;

import com.google.zxing.client.android.CaptureActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;


import edu.mit.mitmobile2.AudioPlayer;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderActivity;
import edu.mit.mitmobile2.SliderView;
import edu.mit.mitmobile2.tour.Tour.Site;
import edu.mit.mitmobile2.tour.Tour.StartLocation;
import edu.mit.mitmobile2.tour.Tour.TourItem;
import edu.mit.mitmobile2.tour.Tour.TourMapItem;

public class TourStopSliderActivity extends SliderActivity {
	
	private static final String BEGIN_GUID_KEY = "start_guid";
	private static final String SITE_GUID_KEY = "site_guid";
	
	// this key is only used in on newIntent, largely because
	// we can only give directions if we already know where the user is on the tour
	private static final String START_WITH_DIRECTIONS_KEY = "start_with_directions";
	
	private List<TourItem> mTourItems;
	private Tour mTour;
	
	private TourProgressBar progbar;
	private AudioPlayer ap;
	private LocationManager mLocationManager;
	private String mLocationProviderName;
	
	public static void launchFromSiteGuid(Context context, String guid, boolean startWithDirections) {
		Intent intent = new Intent(context, TourStopSliderActivity.class);
		intent.putExtra(SITE_GUID_KEY, guid);
		intent.putExtra(START_WITH_DIRECTIONS_KEY, startWithDirections);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}
			
			
	@Override
	protected void onStop() {
		super.onStop();
		ap.stop();
	}

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		mTour = TourModel.getTour();	
		
		if(getIntent().hasExtra(BEGIN_GUID_KEY)) {
			String startLocationGuid = getIntent().getStringExtra(BEGIN_GUID_KEY);		
			StartLocation startLocation = mTour.getStartLocation(startLocationGuid);
			mTourItems = mTour.getTourList(startLocation);	
		} else if(getIntent().hasExtra(SITE_GUID_KEY)) {
			String siteGuid = getIntent().getStringExtra(SITE_GUID_KEY);
			Site site = mTour.getSite(siteGuid);
			mTourItems = mTour.getTourList(site);
		}

		// TODO need to remember what stops we visited if we leave Activity
		
		// Progress Bar
		mSliderView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1f));
		LinearLayout sliderTop = (LinearLayout) findViewById(R.id.ll_top);
		progbar = new TourProgressBar(this);
		progbar.init(mTour.getSites().size(), 0);
		sliderTop.addView(progbar);
		
		// Audio
		ap = new AudioPlayer();
		ap.init(this, null);
		
		createView();
		
		this.setOnPositionChangedListener(new SliderView.OnPositionChangedListener() {		
			@Override
			public void onPositionChanged(int newPosition, int oldPosition) {
				ap.setPage(newPosition);
				ap.stop();
				if((newPosition % 2) == 0) {
					progbar.setProgress(newPosition/2);
				}
			}
		});
		
		mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		mLocationProviderName = mLocationManager.getBestProvider(criteria, true);
	}
	
	List<TourStopSliderInterface> mTourStopSliderInterfaces;
	
	@Override
	protected void onResume() {
		super.onResume();
		if(mLocationProviderName != null) {
			mLocationManager.requestLocationUpdates(mLocationProviderName, 5 * 1000, 0, mLocationListener);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mLocationManager.removeUpdates(mLocationListener);
	}
	
	LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			for(TourStopSliderInterface tourStopSliderInterface: mTourStopSliderInterfaces) {
				tourStopSliderInterface.onLocationChanged(location);
			}
		}

		@Override
		public void onProviderDisabled(String provider) { } // TODO Auto-generated method stub

		@Override
		public void onProviderEnabled(String provider) { } // TODO Auto-generated method stub

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) { } // TODO Auto-generated method stub
		
	};
	
	private void createView() {
		mTourStopSliderInterfaces = new ArrayList<TourStopSliderInterface>();
		for(TourItem tourItem : mTourItems) {
			boolean isSite = (tourItem.getClass() == Site.class);
			TourStopSliderInterface sliderInterface = new TourStopSliderInterface(this, mTour, tourItem, ap, progbar, isSite);
			addScreen(sliderInterface, null, tourItem.getLabel());
			mTourStopSliderInterfaces.add(sliderInterface);
		}
		//Log.v("tour_debug", "tour_debug: getPositionValue="+String.valueOf(getPositionValue()));
		addScreen(new TourThankYouSliderInterface(this, mTour.getFooter()), null, "Thank You");
		setPosition(getPositionValue());
		progbar.setProgress(getPositionValue()/2);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		if(intent.hasExtra(SITE_GUID_KEY)) {
			String siteGuid = intent.getStringExtra(SITE_GUID_KEY);
			int position = getTourItemIndex(siteGuid);
			
			boolean startOnPreviousDirections = intent.getBooleanExtra(START_WITH_DIRECTIONS_KEY, false);
			if(startOnPreviousDirections) {
				position--;
			}
			
			setPosition(position);
			progbar.setProgress(position/2);
		}
	}
	
	private int getTourItemIndex(String siteGuid) {
		for(int i=0; i < mTourItems.size(); i++) {
			TourItem tourItem = mTourItems.get(i);
			if(tourItem.getClass() == Site.class) {
				Site site = (Site) tourItem;
				if(site.getSiteGuid().equals(siteGuid)) {
					
					// found the site to move the tour to
					return i;
				}
			}		
		}
		
		return -1;
	}
	
	@Override
	protected Module getModule() {
		return new TourModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	private static final int MENU_SHOW_TOUR_MAP = MENU_LAST + 1;
	private static final int MENU_REFRESH_IMAGES = MENU_LAST + 2;
	//private static final int MENU_CAMERA = MENU_LAST + 2;
	//private static final int MENU_SCAN_QR = MENU_LAST + 3;
	//private static final int MENU_DOWNLOAD_AUDIO = MENU_LAST + 4;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == MENU_SHOW_TOUR_MAP) {
			int tourItemPosition;
			if(getPosition() < mTourItems.size()) {
				tourItemPosition = getPosition();
			} else {
				// must be on the thank you screen, just use the last stop
				tourItemPosition = mTourItems.size()-1;
			}
			ArrayList<TourMapItem> tourMapItems = Tour.getTourMapItems(mTourItems, tourItemPosition);
			TourMapActivity.launch(this, tourMapItems, mTour.getPathGeoPoints(), true);
			return true;
		} if(item.getItemId() == MENU_REFRESH_IMAGES) {
			int position = getPosition();
			if(position < mTourStopSliderInterfaces.size()) {
				mTourStopSliderInterfaces.get(position).refreshImages();
			}
		}
		
		/* 
		else if (item.getItemId() == MENU_CAMERA) {
			TourCameraActivity.takePict(this, "this could be Stop name");
		} else if (item.getItemId() == MENU_SCAN_QR) {
			i = new Intent("com.google.zxing.client.android.SCAN");
			i.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(i, CaptureActivity.CAPTURE_QR_ACTIVITY_REQUEST_CODE);
		} else if (item.getItemId() == MENU_DOWNLOAD_AUDIO) {
			ap.downloadTourItemAudio(mTourItems.get(getPosition()));
		}
		*/
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		menu.add(0, MENU_SHOW_TOUR_MAP, Menu.NONE, "Tour Map")
			.setIcon(R.drawable.menu_maps);
		
		if(getPosition() < mTourStopSliderInterfaces.size()) {
			menu.add(0, MENU_REFRESH_IMAGES, Menu.NONE, "Refresh")
			.setIcon(R.drawable.menu_refresh);
		}
		//menu.add(0, MENU_CAMERA, Menu.NONE, "Camera")
		//	.setIcon(R.drawable.menu_camera);
		//menu.add(0, MENU_SCAN_QR, Menu.NONE, "Scan QR Code")
		//	.setIcon(R.drawable.menu_scanqr);
		//menu.add(0, MENU_DOWNLOAD_AUDIO, Menu.NONE, "Download Audio (Test)");
	}

	/*****************************************************************************/
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == CaptureActivity.CAPTURE_QR_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				showDialog("Success", "Format: " + format + "\nContents: " + contents);
			} else if (resultCode == RESULT_CANCELED) {
				showDialog("Failed", "Failure msg");
			}
		} else if (requestCode == TourCameraActivity.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			TourCameraActivity.handleCameraResult(this);
		}
	}

	  private void showDialog(String title, String message) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(title);
	    builder.setMessage(message);
	    builder.setPositiveButton("OK", null);
	    builder.show();
	  }
	  
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if(keyCode == KeyEvent.KEYCODE_BACK) {
			 if(getPosition() > 0) {
				 mSliderView.slideLeft();
				 return true;
			 }
		 }
		 
		 return super.onKeyDown(keyCode, event);
	 }
}
