package edu.mit.mitmobile2;

import android.app.Activity;
import android.content.Context;

public abstract class Module {

	abstract public String getLongName();
	
	abstract public String getShortName();

	abstract public Class<? extends Activity> getModuleHomeActivity();
	
	abstract public int getMenuIconResourceId();
	
	abstract public int getHomeIconResourceId();
	
	public String getMenuOptionTitle() {
		return getShortName() + " Home";
	}
	
	public void handleUrl(Context context, String url) {
		// default implementation does nothing		
	}
}
