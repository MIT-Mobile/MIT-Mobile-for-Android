package edu.mit.mitmobile2.news;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class NewsModule extends NewModule {

	@Override
	public String getLongName() {
		return "MIT News";
	}

	@Override
	public String getShortName() {
		return "News";
	}
	
	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return NewsListActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.menu_news;
	}

	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_news;
	}

	@Override
	protected List<MITMenuItem> getPrimaryOptions() {
		return new ArrayList<MITMenuItem>();
	}
}
