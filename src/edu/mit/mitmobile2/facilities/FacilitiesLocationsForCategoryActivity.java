package edu.mit.mitmobile2.facilities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.MITPlainSecondaryTitleBar;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.facilities.FacilitiesDB.LocationTable;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationRecord;


public class FacilitiesLocationsForCategoryActivity extends NewModuleActivity {
	
	public static final String TAG = "FacilitiesLocationsForCategoryActivity";

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
        
        	addSecondaryTitle("Where is it?");
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
				
				Cursor cursor = (Cursor) listView.getItemAtPosition(position);
				LocationRecord locationRecord = FacilitiesDB.getLocationRecord(cursor);
				int dbIdIndex = cursor.getColumnIndexOrThrow(LocationTable._ID);
				long dbId = cursor.getLong(dbIdIndex);
				/* 
				 * if the user selected a use what I typed option, use this as the actual location
				 * and jump to the problem type selection screen
				 * Else, go to the room selection screen if building number is defined, else inside/outside screen
				 */
				Log.d(TAG,"locastion search _id = " + dbId);
				if (dbId == -1) {
					int nameIndex = cursor.getColumnIndexOrThrow("name");
					Global.sharedData.getFacilitiesData().setUserAssignedLocationName(cursor.getString(nameIndex));
					Intent intent = new Intent(mContext, FacilitiesProblemTypeActivity.class);
					startActivity(intent);	
					return;
				} else {
					Global.sharedData.getFacilitiesData().setUserAssignedLocationName(null);
				}
				
				FacilitiesActivity.launchActivityForLocation(mContext, locationRecord);
			}
		});
				
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
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
	