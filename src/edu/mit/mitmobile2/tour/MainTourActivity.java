package edu.mit.mitmobile2.tour;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;

public class MainTourActivity extends ModuleActivity {
	
	FullScreenLoader mLoader;
	TextView mIntroductionView;

	private boolean mTourAvailable = false;
	private MainTourBackgroundView mBackgroundView;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);

		setContentView(R.layout.tour_home);

		mBackgroundView = (MainTourBackgroundView) findViewById(R.id.tourHomeBackground);

		mLoader = (FullScreenLoader) findViewById(R.id.tourHomeLoader);
		mIntroductionView = (TextView) findViewById(R.id.tourHomeIntroduction);
		
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
	protected Module getModule() {
		return new TourModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return true;
	}

	static final int MENU_SCAN_QR = MENU_MODULE_HOME + 1;
	static final int MENU_SHOW_TOUR_MAP = MENU_MODULE_HOME + 4;
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		if(mTourAvailable) {
			menu.add(0, MENU_SHOW_TOUR_MAP, Menu.NONE, "Tour Map")
				.setIcon(R.drawable.menu_maps);
		}
		//menu.add(0, MENU_SCAN_QR, Menu.NONE, "Scan QR Code");
	}
	/*****************************************************************************/
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
	

	/*****************************************************************************/
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
}
