package edu.mit.mitmobile2.touchstone;

import android.app.Activity;

import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;

public class TouchstoneModule extends Module {

	@Override
	public String getLongName() {
		return "Touchstone";
	}

	@Override
	public String getShortName() {
		return "Touchstone";
	}
	
	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return TouchstoneActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.menu_touchstone;
	}

	@Override
	public String getMenuOptionTitle() {
		return "Touchstone";
	}
	
	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_touchstone;
	}
}
