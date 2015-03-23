package edu.mit.mitmobile2.events;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;

import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class EventsModule extends NewModule {

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

	@Override
	public List<MITMenuItem> getPrimaryOptions() {
		return Arrays.asList(
			new MITMenuItem("search", "Search", R.drawable.menu_search)
		);
	}
	
	@Override
	public List<MITMenuItem> getSecondaryOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onItemSelected(Activity activity, String id) {
		if (id.equals("search")) {
		    activity.onSearchRequested();
		    return true;
		}
		return false;
	}
}
