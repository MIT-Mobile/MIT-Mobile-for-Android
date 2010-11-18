package edu.mit.mitmobile.events;

import android.app.Activity;

import edu.mit.mitmobile.Module;
import edu.mit.mitmobile.R;

public class EventsModule extends Module {

	@Override
	public String getShortName() {
		return "Events";
	}
	
	@Override
	public String getLongName() {
		return "Events Calendar";
	}

	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return EventsTopActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.menu_events;
	}

	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_events;
	}
}
