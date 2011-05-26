package edu.mit.mitmobile2.facilities;

import android.app.Activity;

import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;

public class FacilitiesModule extends Module {

	@Override
	public String getLongName() {
		return "Facilities";
	}

	@Override
	public String getShortName() {
		return "Facilities";
	}
	
	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return FacilitiesActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.menu_facilities;
	}

	@Override
	public String getMenuOptionTitle() {
		return "Facilities";
	}
	
	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_facilities;
	}
}
