package edu.mit.mitmobile2.links;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import edu.mit.mitmobile2.MobileWebApi;

public class LinksModel {

	private final static String LINKS_PREFS_NAME = "linksPreferences";
	
	public static void fetchLinks(final Context context, final Handler uiHandler) {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("module", "links");
		
		MobileWebApi webApi = new MobileWebApi(false, false, "Links", context, uiHandler);
		webApi.setIsSearchQuery(false);
		webApi.requestJSONArray(parameters, new MobileWebApi.JSONArrayResponseListener(
				new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(uiHandler) ) {
			
			@Override
			public void onResponse(JSONArray array) throws JSONException {
				SharedPreferences preferences = context.getSharedPreferences(LINKS_PREFS_NAME, Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putString("json", array.toString());
				editor.commit();
				
				ArrayList<LinkListItem> linkLists = new ArrayList<LinkListItem>();
				
				for (int i = 0; i < array.length(); i++) {
					linkLists.add(new LinkListItem(array.getJSONObject(i)));
				}
				
				MobileWebApi.sendSuccessMessage(uiHandler, linkLists);
			}
		});
	}
	
	public static ArrayList<LinkListItem> getCachedLinks(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(LINKS_PREFS_NAME, Context.MODE_PRIVATE);
		String json = preferences.getString("json", "");
		if (json.length() > 0) {
			JSONArray array;
			try {
				array = new JSONArray(json);
				ArrayList<LinkListItem> linkLists = new ArrayList<LinkListItem>();
				
				for (int i = 0; i < array.length(); i++) {
					linkLists.add(new LinkListItem(array.getJSONObject(i)));
				}	
				return linkLists;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
		
	}
	
	
}
