package edu.mit.mitmobile2.tour;

import java.util.List;

import android.app.Activity;

import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class TourModule extends NewModule {

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

	@Override
	public List<MITMenuItem> getPrimaryOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MITMenuItem> getSecondaryOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onItemSelected(Activity activity, String id) {
		// TODO Auto-generated method stub
		return false;
	}

}
