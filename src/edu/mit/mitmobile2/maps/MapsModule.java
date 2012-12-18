package edu.mit.mitmobile2.maps;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class MapsModule extends NewModule {

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
		return MITMapActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.menu_maps;
	}

	@Override
	public String getMenuOptionTitle() {
		return "Maps Home";
	}

	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_map;
	}
	
	@Override
	public void handleUrl(Context context, String url) {
		if(url.startsWith("mitmobile://map/search?")) {
			try {
				String queryTerm = URLDecoder.decode(url.substring("mitmobile://map/search?".length()), "UTF-8");
				CommonActions.searchMap(context, queryTerm);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(url.startsWith("mitmobile://map/search/")) {
			Intent i = new Intent(context, getModuleHomeActivity());
			context.startActivity(i);
		}
	}


	@Override
	public List<MITMenuItem> getPrimaryOptions() {
		// TODO Auto-generated method stub
		return Arrays.asList(
			new MITMenuItem("home", "Home", R.drawable.menu_home),
			new MITMenuItem("my_location", "My Location", R.drawable.map_current),
			new MITMenuItem("bookmarks", "Bookmarks", R.drawable.menu_bookmarks)
		);
	}


	@Override
	public List<MITMenuItem> getSecondaryOptions() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean onItemSelected(Activity activity, String id) {
		return false;
	}
		
}
