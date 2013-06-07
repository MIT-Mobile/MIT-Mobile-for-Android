package edu.mit.mitmobile2.dining;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TabConfigurator;
import edu.mit.mitmobile2.maps.MITMapView;

public class DiningMapActivity extends NewModuleActivity implements TabHost.OnTabChangeListener{
	
	public static final String sHouseTab = "HOUSE DINING";
	public static final String sRetailTab = "RETAIL";
	
	private MITMapView mMapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dining_map);
		
		TabHost tabHost = (TabHost) findViewById(R.id.diningMapTabHost);
		tabHost.setup();
		TabConfigurator tabConfigurator = new TabConfigurator(this, tabHost);
		tabConfigurator.addTab(sHouseTab, android.R.id.tabcontent);
		tabConfigurator.addTab(sRetailTab, android.R.id.tabcontent);
		tabConfigurator.configureTabs();
		tabHost.setOnTabChangedListener(this);
		
		mMapView = (MITMapView) findViewById(R.id.diningMapView);
		
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
	}
	
	private void annotateHouseVenues() {
		
		
	}
	
	private void annotateRetailVenues() {
		
		
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
			Intent i = new Intent(this, DiningHomeActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
	}

	@Override
	protected boolean isModuleHomeActivity() {
		return false;
	}

}
