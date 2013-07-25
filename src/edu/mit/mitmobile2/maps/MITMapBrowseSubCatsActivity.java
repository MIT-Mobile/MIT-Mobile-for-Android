package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.MapCatItem;

public class MITMapBrowseSubCatsActivity extends NewModuleActivity {

	static final String TAG = "MITMapBrowseSubCatsActivity";	
	private static String MENU_BOOKMARKS = "bookmarks";
	static final String CATEGORY_NAME_KEY = "category_name";
	private String mCategoryName;
	Context mContext;
	
	ArrayList<MapCatItem> mSubCats;
	List<String> subcategory_names;
	
	
	/****************************************************/
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate()");
	
	    super.onCreate(savedInstanceState);
	    
	    mContext = this;
	    
    	Bundle extras = getIntent().getExtras();
        if (extras!=null){  
        	mCategoryName = extras.getString(CATEGORY_NAME_KEY);
        } 
        
        mSubCats = Global.curSubCats;
        
        if (mSubCats == null) {
        	// categories flushed from memory
        	finish();
        	return;
        }
	            
        createView();
		
	}
		

	void createView() {

		setContentView(R.layout.map_browse_cats);
        addSecondaryTitle("Browse by " + mCategoryName);        
		
		
		// hide loader
		findViewById(R.id.boringListLoader).setVisibility(View.GONE);
		
		ListView listView = (ListView) findViewById(R.id.boringListLV);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View row, int position, long id) {
				Intent i = new Intent(MITMapBrowseSubCatsActivity.this, MITMapBrowseResultsActivity.class); 
				i.putExtra(MITMapBrowseResultsActivity.KEY_CAT, mSubCats.get(position).categoryId);
				i.putExtra(MITMapBrowseResultsActivity.CATEGORY_NAME_KEY, mSubCats.get(position).categoryName);

				startActivity(i);
			}
		});
		
		listView.setVisibility(View.VISIBLE);
		ArrayAdapter<MapCatItem> subCategoriesAdapter = new SubCategoriesAdapter(this, mSubCats);
		listView.setAdapter(subCategoriesAdapter);		
	}
	
	private static class SubCategoriesAdapter extends SimpleArrayAdapter<MapCatItem> {

		public SubCategoriesAdapter(Context context, List<MapCatItem> items) {
			super(context, items, R.layout.boring_action_row);
		}

		@Override
		public void updateView(MapCatItem catItem, View view) {
			TwoLineActionRow row = (TwoLineActionRow) view;
			row.setTitle(catItem.categoryName);
		}
	}
	


	
	@Override
	protected NewModule getNewModule() {
		return new MapBrowseCatsModule();
	}


	@Override
	protected boolean isScrollable() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	protected void onOptionSelected(String id) {
	    if (id.equals(MENU_BOOKMARKS)) {
			Intent i = new Intent(mContext, MapBookmarksActivity.class); 
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mContext.startActivity(i);
	    }
	}


	@Override
	protected boolean isModuleHomeActivity() {
		// TODO Auto-generated method stub
		return false;
	}
	

}






