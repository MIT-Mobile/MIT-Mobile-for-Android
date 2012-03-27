package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class NewMapModule extends NewModule {

	@Override
	public List<MITMenuItem> getPrimaryOptions() {
		ArrayList<MITMenuItem> menuItems = new ArrayList<MITMenuItem>();
		menuItems.add(new MITMenuItem("browse", "Browse", R.drawable.menu_browse));
		menuItems.add(new MITMenuItem("search", "Search", R.drawable.menu_search));
		return menuItems;
	}

	@Override
	public List<MITMenuItem> getSecondaryOptions() {
		return null;
	}

	@Override
	public boolean onItemSelected(Activity activity, String id) {
		if (id.equals("browse")) {
			// implement browse here
			return true;
		} else if (id.equals("search")) {
			// implement search here
			return true;
		}
		return false;
	}

	@Override
	public String getLongName() {
		return "Campus Map";
	}

	@Override
	public String getShortName() {
		return "Map";
	}

	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return NewMapActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_map;
	}

}
