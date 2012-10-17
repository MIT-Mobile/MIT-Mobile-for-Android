package edu.mit.mitmobile2.links;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;

public class LinksModule extends Module {

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
		return R.drawable.menu_links;
	}

	@Override
	public int getHomeIconResourceId() {
		// TODO Auto-generated method stub
		return R.drawable.home_links;
	}

	public void handleUrl(Context context, String url) {
		if (url.startsWith("mitmobile://links/")) {
			Intent i = new Intent(context, LinksActivity.class);
			context.startActivity(i);
		}
		
	}

}
