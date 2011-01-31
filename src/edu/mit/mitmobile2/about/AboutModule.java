package edu.mit.mitmobile2.about;

import android.app.Activity;

import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;

public class AboutModule extends Module {

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

}
