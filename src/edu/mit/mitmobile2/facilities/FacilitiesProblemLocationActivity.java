package edu.mit.mitmobile2.facilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.FacilitiesItem.CategoryRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationSearchRecord;

//public class FacilitiesProblemLocationActivity extends ListActivity implements OnClickListener, OnItemClickListener {

public class FacilitiesProblemLocationActivity extends ModuleActivity {

	public static final String TAG = "FacilitiesProblemLocationActivity";
	private static final int MENU_INFO = 0;
	
	Context mContext;
	ListView mListView;
	final FacilitiesDB db = FacilitiesDB.getInstance(this);
	FullScreenLoader mLoader;
	
	Handler mFacilitiesLoadedHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG,"message = " + msg.arg1);
		}		
	};
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate()");
		
		mContext = this;
		Handler uiHandler = new Handler();

		createViews();		
	}

	public void createViews() {
		
		setContentView(R.layout.facilities_problem_location);
		mLoader = (FullScreenLoader) findViewById(R.id.facilitiesLoader);
		mLoader.showLoading();
		new DatabaseUpdater().execute(""); 
		
        // Set up location search

        // Set up use my location button
		TwoLineActionRow useMyLocationActionRow = (TwoLineActionRow) findViewById(R.id.facilitiesUseMyLocationActionRow);
		String title1 = "Use My Location";
		String title2 = "";
		useMyLocationActionRow.setTitle(title1 + " " + title2, TextView.BufferType.SPANNABLE);
		useMyLocationActionRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, FacilitiesUseMyLocationActivity.class);
				startActivity(intent);
			}
		});
		
		AutoCompleteTextView facilitiesTextLocation = (AutoCompleteTextView) findViewById(R.id.facilitiesTextLocation);
		facilitiesTextLocation.setAdapter(new LocationsSearchCursorAdapter(this, db));
		facilitiesTextLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View row, int position,
					long id) {
				
				// have no idea what this method should actually do
				// this is completey a placeholder
				Cursor cursor = (Cursor) listView.getItemAtPosition(position);
				LocationSearchRecord locationSearchRecord = new LocationSearchRecord(cursor);
				/* 
				 * if the user selected a use what I typed option, use this as the actual location
				 * and jump to the problem type selection screen
				 * Else, go to the room selection screen if building number is defined, else inside/outside screen
				 */
				Log.d(TAG,"locastion search _id = " + locationSearchRecord._id);
				if (Integer.parseInt(locationSearchRecord._id) == -1) {
					Global.sharedData.getFacilitiesData().setUserAssignedLocationName(locationSearchRecord.name);
					Intent intent = new Intent(mContext, FacilitiesProblemTypeActivity.class);
					startActivity(intent);	
					return;
				}		
				
				Global.sharedData.getFacilitiesData().setLocationId(locationSearchRecord.id);
				Global.sharedData.getFacilitiesData().setLocationName(locationSearchRecord.name);
				Global.sharedData.getFacilitiesData().setBuildingNumber(locationSearchRecord.bldgnum);
				Log.d(TAG,"locastion search _id = " + locationSearchRecord._id);			
				// If there is no building number for the selected location, go to the inside/outside selection activity, else retrieve the rooms for the location
				if (locationSearchRecord.bldgnum == null || locationSearchRecord.bldgnum.equals("")) {
					Intent intent = new Intent(mContext, FacilitiesInsideOutsideActivity.class);
					startActivity(intent);
				}
				else {
					Intent intent = new Intent(mContext, FacilitiesRoomLocationsActivity.class);
					startActivity(intent);
				}
				//String name = cursor.getString(titleIndex);
				//Toast.makeText(mContext, "you selected " + locationSearchRecord.name, Toast.LENGTH_LONG).show();
			}
		});
        		
	}

	private class DatabaseUpdater extends AsyncTask<String, Void, String> {
        
        ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(String... msg) {
                    // Executed in worker thread
                    String result = "";
                    try {
                            final String categoryVersion = FacilitiesDB.updateCategories(mContext, mFacilitiesLoadedHandler );
                            mFacilitiesLoadedHandler.post(new Runnable() {
                                 public void run() {
                                	 Global.setVersion("local", "map", "category_list", categoryVersion, mContext);
                                 }
                            });

                            final String locationVersion = FacilitiesDB.updateLocations(mContext, mFacilitiesLoadedHandler );
                            mFacilitiesLoadedHandler.post(new Runnable() {
                                 public void run() {
                                	 Global.setVersion("local", "map", "location", locationVersion, mContext);
                                 }
                            });

                            final String problemTypeVersion = FacilitiesDB.updateProblemTypes(mContext,mFacilitiesLoadedHandler);
                            mFacilitiesLoadedHandler.post(new Runnable() {
                                 public void run() {
                                	 Global.setVersion("local", "facilities", "problem_type", problemTypeVersion, mContext);
                                 }
                            });
                            result = "success";
                    } catch (Exception e) {
                            Log.d(TAG,"DatabaseUpdater exception: " + e.getMessage());
                    }
                    return result;
            }

            @Override
            protected void onPostExecute(String result) {
                // Executed in UI thread
				CategoryAdapter adapter = new CategoryAdapter(FacilitiesProblemLocationActivity.this, db.getCategoryCursor());
				ListView listView = (ListView) findViewById(R.id.facilitiesProblemLocationListView);
				listView.setAdapter(adapter);
				listView.setVisibility(View.VISIBLE);
				
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
						CategoryRecord category = db.getCategory(position);
						Log.d(TAG,"position = " + position + " id = " + category.id + " name = " + category.name);
						// save the selected category
						Global.sharedData.getFacilitiesData().setLocationCategory(category.id);
						Intent intent = new Intent(mContext, FacilitiesLocationsForCategoryActivity.class);
						startActivity(intent);          
					}
				});
				mLoader.setVisibility(View.GONE);
            }
    }
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		//mBackgroundView.startBackgroundAnimation();
	}
		
	@Override
	protected Module getModule() {
		return new FacilitiesModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_INFO:
			Intent intent = new Intent(mContext, FacilitiesInfoActivity.class);					
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) { 
	}

	
}
	