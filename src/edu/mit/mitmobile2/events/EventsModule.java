package edu.mit.mitmobile2.events;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class EventsModule extends NewModule {

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
		else if(url.startsWith("mitmobile://calendar/category?")) {
			String query = url.substring("mitmobile://calendar/category?".length());
			String[] parts = query.split("&");
			HashMap<String, String> params = new HashMap<String, String>();
			for(String part : parts) {
				String[] keyValuePair = part.split("=");
				try {
					params.put(keyValuePair[0], URLDecoder.decode(keyValuePair[1], "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return;
				}
			}
			
			if(params.containsKey("catID")) {
				int catID = Integer.parseInt(params.get("catID"));
				String title = params.get("title");
				String eventType = params.get("listID");
				
				// hard code open house time
				Long startTime = null;
				if(eventType.equals("OpenHouse")) {
					startTime = 1304179200L * 1000; // April 30th, 2011
				}
				MITEventsDaysSliderActivity.launchCategory(context, catID, title, eventType, startTime);
			}
			else if(params.containsKey("listID")) {
				EventsSimpleCategoryActivity.launch(context, params.get("listID"));
			}
		}
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
