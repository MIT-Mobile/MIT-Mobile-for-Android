package edu.mit.mitmobile2.facilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.FacilitiesItem.CategoryRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationCategoryRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationRecord;


public class FacilitiesLocationsForCategoryActivity extends ModuleActivity {
	public static final String TAG = "FacilitiesLocationsForCategoryActivity";

	Context mContext;
	ListView mListView;
	FacilitiesDB db;
	Handler uiHandler = new Handler();
	//ArrayAdapter<String> adapter;
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
		//LocationCategoryAdapter adapter = new LocationCategoryAdapter(this, db.getLocationCategoryCursor());
		LocationAdapter adapter = new LocationAdapter(this, db.getLocationCategoryCursor());
		Log.d(TAG,"num records in adapter = " + adapter.getCount());
		ListView listView = (ListView) findViewById(R.id.facilitiesProblemLocationsForCategoryListView);
		listView.setAdapter(adapter);
		listView.setVisibility(View.VISIBLE);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				LocationRecord location = db.getLocation(position);
//				Log.d(TAG,"position = " + position + " location_id = " + locationCategory.locationId + " category_id = " + locationCategory.categoryId);
//				// save the selected category
				Global.sharedData.getFacilitiesData().setLocationId(location.id);
				Global.sharedData.getFacilitiesData().setBuildingNumber(location.bldgnum);
				
				// check to see if rooms for the selected location have already been retrieved and retrieve them if necesary
				if (location.last_updated == null || location.last_updated.equals("")) {
					Log.d(TAG,"retrieving rooms for " + location.bldgnum);
					FacilitiesDB.updateRooms(mContext, uiHandler, location.bldgnum);
				}
				else {
					Log.d(TAG,"rooms for " + location.bldgnum + " already retrieved");
				}
				Intent intent = new Intent(mContext, FacilitiesRoomLocationsActivity.class);
				startActivity(intent);
			}
		});
		
		
		
	}

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

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
	}

}
	