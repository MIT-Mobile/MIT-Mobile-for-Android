package edu.mit.mitmobile.maps;

import android.app.Activity;

import edu.mit.mitmobile.Module;
import edu.mit.mitmobile.R;

public class MapsModule extends Module {

	@Override
	public String getLongName() {
		return "Campus Map";
	}


	@Override
	public String getShortName() {
		return "Map";
	}
	
	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return MITMapActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.menu_maps;
	}

	@Override
	public String getMenuOptionTitle() {
		return "Maps Home";
	}

	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_map;
	}
}
