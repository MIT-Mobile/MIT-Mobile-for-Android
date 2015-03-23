package edu.mit.mitmobile2.mit150;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.MobileWebApi.JSONArrayResponseListener;
import edu.mit.mitmobile2.MobileWebApi.DefaultErrorListener;
import edu.mit.mitmobile2.MobileWebApi.ServerResponseException;
import edu.mit.mitmobile2.mit150.CorridorStory.Image;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class CorridorModel {
	
	// private mutable stories
	private static ArrayList<CorridorStory> sStories = new ArrayList<CorridorStory>();
	
	// public unmutable stories
	public static List<CorridorStory> sCorridorStories = sStories;
	
	public static void fetchInitialStories(Context context, Handler uiHandler) {
		if(sStories.size() > 0) {
			MobileWebApi.sendSuccessMessage(uiHandler);
			return;
		}
		
		fetchMoreStories(context, uiHandler);
	}
	
	public static void fetchMoreStories(Context context, final Handler uiHandler) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("module", "corridor");
		params.put("command", "list");
		params.put("offset", Integer.toString(sStories.size()));
		
		MobileWebApi api = new MobileWebApi(false, true, "corridor", context, uiHandler);
		api.requestJSONArray(params, new JSONArrayResponseListener(new DefaultErrorListener(uiHandler), null) {
			@Override
			public void onResponse(JSONArray array) throws ServerResponseException, JSONException {
				for(int i = 0; i < array.length(); i++) {
					JSONObject storyJson = array.getJSONObject(i);
					
					Image storyImage = null;
					if(storyJson.has("image")) {
						JSONObject imageJson = storyJson.getJSONObject("image");
						storyImage = new Image(
								imageJson.getString("src"),
								Integer.parseInt(imageJson.getString("width")),
								Integer.parseInt(imageJson.getString("height"))
						);
					}
					
					sStories.add(
						new CorridorStory(
							storyJson.getString("title"),
							storyJson.getString("firstname"),
							storyJson.getString("lastname"),
							storyJson.getString("affiliation"),
							new Date(storyJson.getLong("date-posted-unix") * 1000),
							storyImage,
							storyJson.getString("body"),
							storyJson.getString("plain-text")
						)
					);
				}
				
				Message msg = Message.obtain();
				msg.arg1 = MobileWebApi.SUCCESS;
				msg.arg2 = array.length();
				uiHandler.sendMessage(msg);
			}
		});
	}

}
