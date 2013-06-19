package edu.mit.mitmobile2.tour;

import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;

public class MainTourActivity extends NewModuleActivity {
	
	FullScreenLoader mLoader;
	TextView mIntroductionView;

	@SuppressWarnings("unused")
	private boolean mTourAvailable = false;
	private MainTourBackgroundView mBackgroundView;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);

		setContentView(R.layout.tour_home);

		mBackgroundView = (MainTourBackgroundView) findViewById(R.id.tourHomeBackground);

		mLoader = (FullScreenLoader) findViewById(R.id.tourHomeLoader);
		mIntroductionView = (TextView) findViewById(R.id.tourHomeIntroduction);
		
		mLoader.showLoading();
		TourModel.fetchTour(this, mTourLoadedHandler);
		
		findViewById(R.id.tourSelectStartPoint).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainTourActivity.this, TourIntroductionActivity.class);
				startActivity(intent);
			}
		});
		
		findViewById(R.id.tourHomeMITIntroductionButton).setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainTourActivity.this, MITIntroductionActivity.class);
				startActivity(intent);
			}
		});
		
		findViewById(R.id.tourHomeGuidedToursButton).setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				CommonActions.viewURL(MainTourActivity.this, "http://web.mit.edu/infocenter/campustours.html");
			}
		});
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		mBackgroundView.startBackgroundAnimation();
	}
	
	Handler mTourLoadedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.arg1 == MobileWebApi.SUCCESS) {
				mIntroductionView.setText(R.string.tourHomeIntro);
				mLoader.setVisibility(View.GONE);
				findViewById(R.id.tourHomeContent).setVisibility(View.VISIBLE);
				mTourAvailable = true;
			} else {
				mLoader.showError();
			}
		}		
	};
	
	@Override
	protected NewModule getNewModule() {
		return new TourModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return true;
	}	

	/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case MENU_SCAN_QR:
			i = new Intent("com.google.zxing.client.android.SCAN");
			i.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(i, 0);
			return true;
		case MENU_SHOW_TOUR_MAP:
			Tour tour = TourModel.getTour();
			TourMapActivity.launch(this, tour.getDefaultTourMapItems(), tour.getPathGeoPoints(), false);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				showDialog("Success", "Format: " + format + "\nContents: " + contents);
			} else if (resultCode == RESULT_CANCELED) {
				showDialog("Failed", "Failure msg");
			}
		}
	}

	  private void showDialog(String title, String message) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle(title);
	    builder.setMessage(message);
	    builder.setPositiveButton("OK", null);
	    builder.show();
	  }
	*/

	private static String MENU_TOUR_MAP = "tour_map";
	
	@Override
	protected List<MITMenuItem> getSecondaryMenuItems() {
		return Arrays.asList(
			new MITMenuItem(MENU_TOUR_MAP, "Tour Map")
		);
	}
	
	@Override
	protected boolean isScrollable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		Tour tour = TourModel.getTour();
		TourMapActivity.launch(this, tour.getDefaultTourMapItems(),
		tour.getPathGeoPoints(), false);
	}
}
