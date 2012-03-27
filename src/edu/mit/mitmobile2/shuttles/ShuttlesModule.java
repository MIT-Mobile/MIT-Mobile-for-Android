package edu.mit.mitmobile2.shuttles;

import java.util.List;

import android.app.Activity;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class ShuttlesModule extends NewModule {

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
	protected boolean onItemSelected(Activity activity, String id) {
		// TODO Auto-generated method stub
		return false;
	}
}
