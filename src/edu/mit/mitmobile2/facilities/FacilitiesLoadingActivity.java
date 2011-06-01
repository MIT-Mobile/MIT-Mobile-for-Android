package edu.mit.mitmobile2.facilities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;

public class FacilitiesLoadingActivity extends ModuleActivity {
	
	private static final String TAG = "FacilitiesLoadingActivity";	
	FullScreenLoader mLoader;
	TextView mIntroductionView;

	private boolean mFacilitiesAvailable = false;
	Context mContext;
	private FacilitiesBackgroundView mBackgroundView;
	FacilitiesDB db;

	Handler mFacilitiesLoadedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			//if(msg.arg1 == MobileWebApi.SUCCESS) {
			if(msg.arg1 == FacilitiesDB.STATUS_CATEGORIES_SUCCESSFUL) {
				Log.d(TAG,"received success message for categories");
			} 
			else if(msg.arg1 == FacilitiesDB.STATUS_LOCATIONS_SUCCESSFUL) {
				Log.d(TAG,"received success message for locations, launching next activity");
				Intent intent = new Intent(mContext, FacilitiesProblemLocationActivity.class);
				startActivity(intent);	
			}
			else {
				mLoader.showError();
			}
		}		
	};

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		mContext = this;
		db = FacilitiesDB.getInstance(mContext);
		//DatabaseUpdater databaseUpdater = new DatabaseUpdater();
		setContentView(R.layout.facilities_loading);

		//mBackgroundView = (FacilitiesBackgroundView) findViewById(R.id.facilitiesBackground);

		mLoader = (FullScreenLoader) findViewById(R.id.facilitiesLoader);
		//mIntroductionView = (TextView) findViewById(R.id.tourHomeIntroduction);
		
		mLoader.showLoading();
		
		//FacilitiesDB.updateCategories(mContext, mFacilitiesLoadedHandler );
		//FacilitiesDB.updateLocations(mContext, mFacilitiesLoadedHandler );
		new DatabaseUpdater().execute("test");
		//DatabaseUpdater du = new DatabaseUpdater();
		//du.run();
	}

	
//	private class DatabaseUpdater extends Thread {
//		
//		@Override
//		public void run() {
//			FacilitiesDB.updateCategories(mContext, mFacilitiesLoadedHandler );
//			FacilitiesDB.updateLocations(mContext, mFacilitiesLoadedHandler );
//		}
//	}

	private class DatabaseUpdater extends AsyncTask<String, Void, String> {
		
	    ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			//dialog = ProgressDialog.show(StatusUpdateActivity.this, "", "Posting message. Please wait", true);
		}

		@Override
		protected String doInBackground(String... msg) {
			// Executed in worker thread
			String result = "";
			try {
				FacilitiesDB.updateCategories(mContext, mFacilitiesLoadedHandler );
				FacilitiesDB.updateLocations(mContext, mFacilitiesLoadedHandler );
				result = "success";
			} catch (Exception e) {
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// Executed in UI thread
			//dialog.dismiss();
			Toast.makeText(FacilitiesLoadingActivity.this, result, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		//mBackgroundView.startBackgroundAnimation();
	}
		

	static final int MENU_SCAN_QR = MENU_MODULE_HOME + 1;
	static final int MENU_SHOW_TOUR_MAP = MENU_MODULE_HOME + 4;
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		if(mFacilitiesAvailable) {
			//menu.add(0, MENU_SHOW_TOUR_MAP, Menu.NONE, "Tour Map")
			//	.setIcon(R.drawable.menu_maps);
		}
		//menu.add(0, MENU_SCAN_QR, Menu.NONE, "Scan QR Code");
	}
	/*****************************************************************************/
	


	@Override
	protected Module getModule() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isModuleHomeActivity() {
		// TODO Auto-generated method stub
		return false;
	}

}
