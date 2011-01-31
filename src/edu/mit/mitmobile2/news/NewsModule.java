package edu.mit.mitmobile2.news;

import android.app.Activity;

import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;

public class NewsModule extends Module {

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
		return NewsListSliderActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.menu_news;
	}

	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_news;
	}
}
