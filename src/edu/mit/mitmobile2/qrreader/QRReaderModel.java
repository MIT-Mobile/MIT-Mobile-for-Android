package edu.mit.mitmobile2.qrreader;

import java.util.HashMap;

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
		
		if (isUrl) {
			parameters.put("url", originString);
		} else {
			parameters.put("barcode", originString);
		}
		
		final MobileWebApi api = new MobileWebApi(false, true, "QR Code", context, uiHandler);
		api.requestJSONObject(parameters, new JSONObjectResponseListener(new MobileWebApi.DefaultErrorListener(uiHandler), 
				new MobileWebApi.DefaultCancelRequestListener(uiHandler)) {
			
			@Override
			public void onResponse(JSONObject object) throws ServerResponseException,
					JSONException {
				// TODO Auto-generated method stub
				SuggestedUrl suggest = new SuggestedUrl();
				if (object.has("success") && !object.isNull("success")) {
					boolean success = object.getBoolean("success");
					suggest.isSuccess = success;
					if (success && object.has("url") && !object.isNull("url")) {
						suggest.suggestedUrl = object.getString("url");
					}
				}
				MobileWebApi.sendSuccessMessage(uiHandler, suggest);
			}
		});
	}
	
	public static class SuggestedUrl {
		public boolean isSuccess;
		public String suggestedUrl;
	}
	
}
