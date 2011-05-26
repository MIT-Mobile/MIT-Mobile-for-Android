package edu.mit.mitmobile2.facilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.FacilitiesItem.CategoryRecord;
import edu.mit.mitmobile2.objs.FacilitiesItem.LocationCategoryRecord;


public class FacilitiesLocationsForCategoryActivity extends ModuleActivity {
	public static final String TAG = "FacilitiesLocationsForCategoryActivity";

	Context mContext;
	ListView mListView;
	FacilitiesDB db;

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
//				LocationCategoryRecord locationCategory = db.getLocationCategory(position);
//				Log.d(TAG,"position = " + position + " location_id = " + locationCategory.locationId + " category_id = " + locationCategory.categoryId);
//				// save the selected category
//				Global.sharedData.getFacilitiesData().setLocationCategory(category.name);
//				Intent intent = new Intent(mContext, FacilitiesLocationsForCategoryActivity.class);
//				startActivity(intent);
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

	
//	public void onListItemClick(ListView parent, View v,int position, long id) {   
//    	Toast.makeText(this, "You have selected " + locationTypes[position],Toast.LENGTH_SHORT).show();
//    } 
//    
//	public void onClick(View v) {
//		Log.d(TAG, "clicked " + v.getId());
//    	Toast.makeText(this, "You have clicked " + v.getId(),Toast.LENGTH_SHORT).show();
//    	Log.d(TAG, "autocomplete selected clicked " + v.getId());
//	}
//
//	@Override
//	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	final TextWatcher textWatcher = new TextWatcher() {
//		public void afterTextChanged(Editable s) {
//			Log.d(TAG, "after text changed()");
//		}
//
//		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//	
//		public void onTextChanged(CharSequence s, int start, int before, int count) {
//			updateAdapter(s, adapter, facilitiesTextLocation);
//	    }
//	};
//
//	private void updateAdapter(CharSequence s, ArrayAdapter<String> adapter, AutoCompleteTextView aCT) {
//		facilitiesTextLocationValues = db.getLocationSuggestionArray(s.toString());
//        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, facilitiesTextLocationValues);
//        adapter.setNotifyOnChange(true);
//        aCT.setAdapter(adapter);
//	}
	
}
	