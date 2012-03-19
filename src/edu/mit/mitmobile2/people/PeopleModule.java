package edu.mit.mitmobile2.people;

import java.util.ArrayList;
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
	protected List<MITMenuItem> getPrimaryOptions() {
		// TODO Auto-generated method stub
		ArrayList<MITMenuItem> items = new ArrayList<MITMenuItem>();
		items.add(new MITMenuItem("y", "Browse", R.drawable.titlebar_action_icon));
		items.add(new MITMenuItem("x", "Browse", R.drawable.titlebar_action_icon));
		items.add(new MITMenuItem("a", "Browse", R.drawable.titlebar_action_icon));
		items.add(new MITMenuItem("b", "Browse", R.drawable.titlebar_action_icon));
		items.add(new MITMenuItem("c", "Browse", R.drawable.titlebar_action_icon));
		items.add(new MITMenuItem("d", "Browse", R.drawable.titlebar_action_icon));
		items.add(new MITMenuItem("e", "Browse", R.drawable.titlebar_action_icon));
		items.add(new MITMenuItem("f", "Browse", R.drawable.titlebar_action_icon));
		return items;
	}

}
