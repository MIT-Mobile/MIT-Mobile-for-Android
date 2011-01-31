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
import android.os.Message;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.MobileWebApi.JSONObjectResponseListener;
import edu.mit.mitmobile2.MobileWebApi.ServerResponseException;

public class MIT150Model {
	
	private ArrayList<MIT150FeatureItem> features;
	public ArrayList<MIT150MoreFeaturesItem> more_features;
	
	private long lastModified;
	private long cachedLastModified;
	private String json;
	
	private MIT150DB mit150db;
	
	//HashMap<String, Bitmap> mThumbnails = new HashMap<String, Bitmap>();

	static private String PREF_150_LAST_MOD = "pref_150_last_mod";
	static private String PREF_150_JSON = "pref_150_json";
	
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;

	/********************************************************************/
	MIT150Model(Context ctx) {
		mit150db = MIT150DB.getInstance(ctx);
	}
	/********************************************************************/
	public ArrayList<MIT150FeatureItem> getFeatures(Context ctx) {
		
		// get cached if none
		//if (features==null) {
		//	features = mit150db.getCachedFeatures();
		//}
		
		return features;
	}
	/********************************************************************/
	public void fetchMIT150(final Context context, final Handler uiHandler) {	
		
		pref = context.getSharedPreferences(Global.PREFS,Context.MODE_WORLD_READABLE|Context.MODE_WORLD_READABLE);  

		cachedLastModified = pref.getLong(PREF_150_LAST_MOD, -1); 	
		json = pref.getString(PREF_150_JSON, null); 		
		
		
		final Handler midHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.arg1 == MobileWebApi.SUCCESS) {
					if (lastModified>cachedLastModified) {
						fetchMainImages(uiHandler);
					} else {
						// don't download again but flag success
						if (json!=null) {
							try {
								handleJSON(new JSONObject(json));
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						features = mit150db.getCachedFeatures();
						mit150db.getCachedMoreThumbnails(more_features);
						MobileWebApi.sendSuccessMessage(uiHandler);
					}
				} 
			}
		};
		
		
		MobileWebApi webApi = new MobileWebApi(false, true, "MIT150", context, midHandler);
		
		HashMap<String, String> query = new HashMap<String, String>();
		query.put("module", "features");
		query.put("command", "list");
		
		features = null;
		
		webApi.requestJSONObject(query, new JSONObjectResponseListener(new MobileWebApi.DefaultErrorListener(uiHandler), null) {

			@Override
			public void onResponse(JSONObject object)
					throws ServerResponseException, JSONException {
				
				handleJSON(object);

				json = object.toString();
				
				MobileWebApi.sendSuccessMessage(midHandler);
				
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
	public void fetchMainImages(final Handler uiHandler) {
		
		// TODO send failure msg
		
		Thread t = new Thread() {
			@Override
			public void run() {
				
				boolean saw_error = false;
				BitmapFactory.Options opts = new BitmapFactory.Options();
				for (MIT150FeatureItem f : features) {
					if (f.photo_url==null) {
						saw_error = true;
						continue;
					}
					f.bm = getImage(f.photo_url,opts);
				}
				
				if (!saw_error) {
					// Cache new data 
					mit150db.updateFeatures(features);
					editor = pref.edit();
					editor.putLong(PREF_150_LAST_MOD, lastModified);
					editor.putString(PREF_150_JSON, json);
					editor.commit();
				}
				
				MobileWebApi.sendSuccessMessage(uiHandler);
			}
		};
		t.start();
		
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
					m.bd = new BitmapDrawable(bm);
					mit150db.saveMoreThumbnail(m);
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
