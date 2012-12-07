package edu.mit.mitmobile2.maps;

import java.util.Date;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;
import edu.mit.mitmobile2.LoaderBar;
import edu.mit.mitmobile2.MITSearchRecentSuggestions;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.MapItem;

public abstract class MapBaseActivity extends MapActivity {

	protected MITMapView mapView;
	
	
	// used to reset out of List Mode
	public static final String KEY_VIEW_PINS = "view_pins";
	
	
	public static final String KEY_TITLE = "title";
	public static final String KEY_SNIPPET = "snippet";
	public static final String KEY_MODULE = "module";
	public static final String KEY_HEADER_TITLE = "header_title";
	public static final String KEY_HEADER_SUBTITLE = "header_subtitle";
	
	static final int MENU_HOME   = Menu.FIRST;

	public static final String KEY_POSITION = "pos";
	protected static final String SEARCH_TERM_KEY = "search";

	protected MapController mctrl;
	protected FixedMyLocation myLocationOverlay;
	protected List<MapItem> mMapItems;
	protected GeoPoint center;
	
	protected String title;
	protected String snippet;
	protected String module;
	protected int bubble_pos = -1;

	protected List<Overlay>  ovrlys;
	
	protected String mHeaderTitle = null;
	protected String mHeaderSubtitle = null;
	
	protected MITMapsDataModel mdm;
	
	protected ListView mListView;
	
	static int INIT_ZOOM = 17;
	static int INIT_ZOOM_ONE_ITEM = 18;
	
	protected Context ctx;

	protected static final String MAP_ITEMS_KEY = "map_items";
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
	    
	    ctx = this;

    	Bundle extras = getIntent().getExtras();
    	
	    setContentView(getLayoutId());
	    
        if (extras!=null){ 

        	title   = extras.getString(KEY_TITLE);   
        	snippet = extras.getString(KEY_SNIPPET);   
        	
        	module  = extras.getString(KEY_MODULE); 
        	
        	bubble_pos = extras.getInt(KEY_POSITION,-1);
        	
        	String action = getIntent().getAction();
    		if(action != null && action.equals(Intent.ACTION_SEARCH)) {
    			String findLoc = extras.getString(SearchManager.QUERY);
    			doSearch(findLoc);
    		}
        	
        	mHeaderTitle = extras.getString(KEY_HEADER_TITLE);
        	mHeaderSubtitle = extras.getString(KEY_HEADER_SUBTITLE);
        	
        
        } 
        
	    
	    mListView = (ListView) findViewById(R.id.mapListView);

	    if(mHeaderTitle != null) {
	    	findViewById(R.id.mapHeader).setVisibility(View.VISIBLE);
	    	
	    	TextView headerTitleTV = (TextView) findViewById(R.id.mapHeaderTitle);
	    	headerTitleTV.setText(mHeaderTitle);
	    	
	    	TextView headerSubtitleTV = (TextView) findViewById(R.id.mapHeaderSubtitle);
	    	if(mHeaderSubtitle != null) {
	    		headerSubtitleTV.setText(mHeaderSubtitle);
	    	} else {
	    		headerSubtitleTV.setVisibility(View.GONE);
	    	}
	    }
	    
	    mdm = new MITMapsDataModel();
	    
		mapView = (MITMapView) findViewById(R.id.mapview);
		
		mapView.setBuiltInZoomControls(true);	
		
		
	
	}
	/****************************************************/
	protected void setOverlays() {

		mctrl = mapView.getController();

		ovrlys = mapView.getOverlays();

		
		// My Location
		if(myLocationOverlay == null) {
			myLocationOverlay = new FixedMyLocation(this, mapView);
			ovrlys.add(myLocationOverlay);
			myLocationOverlay.enableMyLocation();
		}

		// possibly catch memory leaks?
		ZoomButtonsController zoomctrl = mapView.getZoomButtonsController(); 
		zoomctrl.setOnZoomListener(new ZoomButtonsController.OnZoomListener() {
		        public void onZoom(boolean zoomIn) {
		            try{
		                System.gc();
		                if(zoomIn) mctrl.zoomIn();
		                else mctrl.zoomOut();
		                System.gc();
		            }
		            catch(OutOfMemoryError e)
		            {
		                e.printStackTrace();
		            }
		            catch (Exception e)
		            {
		            }               
		        }
		        public void onVisibilityChanged(boolean visible) {
		        	
		        }
		    }
		);
		
	}
	/****************************************************/
	protected List<MapItem> loadMapItems(Intent intent) {
		return intent.getParcelableArrayListExtra(MAP_ITEMS_KEY);
	}
	/****************************************************/
	@Override
	public void onNewIntent(Intent newIntent) {
		super.onNewIntent(newIntent);
				
		String action = newIntent.getAction();
		
		if(action != null && action.equals(Intent.ACTION_SEARCH)) {
			String findLoc = newIntent.getStringExtra(SearchManager.QUERY);
			doSearch(findLoc);
		} else {			
			mListView.setVisibility(View.GONE);
			mapView.setVisibility(View.VISIBLE);
			
			if(newIntent.hasExtra(KEY_VIEW_PINS)) {
				mMapItems = loadMapItems(newIntent);
				setOverlays();
			}
		}
		
	}
	

	/****************************************************/
	protected void doSearch(final String searchTerm) {
		final LoaderBar loaderBar = (LoaderBar) findViewById(R.id.mapSearchLoader);
		loaderBar.setLoadingMessage("Searching for " + searchTerm);
		loaderBar.setFailedMessage("Search failed!");
		loaderBar.enableAnimation();
		loaderBar.startLoading();
		
		final Handler updateResultsUI = new Handler() {
			
			@Override
			public void handleMessage(Message message) {
				if(message.arg1 == MobileWebApi.SUCCESS) {
					mMapItems = MITMapsDataModel.getSearchResults(searchTerm);
					if(mMapItems.size() == 0) {
						Toast.makeText(MapBaseActivity.this, "No matches found", Toast.LENGTH_LONG).show();
					}
					setOverlays();
					loaderBar.setLastLoaded(new Date());
				} else {
					Toast.makeText(MapBaseActivity.this, MobileWebApi.NETWORK_ERROR, Toast.LENGTH_LONG).show();
					loaderBar.errorLoading();
				}
			}
		};
				
		MITSearchRecentSuggestions suggestions = new MITSearchRecentSuggestions(this, MapsSearchSuggestionsProvider.AUTHORITY, MapsSearchSuggestionsProvider.MODE);
		suggestions.saveRecentQuery(searchTerm.toLowerCase(), null);
		
		MITMapsDataModel.executeSearch(searchTerm, updateResultsUI, this);
	}
	/****************************************************/
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.lowMemory = true;
	}
	/****************************************************/
	@Override
	public void onResume() {
		super.onResume();
		if (myLocationOverlay!=null) myLocationOverlay.enableMyLocation();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (myLocationOverlay!=null) myLocationOverlay.disableMyLocation();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.stop();
	}
	/****************************************************/
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	protected int getLayoutId() {
		return R.layout.maps;
	}
	
	// TODO set configChanges attrib
	/*
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    // Checks the orientation of the screen
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	    	//
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	        //
	    }
	}
	*/
}
