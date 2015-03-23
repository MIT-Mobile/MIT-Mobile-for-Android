package edu.mit.mitmobile2.dining;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.TabConfigurator;
import edu.mit.mitmobile2.dining.DiningModel.DiningHall;
import edu.mit.mitmobile2.dining.DiningModel.DiningHallLocation;
import edu.mit.mitmobile2.dining.DiningModel.DiningVenues;
import edu.mit.mitmobile2.maps.MITMapView;
import edu.mit.mitmobile2.R;


public class DiningMapActivity extends NewModuleActivity implements TabHost.OnTabChangeListener{
	
	public static final String sHouseTab = "HOUSE DINING";
	public static final String sRetailTab = "RETAIL";
	
	private MITMapView mMapView;
	TabHost mTabHost;
	private DiningVenues mDiningVenues;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dining_map);
		
		mDiningVenues = DiningModel.getDiningVenues();
		if (mDiningVenues == null) {
			// fail gracefully
			finish();
			return;
		}
		
		mTabHost = (TabHost) findViewById(R.id.diningMapTabHost);
		mTabHost.setup();
		TabConfigurator tabConfigurator = new TabConfigurator(this, mTabHost);
		tabConfigurator.addTab(sHouseTab, android.R.id.tabcontent);
		tabConfigurator.addTab(sRetailTab, android.R.id.tabcontent);
		tabConfigurator.setUnderlineColor(getResources().getColor(R.color.diningTabUnderline));
		tabConfigurator.setTextStyleResID(R.style.DiningTabTitle);
		tabConfigurator.configureTabs();
		mTabHost.setOnTabChangedListener(this);
		
		mMapView = (MITMapView) findViewById(R.id.diningMapView);
		
		String tabIndex = getIntent().getStringExtra(DiningHomeActivity.SELECTED_TAB);
		mTabHost.setCurrentTabByTag(tabIndex);
		
		onTabChanged(mTabHost.getCurrentTabTag());
	}
	
	// onTabChangedListener
	@Override
	public void onTabChanged(String tabId) {
		Log.d("TAB", "Tab changed to id : " + tabId);
		mMapView.clearMapItems();
		if (tabId.equals(sHouseTab)) {
			annotateHouseVenues();
		} else if (tabId.equals(sRetailTab)) {
			annotateRetailVenues();
		}
		if (mMapView.isLoaded()) {
			mMapView.syncGraphicsLayers();
		}
	}
	
	private void annotateHouseVenues() {
		boolean needsRecenter = false;
		for (DiningHall hall : mDiningVenues.getHouses()) {
			DiningHallLocation location = hall.getLocation();
			if (location.getMapPoints().size() > 0) {
				mMapView.addMapItem(location);
				needsRecenter = true;
			}
		}
		if (needsRecenter) {
			mMapView.fitMapItems();
		}
	}
	
	private void annotateRetailVenues() {
		mMapView.pause();
		boolean needsRecenter = false;
		Map<String, List<? extends DiningHall>> retailVenues = mDiningVenues.getRetail();
		for (String buildingID : mDiningVenues.getRetailBuildingNumbers()) {
			for (DiningHall hall : retailVenues.get(buildingID)) {
				DiningHallLocation location = hall.getLocation();
				if (location.getMapPoints().size() > 0) {
					mMapView.addMapItem(location);
					needsRecenter = true;
				}
			}
		}
		if (needsRecenter) {
			mMapView.fitMapItems();
		}
		mMapView.unpause();
	}
	
	@Override
	public void onBackPressed() {
		Intent returnIntent = new Intent();
		returnIntent.putExtra(DiningHomeActivity.SELECTED_TAB, mTabHost.getCurrentTabTag());
		setResult(RESULT_OK, returnIntent);        
		finish();
	}
	
	@Override
	protected NewModule getNewModule() {
		return new DiningModule();
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}

	@Override
	protected List<String> getMenuItemBlackList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(DiningModule.FILTER_ITEM_ID);
		list.add(DiningModule.MAPVIEW_ITEM_ID);
		return list;
	}
	
	@Override
	protected void onOptionSelected(String optionId) {
		if (optionId.equals(DiningModule.LISTVIEW_ITEM_ID)) {
			onBackPressed();
		}
	}

	@Override
	protected boolean isModuleHomeActivity() {
		return false;
	}

}
