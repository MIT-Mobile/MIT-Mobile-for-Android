package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TitleBar;
import edu.mit.mitmobile2.objs.MapItem;


public class MITMapBrowseResultsActivity extends NewModuleActivity  {

	static final String TAG = "MITMapBrowseResultsActivity";
	private static String MENU_BOOKMARKS = "bookmarks";

	// TODO may drop if we don't keep accelerator
	static final String BK_ADD = "Add Bookmark";
	static final String BK_RM = "Remove Bookmark";
	
	public static final String KEY_CAT = "cat";
	public static final String CATEGORY_NAME_KEY = "category_name";
	
	String cat;
	String mCategoryName;
	
	String bookmark_action;
	
	MapItemsAdapter adapter;
	
	protected boolean bookmarks_mode = false;
	
	MapItem curMapItem;
	
	ListView mListView;
	TextView mEmptyMessageTV;
	FullScreenLoader mLoaderView;
	
	int longClickPos;
	
	Context ctx;
	
	List<MapItem> results;
	
	MITMapsDataModel mdm = new MITMapsDataModel();
	
	MapsDB mDB;
	
	/*******************************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		Log.d(TAG,"onCreate");
	    super.onCreate(savedInstanceState);
	    
	    ctx = this;
	    
	    mDB =  MapsDB.getInstance(this);
	    
    	Bundle extras = getIntent().getExtras();
        if (extras!=null){  
        	cat = extras.getString(KEY_CAT); 
        	mCategoryName = extras.getString(CATEGORY_NAME_KEY);
        	bookmark_action = BK_ADD;
        } else {
        	bookmark_action = BK_RM;
        }
	    

        setContentView(R.layout.map_browse_cats);
        
        if(mCategoryName != null) {
            addSecondaryTitle(mCategoryName);        
        	bookmarks_mode = false;
        } else {
        	addSecondaryTitle("Bookmarks");
        	bookmarks_mode = true;
        }
	    
    	
		mListView = (ListView) findViewById(R.id.boringListLV);
		mEmptyMessageTV = (TextView) findViewById(R.id.boringListEmptyTV);
		mEmptyMessageTV.setText(getResources().getString(R.string.map_no_bookmarks));
		mLoaderView = (FullScreenLoader) findViewById(R.id.boringListLoader);
				
        if (cat==null) showBookmarks();
        else {
        	mLoaderView.showLoading();
        	searchCategory(cat);
        }
		
	}
	
	/****************************************************/
	@Override
	protected void onResume() {
		
		super.onResume();

		if (cat==null) {
			//adapter.notifyDataSetChanged();
			showBookmarks();  // refresh
		}
		
	}
	/****************************************************/
	
	void searchCategory(final String id) {
			
		final Runnable updateResultsUI = new Runnable() {
			@Override
			public void run() {
				createView();
			}
		};
		
		final Handler myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				
				if(msg.arg1 == MobileWebApi.SUCCESS) {
					results = MITMapsDataModel.getCategory(id);
					post(updateResultsUI);
				} else {
					mLoaderView.showError();
				}
			}

		};
		
		MITMapsDataModel.fetchCategory(id, myHandler, this);		
	}
	
	/****************************************************/
	void showBookmarks() {		
		Cursor bookmarkCursor = mDB.getMapsCursor();	
		results = new ArrayList<MapItem>();
		if(bookmarkCursor.moveToFirst()) {
			while(!bookmarkCursor.isAfterLast()) {
				results.add(MapsDB.retrieveMapItem(bookmarkCursor));
				bookmarkCursor.moveToNext();
			}
		}
		bookmarkCursor.close();
		
		createView();
	}
	
	/****************************************************/
	void createView() {

		adapter = new MapItemsAdapter(this, results,mCategoryName);
		
		mLoaderView.setVisibility(View.GONE);
		
		mListView.setAdapter(adapter);
		
		mListView.setOnItemClickListener(adapter.showOnMapOnItemClickListener());
		
		if(results.size() > 0) {
			mListView.setVisibility(View.VISIBLE);
			mEmptyMessageTV.setVisibility(View.GONE);
		} else {
			mListView.setVisibility(View.GONE);
			mEmptyMessageTV.setVisibility(View.VISIBLE);
		}
	}
	

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}


	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
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
			Intent i = new Intent(ctx, MapBookmarksActivity.class); 
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			ctx.startActivity(i);
	    }
	}	 
}
