package edu.mit.mitmobile2.qrreader;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.MobileWebApi.JSONObjectResponseListener;
import edu.mit.mitmobile2.MobileWebApi.ServerResponseException;

public class QRReaderModel {
	private static final String MODULE_NAME = "qr";
	
	public void fetchSuggestedUrl(Context context, String originString, final Handler uiHandler, boolean isUrl) {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("module", MODULE_NAME);
		parameters.put("q", originString);
		
		final MobileWebApi api = new MobileWebApi(false, true, "QR Code", context, uiHandler);
		api.requestJSONObject(parameters, new JSONObjectResponseListener(new MobileWebApi.DefaultErrorListener(uiHandler), 
				new MobileWebApi.DefaultCancelRequestListener(uiHandler)) {
			
			@Override
			public void onResponse(JSONObject object) throws ServerResponseException,
					JSONException {

				SuggestedUrl suggest = new SuggestedUrl();
				if (object.has("success") && !object.isNull("success")) {
					boolean success = object.getBoolean("success");
					suggest.isSuccess = success;
					// success so parse rest of JSON
					suggest.type 		= object.getString("type");
					suggest.displayType	= object.getString("displayType");
					suggest.displayName	= object.getString("displayName");
					
					// init suggest.shareAction
					JSONObject shareObj = object.optJSONObject("share");
					if (shareObj != null) {
						suggest.shareAction = new QRAction();
						suggest.shareAction.title	= shareObj.getString("title");
						suggest.shareAction.url		= shareObj.getString("data");
					}
					
					// init suggest.actions array
					JSONArray actionsArr = object.optJSONArray("actions");
					if (actionsArr != null) {
						int actionsArrLength = actionsArr.length();
						suggest.actions = new QRAction[actionsArrLength];
						for (int i = 0; i < actionsArrLength; i++) {
							
							JSONObject jsonAction = actionsArr.getJSONObject(i);
							suggest.actions[i].title 	= jsonAction.getString("title");
							suggest.actions[i].url 		= jsonAction.getString("url");
						}
					}
					
				}
				MobileWebApi.sendSuccessMessage(uiHandler, suggest);
			}
		});
	}
	
	public static class SuggestedUrl {
		public boolean isSuccess;
		public String type;
		public String displayType;
		public String displayName;
		public QRAction[] actions;
		public QRAction	shareAction;
		
	}
	
	public static class QRAction {
		public String title;
		public String url;
	}
	
}
