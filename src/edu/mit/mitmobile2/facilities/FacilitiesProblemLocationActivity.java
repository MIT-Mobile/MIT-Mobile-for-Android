package edu.mit.mitmobile2.facilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.FacilitiesItem.CategoryRecord;

//public class FacilitiesProblemLocationActivity extends ListActivity implements OnClickListener, OnItemClickListener {

public class FacilitiesProblemLocationActivity extends ModuleActivity {

	public static final String TAG = "FacilitiesProblemLocationActivity";

	Context mContext;
	ListView mListView;
	FacilitiesDB db;
	private AutoCompleteTextView facilitiesTextLocation;
	private Button useMyLocationButton;
	
	//ArrayAdapter<String> adapter;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate()");
		
		mContext = this;
		Handler uiHandler = new Handler();
		db = FacilitiesDB.getInstance(mContext);

		createViews();

	}

	public void createViews() {
        setContentView(R.layout.facilities_problem_location);

        // Set up location search

        // Set up use my location button
		TwoLineActionRow useMyLocationActionRow = (TwoLineActionRow) findViewById(R.id.facilitiesUseMyLocationActionRow);
		String title1 = "Use My Location";
		String title2 = "";
		useMyLocationActionRow.setTitle(title1 + " " + title2, TextView.BufferType.SPANNABLE);
		useMyLocationActionRow.setActionIconResource(R.drawable.arrow_right_normal);
		useMyLocationActionRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, FacilitiesProblemLocationActivity.class);
				startActivity(intent);
			}
		});
        
        // Set up browse by location
		///locationCategories = db.getCategoryArray();
		///setListAdapter(new ArrayAdapter<String>(this,R.layout.simple_row,db.getCategoryArray()));

		final FacilitiesDB db = FacilitiesDB.getInstance(this);
		CategoryAdapter adapter = new CategoryAdapter(this, db.getCategoryCursor());
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
			
				//Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + numericPhone));
				//startActivity(intent);
			}
		});
		
		
		
	}

	public boolean onKeyDown(int keyCode, KeyEvent event){
	    if(keyCode == KeyEvent.KEYCODE_BACK) {
	            Intent intent = new Intent(mContext, FacilitiesActivity.class);              
	            startActivity(intent);          
	            finish();
	            return true;
	    }
	    return false;
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
	