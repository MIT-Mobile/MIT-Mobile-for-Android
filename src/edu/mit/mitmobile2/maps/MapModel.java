package edu.mit.mitmobile2.maps;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.news.NewsDB;
import edu.mit.mitmobile2.news.NewsModel;
import edu.mit.mitmobile2.objs.MapItem;
import edu.mit.mitmobile2.objs.NewsItem;
import edu.mit.mitmobile2.objs.PersonItem;
import edu.mit.mitmobile2.people.PeopleDB;

public class MapModel {
    public static String MODULE_MAP = "map";
	public static final String TAG = "MapModel";
	public static final String MAP_EXPORT_PATH = "http://ims-pub.mit.edu/ArcGIS/rest/services/base/WhereIs_Base/MapServer/export";
	public static final int FETCH_SUCCESSFUL = 1;
	public static final int FETCH_FAILED = 2;
	private Context mContext;
	private MapsDB mMapsDB;
	
	public MapModel(Context context) {
		mMapsDB = MapsDB.getInstance(context);
		mContext = context;
		//mSharedPreferences = mContext.getSharedPreferences(NEWS_PREFERENCES_FILE, Context.MODE_PRIVATE);
	}

	
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
            	//Log.d(TAG,jobject.toString());
            	MapServerData mapServerData = MapParser.parseMapServerData(jobject);
            	MobileWebApi.sendSuccessMessage(uiHandler, mapServerData);
            }
        });
    }
    
    public Cursor getMapsCursor() {
		return mMapsDB.getMapsCursor();
	}
  
	public void getBookmarks(final Handler uiHandler) {
		new Thread() {
			@Override
			public void run() {
				ArrayList mapItems = mMapsDB.getMapItems();
				Message msg = new Message();
				msg.obj = mapItems;
				uiHandler.sendMessage(msg);
			}
		}.start();
		
	}

    public void clearAllBookmarks(final Handler uiHandler) {
		new Thread() {
			@Override
			public void run() {
				mMapsDB.clearAllBookmarks();
				uiHandler.sendEmptyMessage(0);
			}
		}.start();
		
		Toast.makeText(mContext, "clearing bookmarks", Toast.LENGTH_LONG).show();
	}

}


