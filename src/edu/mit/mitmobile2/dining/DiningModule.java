package edu.mit.mitmobile2.dining;

import java.util.List;

import android.app.Activity;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class DiningModule extends NewModule {

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
		return "Dining";
	}

	@Override
	public String getShortName() {
		return "Dining";
	}

	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return DiningHomeActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		return 0;
	}

	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_events;
	}

}
