package edu.mit.mitmobile.emergency;

import android.app.Activity;

import edu.mit.mitmobile.Module;
import edu.mit.mitmobile.R;

public class EmergencyModule extends Module {

	@Override
	public String getLongName() {
		return "Emergency Info";
	}

	@Override
	public String getShortName() {
		return "Emergency";
	}
	
	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return EmergencyActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.menu_emergency;
	}

	@Override
	public String getMenuOptionTitle() {
		return "Emergency Info";
	}
	
	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_emergency;
	}
}
