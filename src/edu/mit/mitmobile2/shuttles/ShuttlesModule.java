package edu.mit.mitmobile2.shuttles;

import android.app.Activity;

import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;

public class ShuttlesModule extends Module {

	@Override
	public String getLongName() {
		return "Shuttles";
	}

	@Override
	public String getShortName() {
		return "Shuttles";
	}
	
	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return ShuttlesActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.menu_shuttles;
	}

	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_shuttles;
	}
}
