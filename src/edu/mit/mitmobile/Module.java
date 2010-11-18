package edu.mit.mitmobile;

import android.app.Activity;

public abstract class Module {

	abstract public String getLongName();
	
	abstract public String getShortName();

	abstract public Class<? extends Activity> getModuleHomeActivity();
	
	abstract public int getMenuIconResourceId();
	
	abstract public int getHomeIconResourceId();
	
	public String getMenuOptionTitle() {
		return getShortName() + " Home";
	}
}
