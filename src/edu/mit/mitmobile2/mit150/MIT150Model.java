package edu.mit.mitmobile2.mit150;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Log;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.MobileWebApi.JSONObjectResponseListener;
import edu.mit.mitmobile2.MobileWebApi.ServerResponseException;

/*
 * Here be more dragons than the rest of the code
 * This code has a somewhat confused notion of caching
 * the database is mainly just used to cache images
 * and the rest of the data is cached in a json snipper
 */

public class MIT150Model {
	
	private static ArrayList<MIT150FeatureItem> features = null;
	public static ArrayList<MIT150MoreFeaturesItem> more_features = null;
	private boolean mainImagesFetched = false;
	
	private long lastModified;
	private String json;
	
	private MIT150DB mit150db;
	
	//HashMap<String, Bitmap> mThumbnails = new HashMap<String, Bitmap>();

	static private String PREF_150_LAST_SAVED = "pref_150_last_saved";
	static private String PREF_150_JSON = "pref_150_json";
	
	//private SharedPreferences.Editor editor;

	/********************************************************************/
	MIT150Model(Context ctx) {
		mit150db = MIT150DB.getInstance(ctx);
	}
	/********************************************************************/
	public ArrayList<MIT150FeatureItem> getFeatures(Context ctx) {
		
		return features;
	}
	/********************************************************************/
	public void fetchMIT150(final Context context, final Handler uiHandler) {	
		if(features != null && more_features != null && mainImagesFetched) {
			MobileWebApi.sendSuccessMessage(uiHandler);
			return;
		}
		
		final SharedPreferences pref = context.getSharedPreferences(Global.PREFS,Context.MODE_WORLD_READABLE|Context.MODE_WORLD_READABLE);
		final long cachedLastSaved = pref.getLong(PREF_150_LAST_SAVED, -1); 	
		// keep for 6 hours
		if(System.currentTimeMillis() - cachedLastSaved < 6L * 60L * 60L * 1000L) { // cache less than 6 hours  
			json = pref.getString(PREF_150_JSON, null); 
			// don't download again but flag success
			if (json!=null) {
				try {
					handleJSON(new JSONObject(json));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			features = mit150db.getCachedFeatures();
			mainImagesFetched = true;
			MobileWebApi.sendSuccessMessage(uiHandler);
			return;
		} 
		
		MobileWebApi webApi = new MobileWebApi(false, true, "MIT150", context, uiHandler);
		
		HashMap<String, String> query = new HashMap<String, String>();
		query.put("module", "features");
		query.put("command", "list");
		
		webApi.requestJSONObject(query, new JSONObjectResponseListener(new MobileWebApi.DefaultErrorListener(uiHandler), null) {

			@Override
			public void onResponse(JSONObject object)
					throws ServerResponseException, JSONException {
				
				handleJSON(object);
				json = object.toString();

				new Thread() {

					public void run() {
						
						if(cachedLastSaved == -1 || cachedLastSaved > lastModified) {
							// do not bother downloading new images
							// since data has not been modified since last saved
							mainImagesFetched = fetchMainImages();
				
							if(!mainImagesFetched) {
								// early exit failed to receive main button images
								MobileWebApi.sendErrorMessage(uiHandler);
								return;
							}
							
							mit150db.updateFeatures(features);
							SharedPreferences.Editor editor = pref.edit();
							editor.putLong(PREF_150_LAST_SAVED, System.currentTimeMillis());
							editor.putString(PREF_150_JSON, json);
							editor.commit();
						} else {
							features = mit150db.getCachedFeatures();
						}
						
						MobileWebApi.sendSuccessMessage(uiHandler);
					}
				}.start();
			}
		});
	}
	
	private void handleJSON(JSONObject object) throws JSONException {

		lastModified  = object.getLong("last-modified");

		features = new ArrayList<MIT150FeatureItem>();
		more_features = new ArrayList<MIT150MoreFeaturesItem>();
		
		JSONArray fs = object.getJSONArray("features");
		for(int i = 0; i < fs.length(); i++) {
			JSONObject jo = fs.getJSONObject(i);
			MIT150FeatureItem f = new MIT150FeatureItem();
			f.id    = jo.getString("id");
			f.title = jo.getString("title");
			f.subtitle = jo.optString("subtitle");
			f.url   = jo.getString("url");
			f.photo_url = jo.getString("photo-url");
			
			f.setTintColor(jo.getString("tint-color"));
			if(jo.has("title-color")) {
				f.setTitleColor(jo.getString("title-color"));
			}
			if(jo.has("arrow-color")) {
				f.setArrowColor(jo.getString("arrow-color"));
			}
			
			
			JSONObject dim = jo.getJSONObject("dimensions");
			f.dim = f.new Dimension();
			f.dim.height = dim.getInt("height");
			f.dim.width = dim.getInt("width");
			
			features.add(f);
		}
		
		fs = object.getJSONArray("more-features");
		for(int i = 0; i < fs.length(); i++) {
			JSONObject jo = fs.getJSONObject(i);
			
			MIT150MoreFeaturesItem m = new MIT150MoreFeaturesItem();
			m.section_title    = jo.getString("section-title");

			JSONArray fs2    = jo.getJSONArray("items");
			m.items = new ArrayList<MIT150MoreItem>();
			for (int j=0; j<fs2.length(); j++) {
				MIT150MoreItem mi = new MIT150MoreItem();
				JSONObject joo = fs2.getJSONObject(j);
				mi.thumbnail152_url = joo.getString("thumbnail152-url");
				mi.title = joo.getString("title");
				mi.subtitle = joo.getString("subtitle");
				mi.url = joo.getString("url");
				m.items.add(mi);
			}
			more_features.add(m);
		}
	}

	/********************************************************************/
	public boolean fetchMainImages() {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		for (MIT150FeatureItem f : features) {
			f.bm = getImage(f.photo_url,opts);
			
			if(f.bm == null) {
				return false;
			}
		}		
		return true;
	}

	/********************************************************************/
	public void fetchThumbnails(final Handler uiHandler, final ArrayList<MIT150MoreItem> items) {
		
		Thread t = new Thread() {
			@Override
			public void run() {
				Bitmap bm;
				BitmapFactory.Options opts = new BitmapFactory.Options();
				for (MIT150MoreItem m : items) {
					if (m.thumbnail152_url==null) continue;
					if (m.bd!=null) continue;
					bm = getImage(m.thumbnail152_url,opts);
					//mThumbnails.put(m.thumbnail152_url, bm);
					if(bm != null) {
						m.bd = new BitmapDrawable(bm);
						mit150db.saveMoreThumbnail(m);
					}
				}
				// TODO cache?
				uiHandler.sendEmptyMessage(0);
			}
		};
		t.start();
		
	}
	
	/********************************************************************/
	public Bitmap getImage(String url, BitmapFactory.Options decodeOptions) {
	
    	DefaultHttpClient httpClient = new DefaultHttpClient();
    	HttpGet request = new HttpGet(url);
    	HttpResponse response;
		
    	Log.d("MIT150", "requesting image " + url);
		try {
			response = httpClient.execute(request);

			if(response.getStatusLine().getStatusCode() == 200) {
				byte[] imageData = EntityUtils.toByteArray(response.getEntity());
	     		
	     		return BitmapFactory.decodeByteArray(imageData, 0, imageData.length, decodeOptions);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
