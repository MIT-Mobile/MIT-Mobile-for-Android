package edu.mit.mitmobile2.news;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;

import edu.mit.mitmobile2.MITMenuItem;
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
	public List<MITMenuItem> getPrimaryOptions() {
		ArrayList<MITMenuItem> items = new ArrayList<MITMenuItem>();
		items.add(new MITMenuItem("search", "Search", R.drawable.menu_search));
		return items;
	}

	@Override
	public List<MITMenuItem> getSecondaryOptions() {
		ArrayList<MITMenuItem> items = new ArrayList<MITMenuItem>();
		items.add(new MITMenuItem("bookmarks", "Bookmarks"));
		return items;
	}

	@Override
	public boolean onItemSelected(Activity activity, String id) {
		if(id.equals("bookmarks")) {
			Intent intent = new Intent(activity, NewsBookmarksActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			activity.startActivity(intent);
			return true;
		} else if(id.equals("search")) {
			activity.onSearchRequested();
		}
		return false;
	}
}
