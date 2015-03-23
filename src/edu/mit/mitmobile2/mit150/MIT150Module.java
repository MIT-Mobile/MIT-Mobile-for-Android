package edu.mit.mitmobile2.mit150;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;

public class MIT150Module extends Module {

	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_mit150;
	}

	@Override
	public String getLongName() {
		return "MIT150";
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.menu_mit150;
	}

	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return MainMIT150Activity.class;
	}

	@Override
	public String getShortName() {
		return "MIT150";
	}

	@Override
	public void handleUrl(Context context, String url) {
		if (url.startsWith("mitmobile://mit150/about")) {
			Intent i = new Intent(context, MIT150WelcomeActivity.class);
			context.startActivity(i);
		} else if (url.startsWith("mitmobile://mit150/corridor")) {
			Intent i = new Intent(context,CorridorListActivity.class);
			context.startActivity(i);
		}
	}
}
