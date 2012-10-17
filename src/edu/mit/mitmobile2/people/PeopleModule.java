package edu.mit.mitmobile2.people;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class PeopleModule extends NewModule {

	@Override
	public String getLongName() {
		return "People Directory";
	}

	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return PeopleActivity.class;
	}

	@Override
	public String getShortName() {
		return "Directory";
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.menu_directory;
	}
	
	@Override
	public String getMenuOptionTitle() {
		return "People Home";
	}

	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_people;
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
