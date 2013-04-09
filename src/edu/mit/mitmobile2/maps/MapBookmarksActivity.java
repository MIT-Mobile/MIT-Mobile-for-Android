package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.MITPlainSecondaryTitleBar;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TitleBar;
import edu.mit.mitmobile2.objs.MapItem;

public class MapBookmarksActivity extends NewModuleActivity {

	@Override
	protected List<MITMenuItem> getPrimaryMenuItems() {
		// TODO Auto-generated method stub
		return null;
	}

	private MapModel mMapModel;
	Cursor mMapsCursor;
	
	//TitleBar boringListTitleBar;
	ListView mListView;
	TextView mEmptyMessageTV;
	FullScreenLoader mLoaderView;
	ArrayList<MapItem> mapItems;
	MapsDB db;
	Context context;
	
	private final static String TAG = "MapBookmarksActivity";
	private final static String MENU_CLEAR_BOOKMARKS = "menu_clear_bookmarks";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = this;
		mapItems = new ArrayList<MapItem>();

		setContentView(R.layout.map_bookmarks_list);	
		initSecondaryTitleBar();
		mListView = (ListView)findViewById(R.id.mapBookmarksLV);
		mEmptyMessageTV = (TextView)findViewById(R.id.mapBookmarksListEmptyTV);
		mLoaderView = (FullScreenLoader)findViewById(R.id.mapBookmarksListLoader);
		//boringListTitleBar = (TitleBar)findViewById(R.id.boringListTitleBar);
	
		db = MapsDB.getInstance(this);
		
		mLoaderView.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.GONE);
		mMapModel = new MapModel(context);
		mMapModel.getBookmarks(getBookmarksHandler);						
	}
	
	@Override
	protected List<MITMenuItem> getSecondaryMenuItems() {
		ArrayList<MITMenuItem> items = new ArrayList<MITMenuItem>();
		if (mapItems.size() > 0) {
			items.add(new MITMenuItem(MENU_CLEAR_BOOKMARKS, "Clear Bookmarks"));
		}
		return items;
	}
	
	private void initSecondaryTitleBar() {
		final MITPlainSecondaryTitleBar titleBar = new MITPlainSecondaryTitleBar(this);
		titleBar.setTitle("Bookmarks");
		getTitleBar().addSecondaryBar(titleBar);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//updateUI();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//mMapsCursor.close();
	}
	
	private void updateUI() {				
	
		MapItemArrayAdapter adapter = new MapItemArrayAdapter(context, mapItems, null);
		mListView.setAdapter(adapter);

		mListView.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> listView, View view, int position, long id) {					
					
						Intent i = new Intent(context, MITMapDetailsSliderActivity.class);
    	            	i.putParcelableArrayListExtra(MITMapView.MAP_DATA_KEY, (ArrayList<? extends Parcelable>) mapItems);
    	            	i.putExtra(MITMapView.MAP_ITEM_INDEX_KEY, position);
    	            	context.startActivity(i);
					}
				}
		);

		
		if (mapItems.size() > 0) {
			mListView.setVisibility(View.VISIBLE);			
			mEmptyMessageTV.setVisibility(View.GONE);
		}
		else {
			mEmptyMessageTV.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);			
		}
		
		mLoaderView.setVisibility(View.GONE);

		refreshTitleBarOptions();
	}
	
	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return new MapsModule();
	}

	@Override
	protected boolean isScrollable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		if (optionId.equals(MENU_CLEAR_BOOKMARKS)) {
			mMapModel.clearAllBookmarks(new Handler() {
				@Override
				public void handleMessage(Message msg) {
					mapItems.clear();
					updateUI();
				}
			});			
		}
		
	}
	
    public Handler getBookmarksHandler = new Handler() {
        @SuppressWarnings("unchecked")
		@Override
        public void handleMessage(Message msg) {
            	try {
            		mapItems = (ArrayList<MapItem>)msg.obj;
            		Toast.makeText(context, mapItems.size() + " bookmarks", Toast.LENGTH_LONG).show();
            		updateUI();
            	}
            	catch (Exception e) {
            		Log.d(TAG,"getBookmarksHandler exception");
            		Log.d(TAG,e.getStackTrace().toString());
            	}
        }
    };

}
