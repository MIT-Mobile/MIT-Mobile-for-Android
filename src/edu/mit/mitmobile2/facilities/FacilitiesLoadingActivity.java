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
				//Intent intent = new Intent(mContext, FacilitiesProblemLocationActivity.class);
				//startActivity(intent);
				
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
		setContentView(R.layout.facilities_loading);

		mLoader = (FullScreenLoader) findViewById(R.id.facilitiesLoader);
		mLoader.showLoading();
		
		new DatabaseUpdater().execute("");
	}

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
			
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
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
