package edu.mit.mitmobile2.links;

import java.util.List;

import android.app.Activity;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class LinksModule extends NewModule {

	@Override
	public String getLongName() {
		// TODO Auto-generated method stub
		return "Links";
	}

	@Override
	public String getShortName() {
		// TODO Auto-generated method stub
		return "Links";
	}

	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		// TODO Auto-generated method stub
		return LinksActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		// TODO Auto-generated method stub
		return R.drawable.action_external;
	}

	@Override
	public int getHomeIconResourceId() {
		// TODO Auto-generated method stub
		return R.drawable.home_news;
	}

	@Override
	public List<MITMenuItem> getPrimaryOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MITMenuItem> getSecondaryOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onItemSelected(Activity activity, String id) {
		// TODO Auto-generated method stub
		return false;
	}

}
