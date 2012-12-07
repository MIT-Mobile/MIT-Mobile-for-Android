package edu.mit.mitmobile2.emergency;

import java.util.List;

import android.app.Activity;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class EmergencyModule extends NewModule {

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

	@Override
	public List<MITMenuItem> getPrimaryOptions() {
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
