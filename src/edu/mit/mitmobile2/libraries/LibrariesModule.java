package edu.mit.mitmobile2.libraries;

import android.app.Activity;

import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;

public class LibrariesModule extends Module {

	@Override
	public String getLongName() {
		return "Libraries";
	}

	@Override
	public String getShortName() {
		return "Libraries";
	}
	
	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return LibraryActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.menu_libraries;
	}

	@Override
	public String getMenuOptionTitle() {
		return "Libraries";
	}
	
	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_libraries;
	}
}
