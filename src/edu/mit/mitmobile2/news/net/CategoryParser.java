package edu.mit.mitmobile2.news.net;

import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.news.beans.NewsCategory;

public class CategoryParser   extends NewsParser<NewsCategory>{
	//parsing functions
	@Override
	public NewsCategory parseObject(JSONObject obj){
		NewsCategory cat = null;
		try{
			cat = new NewsCategory();
			cat.setId(obj.getString("id"));
			cat.setUrl(obj.getString("url"));
			cat.setName(obj.getString("name"));
		}catch(JSONException e){
			e.printStackTrace();
			cat = null;
		}
		return cat;
	}
}
