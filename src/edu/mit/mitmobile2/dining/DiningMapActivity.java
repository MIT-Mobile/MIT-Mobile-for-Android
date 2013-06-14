package edu.mit.mitmobile2.dining;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.TabConfigurator;
import edu.mit.mitmobile2.dining.DiningModel.DiningHall;
import edu.mit.mitmobile2.maps.MITMapView;
import edu.mit.mitmobile2.R;


public class DiningMapActivity extends NewModuleActivity implements TabHost.OnTabChangeListener{
	
	public static final String sHouseTab = "HOUSE DINING";
	public static final String sRetailTab = "RETAIL";
	
	private MITMapView mMapView;
	TabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dining_map);
		
		mTabHost = (TabHost) findViewById(R.id.diningMapTabHost);
		mTabHost.setup();
		TabConfigurator tabConfigurator = new TabConfigurator(this, mTabHost);
		tabConfigurator.addTab(sHouseTab, android.R.id.tabcontent);
		tabConfigurator.addTab(sRetailTab, android.R.id.tabcontent);
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
		mMapView.fitMapItems();
		if (mMapView.isLoaded()) {
			mMapView.syncGraphicsLayers();
		}
	}
	
	private void annotateHouseVenues() {
		for (DiningHall hall : DiningModel.getDiningVenues().getHouses()) {
			mMapView.addMapItem(hall.getLocation());
		}
	}
	
	private void annotateRetailVenues() {
		for (DiningHall hall : DiningModel.getDiningVenues().getRetail()) {
			mMapView.addMapItem(hall.getLocation());
		}
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
