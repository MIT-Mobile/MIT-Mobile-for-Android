package edu.mit.mitmobile2.events;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.Activity;
import android.content.Context;

import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;

public class EventsModule extends Module {

	@Override
	public String getShortName() {
		return "Events";
	}
	
	@Override
	public String getLongName() {
		return "Events Calendar";
	}

	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return EventsTopActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.menu_events;
	}

	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_events;
	}
	
	@Override
	public void handleUrl(Context context, String url) {
		if(url.startsWith("mitmobile://calendar/?source=")) {
			try {
				String eventSource = URLDecoder.decode(url.substring("mitmobile://calendar/?source=".length()), "UTF-8");
				MITEventsDaysSliderActivity.launchEventType(context, eventSource, null);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
