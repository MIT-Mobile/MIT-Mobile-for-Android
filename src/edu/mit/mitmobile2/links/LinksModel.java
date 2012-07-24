package edu.mit.mitmobile2.links;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.Handler;
import edu.mit.mitmobile2.MobileWebApi;

public class LinksModel {

	
	public static void fetchLinks(Context context, final Handler uiHandler) {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("module", "links");
		
		MobileWebApi webApi = new MobileWebApi(false, true, "People", context, uiHandler);
		webApi.setIsSearchQuery(false);
		webApi.requestJSONArray(parameters, new MobileWebApi.JSONArrayResponseListener(
				new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(uiHandler) ) {
			
			@Override
			public void onResponse(JSONArray array) throws JSONException {
				ArrayList<LinkItem> links = new ArrayList<LinkItem>();
				
				for (int i = 0; i < array.length(); i++) {
					links.add(new LinkItem(array.getJSONObject(i)));
				}
				
				MobileWebApi.sendSuccessMessage(uiHandler, links);
			}
		});
	}
	
	
	
}
