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
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TitleBar;
import edu.mit.mitmobile2.objs.MapItem;


public class MITMapBrowseResultsActivity extends ModuleActivity  {

	static final String TAG = "MITMapBrowseResultsActivity";
	static final int MENU_BROWSE_OR_BKS = MENU_SEARCH + 1;
	static final int MENU_CLEAR_BKS = MENU_SEARCH + 2;
	static final int MENU_VIEW_MAP = MENU_SEARCH + 3;
	
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
        	//bookmark_action = BK_ADD;
        } else {
        	//bookmark_action = BK_RM;
        }
	    

        setContentView(R.layout.boring_list_layout);
        
        TitleBar titleBar = (TitleBar) findViewById(R.id.boringListTitleBar);
        
        if(mCategoryName != null) {
        	titleBar.setTitle(mCategoryName);
        	bookmarks_mode = false;
        } else {
        	titleBar.setTitle("Bookmarks");
        	//titleBar.setTitle("Map Results");
        	bookmarks_mode = true;
        }
	    
    	
		mListView = (ListView) findViewById(R.id.boringListLV);
		mEmptyMessageTV = (TextView) findViewById(R.id.boringListEmptyTV);
		mEmptyMessageTV.setText(getResources().getString(R.string.map_no_bookmarks));
		mLoaderView = (FullScreenLoader) findViewById(R.id.boringListLoader);
		
		//lv.setLongClickable(true);
		//lv.setOnLongClickListener(this);  // FIXME only regular or long click possible, not both
		
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
		
		adapter = new MapItemsAdapter(this, results);
		
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
	
	/****************************************************/
	@Override
	protected Dialog onCreateDialog(int id) {

		String[] options = {"View",bookmark_action,"Google Maps"};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Action");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				Log.d(TAG,"item = " + item);
				Intent i;
				switch (item) {
				case 0:
					// View
					// we would have to launch the MapDetailsActivity here
					break;
				case 1:
					// Bookmark
					if (bookmark_action.equals(BK_ADD)) {
						mDB.saveMapItem(curMapItem);
					} else {
						mDB.delete(curMapItem);
					}
					break;
				case 2:
					// Google
					String uri = "geo:0,0?q="+curMapItem.name+"+near+"+curMapItem.street+",Cambridge,MA";
					i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
					startActivity(i);
					break;
				}
			}
		});

		AlertDialog alert = builder.create();

		return alert;
		
	}
	/****************************************************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
		case MENU_MODULE_HOME: 
			Log.d(TAG,"menu_module_home");
			if (results==null) return false;
			Intent i = new Intent(this, MITMapActivity.class);
			i.putParcelableArrayListExtra(MITMapActivity.MAP_ITEMS_KEY, new ArrayList<MapItem>(results));
			
			startActivity(i);
			return true;
			
		case MENU_BROWSE_OR_BKS:
			Log.d(TAG,"MENU_BROWSE_OR_BKS");

			//Cursor c  = (Cursor) mListView.getSelectedItem();
			int selected = mListView.getSelectedItemPosition();
			if (selected<0) return false;
			
			if (bookmark_action.equals(BK_ADD)) {
				curMapItem = results.get(selected);
				mDB.saveMapItem(curMapItem);
			} else {
				Cursor c = (Cursor) mListView.getItemAtPosition(selected);
				curMapItem = MapsDB.retrieveMapItem(c);
				mDB.delete(curMapItem);
				c.close();
				showBookmarks();
			}
		
			return true;
			
		case MENU_CLEAR_BKS: 
			mDB.clearAll();
			showBookmarks();
			return true;
			
		case MENU_VIEW_MAP: 
			Log.d(TAG,"menu_view_map");
			//MITMapActivity.launchNewMapItems(this, results);
			MITMapActivity.launchNewMapItems(this, results);
			break;
		
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		super.onPrepareOptionsMenu(menu);
	
		//menu.add(0, MENU_BROWSE_OR_BKS, Menu.NONE, bookmark_action)
		//  .setIcon(R.drawable.menu_bookmarks);
		
		if (bookmarks_mode) {
			Cursor bookmarkCursor = mDB.getMapsCursor();	
			if (bookmarkCursor.getCount()>0) {
				menu.add(0, MENU_CLEAR_BKS, Menu.NONE, "Clear Bookmarks")
				  .setIcon(R.drawable.menu_bookmarks);
			}
			bookmarkCursor.close();
		}
		
		return true;
	}

	@Override
	protected Module getModule() {
		return new MapsModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		menu.add(0, MENU_VIEW_MAP, Menu.NONE, "View on Map")
		  .setIcon(R.drawable.menu_view_on_map);		
	}
	 
	/*
	@Override
	public boolean onLongClick(View v) {
		
		longClickPos = mListView.getPositionForView(v);
		
		if (longClickPos<0) return false;

		if (bookmark_action.equals(BK_ADD)) 
			curMapItem = results.get(longClickPos);
		else {
			Cursor c = (Cursor) mListView.getItemAtPosition(longClickPos);
			curMapItem = mDB.retrieveMapItem(c);
		}
		
		showDialog(0);
		
		return true;
	}
	*/
}
