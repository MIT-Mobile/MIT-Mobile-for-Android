package edu.mit.mitmobile2.news.net;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public abstract class NewsParser<T> {
	public abstract T parseObject(JSONObject jsonObj);
	
	public NewsParser(){
		
	}
	public ArrayList<T> parseArrayFromString(String t){
		try{
			JSONArray array = new JSONArray(t);
			return parseObjectArray(array);
		}catch(Exception e){
			Log.d("NEWS_PARSER",""+e.getLocalizedMessage());
			return null;
		}
	}
	public T parseObjectFromString(String t){
		try{
			JSONObject obj = new JSONObject(t);
			return parseObject(obj);
		}catch(Exception e){
			Log.d("NEWS_PARSER",""+e.getLocalizedMessage());
			return null;
		}
	}
	//parsing function
	public ArrayList<T> parseObjectArray(JSONArray array) {
		ArrayList<T> reps = new ArrayList<T>();
		for (int i = 0; i < array.length(); i++) {
			try {
				JSONObject jsonObj = array.getJSONObject(i);
				reps.add(parseObject(jsonObj));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return reps;
	}
}