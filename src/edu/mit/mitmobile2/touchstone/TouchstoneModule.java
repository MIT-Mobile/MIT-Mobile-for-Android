package edu.mit.mitmobile2.touchstone;

import java.util.List;

import android.app.Activity;

import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class TouchstoneModule extends NewModule {

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

	@Override
	public List<MITMenuItem> getPrimaryOptions() {
		return null;
	}

	@Override
	public List<MITMenuItem> getSecondaryOptions() {
		return null;
	}

	@Override
	public boolean onItemSelected(Activity activity, String id) {
		return false;
	}
}
