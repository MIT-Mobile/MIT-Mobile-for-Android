package edu.mit.mitmobile2.news.net;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.news.beans.NewsImage;
import edu.mit.mitmobile2.news.beans.NewsImageRepresentation;

public class ImageParser  extends NewsParser<NewsImage>{
	
	//parsing functions
	@Override
	public NewsImage parseObject(JSONObject obj){
		NewsImage im = null;
		try{
			im = new NewsImage();
			if(obj.has("description"))
				im.setDescription(obj.getString("description"));
			if(obj.has("credits"))
				im.setCredits(obj.getString("credits"));
			ArrayList<NewsImageRepresentation> reps = null;
			JSONArray ar = obj.getJSONArray("representations");
			if(ar!=null){
				ImageRepresentationParser irDS = new ImageRepresentationParser();
				reps = irDS.parseObjectArray(ar);
			}
			im.setRepresentations(reps);
		}catch(JSONException e){
			e.printStackTrace();
			im = null;
		}
		return im;
	}

}
