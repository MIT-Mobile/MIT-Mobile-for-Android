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
				LocationRecord location = new LocationRecord();
				location.id = cursor.getString(1);
				location.name = cursor.getString(2);
				location.lat_wgs84 = cursor.getString(3);
				location.long_wgs84 = cursor.getString(4);
				location.bldgnum = cursor.getString(5);
				location.last_updated = cursor.getString(6);

				Global.sharedData.getFacilitiesData().setLocationId(location.id);
				Global.sharedData.getFacilitiesData().setBuildingNumber(location.bldgnum);

				// If there is no building number for the selected location, go to the inside/outside selection activity, else retrieve the rooms for the location
				if (location.bldgnum == null || location.bldgnum.equals("")) {
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
	