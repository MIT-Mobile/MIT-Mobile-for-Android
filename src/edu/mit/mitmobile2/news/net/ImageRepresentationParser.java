package edu.mit.mitmobile2.news.net;

import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.news.beans.NewsImageRepresentation;

public class ImageRepresentationParser extends NewsParser<NewsImageRepresentation>{
	
	public ImageRepresentationParser(){
	
	}

	//parsing functions
	@Override
	public NewsImageRepresentation parseObject(JSONObject obj){
		NewsImageRepresentation ir = null;
		try{
			ir = new NewsImageRepresentation();
			ir.setWidth(obj.getInt("width"));
			ir.setHeight(obj.getInt("height"));
			ir.setUrl(obj.getString("url"));
		}catch(JSONException e){
			e.printStackTrace();
			ir = null;
		}
		return ir;
	}

}
