package edu.mit.mitmobile2.maps;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.news.NewsModel;
import edu.mit.mitmobile2.objs.NewsItem;
import edu.mit.mitmobile2.objs.PersonItem;
import edu.mit.mitmobile2.people.PeopleDB;

public class MapModel {
    public static String MODULE_MAP = "map";
	public static final String TAG = "MapModel";
	public static final String MAP_EXPORT_PATH = "http://ims-pub.mit.edu/ArcGIS/rest/services/base/WhereIs_Base/MapServer/export";
	public static final int FETCH_SUCCESSFUL = 1;
	public static final int FETCH_FAILED = 2;

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
  
    public static void exportMapBitmap(Context mContext,String boundingBox, final boolean silent, final Handler uiHandler) {
		HashMap<String, String> parameters = new HashMap<String, String>();


		//http://ims-pub.mit.edu/ArcGIS/rest/services/base/WhereIs_Base/MapServer/export?bbox=-7915408.507713147%2C5214104.055227387%2C-7915208.507713147%2C5214304.055227387&bboxSR=&layers=&layerdefs=&size=&imageSR=&format=png&transparent=false&dpi=&time=&layerTimeOptions=&f=image
		parameters.put("format", "png");		
		parameters.put("transparent","false");
		parameters.put("f","image");

		if(boundingBox != null) {
			parameters.put("bbox", boundingBox);
		}
		
		MobileWebApi webApi = new MobileWebApi(false, !silent, "Maps", mContext, uiHandler);
		webApi.requestRaw(MAP_EXPORT_PATH, parameters, new MobileWebApi.RawResponseListener(null, null) {			
			@Override
			public void onError() {
				Log.d(TAG,"error");
				Message message = Message.obtain();
				message.arg1 = FETCH_FAILED;
				uiHandler.sendMessage(message);
			}
			
			@Override
			public void onResponse(final InputStream stream) {
				Log.d(TAG,"response receieved");
				Log.d(TAG,stream.toString());
				Message message = Message.obtain();
				message.arg1 = FETCH_SUCCESSFUL;
				Bitmap bmp = BitmapFactory.decodeStream(stream);
				message.obj = bmp;
				uiHandler.sendMessage(message);
			}
		});	
		

    }

}


