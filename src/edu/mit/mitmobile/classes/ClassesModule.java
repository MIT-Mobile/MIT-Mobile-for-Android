package edu.mit.mitmobile.classes;

import android.app.Activity;

import edu.mit.mitmobile.Module;
import edu.mit.mitmobile.R;

public class ClassesModule extends Module {

	@Override
	public String getLongName() {
		return "MIT Stellar";
	}

	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return CoursesTopActivity.class;
	}

	@Override
	public String getShortName() {
		return "Stellar";
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.menu_stellar;
	}
	
	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_stellar;
	}
}
