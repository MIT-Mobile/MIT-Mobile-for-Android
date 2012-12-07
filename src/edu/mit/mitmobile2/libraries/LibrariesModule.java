package edu.mit.mitmobile2.libraries;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class LibrariesModule extends NewModule {

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

	@Override
	public List<MITMenuItem> getPrimaryOptions() {
		ArrayList<MITMenuItem> items = new ArrayList<MITMenuItem>();
		items.add(new MITMenuItem("search", "Search", R.drawable.menu_search));
		return items;
	}

	@Override
	public List<MITMenuItem> getSecondaryOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onItemSelected(Activity activity, String id) {
	    if (id.equals("search")) {
		return activity.onSearchRequested();
	    }
	    return false;
	}
}
