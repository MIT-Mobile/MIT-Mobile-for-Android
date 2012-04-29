package edu.mit.mitmobile2.about;

import java.util.List;

import android.app.Activity;

import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class AboutModule extends NewModule {

	@Override
	public String getLongName() {
		return "About";
	}

	@Override
	public String getShortName() {
		return "About";
	}

	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return AboutActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.menu_about;
	}

	@Override
	public String getMenuOptionTitle() {
		return "About";
	}
	
	@Override
	public int getHomeIconResourceId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<MITMenuItem> getPrimaryOptions() {
		// TODO Auto-generated method stub
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
