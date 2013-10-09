package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.JSONParser;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.MapCatItem;

public class MITMapBrowseCatsActivity extends NewModuleActivity {

	public static final String TAG = "MITMapBrowseCatsActivity";
	private static String MENU_BOOKMARKS = "bookmarks";

	//public static final String KEY_TITLE = "title";

	//static final int MENU_BOOKMARKS  = MENU_SEARCH + 1;

	protected FullScreenLoader mLoader;
	Context mContext;
	
	/*
	String[] categories = {
		"Building Number",	
		"Building Name",	
		"Selected Rooms",	
		"Food Service",	
		"Libraries",	
		"Residences",	
		"Parking Lots",	
		"Streets and Landmarks",	
		"Courts and Green Spaces",	
		"Athena Clusters",	
		"Museum and Galleries",	
		"Hotels"
	};
	*/
	
	ArrayList<MapCatItem> cats;
	String[] categories;
	//private MITPlainSecondaryTitleBar mSecondaryTitleBar;
	
	/****************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate()");
	    super.onCreate(savedInstanceState);

	    mContext = this;

	    
    	Bundle extras = getIntent().getExtras();
        if (extras!=null){  
        	//snippet = extras.getString(KEY_SNIPPET); 
        } else {
    		
        }

		setContentView(R.layout.map_browse_cats);
        addSecondaryTitle("Browse Map");        
        mLoader = (FullScreenLoader) findViewById(R.id.boringListLoader);
        mLoader.showLoading();
        
        fetchCats();

	}
	/****************************************************/
	void createView() {
		

		findViewById(R.id.boringListLoader).setVisibility(View.GONE);
		
		ListView listView = (ListView) findViewById(R.id.boringListLV);


		ArrayAdapter<String> categoriesAdapter = new CategoriesAdapter(this, Arrays.asList(categories));
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View row, int position, long id) {
				Intent i;
				Global.curSubCats = cats.get(position).subcategories;
				if (Global.curSubCats.size()<1) {
					i = new Intent(MITMapBrowseCatsActivity.this, MITMapBrowseResultsActivity.class); 
					i.putExtra(MITMapBrowseResultsActivity.KEY_CAT, cats.get(position).categoryId);
					i.putExtra(MITMapBrowseResultsActivity.CATEGORY_NAME_KEY, cats.get(position).categoryName);
				} else {
					i = new Intent(MITMapBrowseCatsActivity.this, MITMapBrowseSubCatsActivity.class); 
					i.putExtra(MITMapBrowseSubCatsActivity.CATEGORY_NAME_KEY, cats.get(position).categoryName);
				}
				
				startActivity(i);
			}
		});
		
		listView.setAdapter(categoriesAdapter);	
		listView.setVisibility(View.VISIBLE);
		
	}
	
	
	/****************************************************/
	void fetchCats() {
			
		final Runnable updateResultsUI = new Runnable() {
			@Override
			public void run() {
				if (categories==null) {
					mLoader.showError();
				} else {
					mLoader.setVisibility(View.GONE);
					createView();
				}
			}
		};
		
		final Handler myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				post(updateResultsUI);
			}
		};
		
		final JSONParser mp = new MapCategoriesParser() {
			@Override
			public void saveData() {
				if (items.size()>0) {
					cats = (ArrayList<MapCatItem>) items;
					categories = new String[items.size()];
					for (int x=0; x<cats.size(); x++) {
						categories[x] = cats.get(x).categoryName;
					}
				}
			}
		};

		
		mp.getJSONThread("?command=categorytitles", myHandler);
		
	}
	
	private static class CategoriesAdapter extends SimpleArrayAdapter<String> {

		public CategoriesAdapter(Context context, List<String> items) {
			super(context, items, R.layout.boring_action_row);
		}

		@Override
		public void updateView(String title, View view) {
			TwoLineActionRow row = (TwoLineActionRow) view;
			row.setTitle(title);
		}
	}

//	@Override
//	protected Module getModule() {
//		return new MapsModule();
//	}
	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}
//	@Override
//	protected void prepareActivityOptionsMenu(Menu menu) {
//		menu.add(0, MENU_BOOKMARKS, Menu.NONE, "Bookmarks")
//		  .setIcon(R.drawable.menu_bookmarks);
//	}
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

}

