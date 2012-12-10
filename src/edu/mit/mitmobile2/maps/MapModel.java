package edu.mit.mitmobile2.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.MobileWebApi.HttpClientType;
import edu.mit.mitmobile2.MobileWebApi.JSONObjectResponseListener;
import edu.mit.mitmobile2.MobileWebApi.ServerResponseException;
import edu.mit.mitmobile2.MobileWebApi.DefaultCancelRequestListener;
import edu.mit.mitmobile2.MobileWebApi.DefaultErrorListener;

public class MapModel {
    public static String MODULE_MAP = "map";
	public static final String TAG = "MapModel";

    public static void fetchMapServerData(final Context context, final Handler uiHandler) {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("command", "bootstrap");
        parameters.put("module", MODULE_MAP);

        MobileWebApi webApi = new MobileWebApi(false, true, "Map", context, uiHandler);
        webApi.requestJSONObject(parameters, new MobileWebApi.JSONObjectResponseListener(
                new MobileWebApi.DefaultErrorListener(uiHandler), new MobileWebApi.DefaultCancelRequestListener(
                        uiHandler)) {

            @Override
            public void onResponse(JSONObject jobject) throws JSONException {
            	Log.d(TAG,jobject.toString());
            	MapServerData mapServerData = MapParser.parseMapServerData(jobject);
            	MobileWebApi.sendSuccessMessage(uiHandler, mapServerData);
            }
        });
    }
    
}


