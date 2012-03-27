package edu.mit.mitmobile2.classes;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class ClassesModule extends NewModule {

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

	@Override
	public List<MITMenuItem> getPrimaryOptions() {
		// TODO Auto-generated method stub
		ArrayList<MITMenuItem> items = new ArrayList<MITMenuItem>();
		items.add(new MITMenuItem("search", "Search", R.drawable.menuitem_search));
		return items;
	}

	@Override
	public List<MITMenuItem> getSecondaryOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onItemSelected(Activity activity, String id) {
		// TODO Auto-generated method stub
		if (id.equals("search")) {
			activity.onSearchRequested();
		}
		return false;
	}
}
