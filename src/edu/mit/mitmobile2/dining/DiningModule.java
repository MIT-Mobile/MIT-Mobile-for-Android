package edu.mit.mitmobile2.dining;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class DiningModule extends NewModule {
	
	public final static String FILTER_ITEM_ID = "dining.filter"; 

	@Override
	public List<MITMenuItem> getPrimaryOptions() {
		ArrayList<MITMenuItem> list = new ArrayList<MITMenuItem>();
		list.add(new MITMenuItem(FILTER_ITEM_ID, "Filter", R.drawable.action_history));
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
