package edu.mit.mitmobile2.dining;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class DiningModule extends NewModule {
	
	public final static String FILTER_ITEM_ID = "dining.filter";
	public final static String MAPVIEW_ITEM_ID = "dining.mapview"; 
	public final static String LISTVIEW_ITEM_ID = "dining.listview"; 

	@Override
	public List<MITMenuItem> getPrimaryOptions() {
		ArrayList<MITMenuItem> list = new ArrayList<MITMenuItem>();
		list.add(new MITMenuItem(FILTER_ITEM_ID, "Filter", R.drawable.menu_dining_filter));
		list.add(new MITMenuItem(MAPVIEW_ITEM_ID, "Show Map", R.drawable.menu_view_on_map));
		list.add(new MITMenuItem(LISTVIEW_ITEM_ID, "Show List", R.drawable.menu_view_as_list));
		return list;
	}

	@Override
	public List<MITMenuItem> getSecondaryOptions() {
		return null;
	}

	@Override
	public boolean onItemSelected(Activity activity, String id) {
		return false;
	}

	@Override
	public String getLongName() {
		return "Dining";
	}

	@Override
	public String getShortName() {
		return "Dining";
	}

	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return DiningHomeActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		return 0;
	}

	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_dining;
	}

}
