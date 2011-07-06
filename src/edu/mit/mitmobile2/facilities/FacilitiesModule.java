package edu.mit.mitmobile2.facilities;

import android.app.Activity;

import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;

public class FacilitiesModule extends Module {

	@Override
	public String getLongName() {
		return "Bldg Services";
	}

	@Override
	public String getShortName() {
		return "Bldg Services";
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
		return "Bldg Services";
	}
	
	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_facilities;
	}
}
