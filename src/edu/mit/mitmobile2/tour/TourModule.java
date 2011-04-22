package edu.mit.mitmobile2.tour;

import android.app.Activity;

import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;

public class TourModule extends Module {

	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_tour;
	}

	@Override
	public String getLongName() {
		return "Campus Tour";
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.menu_tour;
	}

	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return MainTourActivity.class;
	}

	@Override
	public String getShortName() {
		return "Tours";
	}

}
