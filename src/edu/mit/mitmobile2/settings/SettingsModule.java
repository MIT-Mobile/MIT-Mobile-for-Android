package edu.mit.mitmobile2.settings;

import java.util.List;

import android.app.Activity;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;

public class SettingsModule extends NewModule {

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

	@Override
	public String getLongName() {
		return "Settings";
	}

	@Override
	public String getShortName() {
		return "Settings";
	}

	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return MITSettingsActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		return 0;
	}

	@Override
	public int getHomeIconResourceId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
