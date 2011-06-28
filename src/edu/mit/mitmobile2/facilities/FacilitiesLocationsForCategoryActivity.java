package edu.mit.mitmobile2.facilities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.FacilitiesItem.CategoryRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationCategoryRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationSearchRecord;


public class FacilitiesLocationsForCategoryActivity extends ModuleActivity {
	
	public static final String TAG = "FacilitiesLocationsForCategoryActivity";
	private static final int MENU_INFO = 0;

	Context mContext;
	ListView mListView;
	FacilitiesDB db;
	Handler uiHandler = new Handler();
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate()");
		super.onCreate(savedInstanceState);
		
		mContext = this;
		db = FacilitiesDB.getInstance(mContext);

		createViews();

	}

	public void createViews() {
        setContentView(R.layout.facilities_locations_for_category);

        // Set up locations for selected category
		final FacilitiesDB db = FacilitiesDB.getInstance(this);
		LocationAdapter adapter = new LocationAdapter(this, db.getLocationCategoryCursor());
		Log.d(TAG,"num records in adapter = " + adapter.getCount());
		ListView listView = (ListView) findViewById(R.id.facilitiesProblemLocationsForCategoryListView);
		listView.setAdapter(adapter);
		listView.setVisibility(View.VISIBLE);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Cursor cursor = (Cursor)parent.getItemAtPosition(position);
				LocationRecord location = FacilitiesDB.getLocationRecord(cursor);
				FacilitiesActivity.launchActivityForLocation(mContext, location);
			}
		});
		
		AutoCompleteTextView facilitiesTextLocation = (AutoCompleteTextView) findViewById(R.id.facilitiesTextLocation);
		facilitiesTextLocation.setAdapter(new LocationsForCategorySearchCursorAdapter(this, db));
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
				Global.sharedData.getFacilitiesData().setLocationId(locationSearchRecord.id);
				Global.sharedData.getFacilitiesData().setBuildingNumber(locationSearchRecord.bldgnum);
				Log.d(TAG,"locastion search _id = " + locationSearchRecord._id);
				if (Integer.parseInt(locationSearchRecord._id) == -1) {
					Global.sharedData.getFacilitiesData().setBuildingRoomName(locationSearchRecord.name);
					Intent intent = new Intent(mContext, FacilitiesProblemTypeActivity.class);
					startActivity(intent);					
				}				
				// If there is no building number for the selected location, go to the inside/outside selection activity, else retrieve the rooms for the location
				else if (locationSearchRecord.bldgnum == null || locationSearchRecord.bldgnum.equals("")) {
					Intent intent = new Intent(mContext, FacilitiesInsideOutsideActivity.class);
					startActivity(intent);
				}
				else {
					Intent intent = new Intent(mContext, FacilitiesRoomLocationsActivity.class);
					startActivity(intent);
				}
			}
		});
				
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
	