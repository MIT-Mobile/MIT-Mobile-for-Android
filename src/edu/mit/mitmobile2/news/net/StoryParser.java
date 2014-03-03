package edu.mit.mitmobile2.news.net;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.mitmobile2.news.beans.NewsImage;
import edu.mit.mitmobile2.news.beans.NewsStory;
import edu.mit.mitmobile2.news.beans.NewsCategory;

public class StoryParser extends NewsParser<NewsStory>{
	//parsing functions
	@Override
	public NewsStory parseObject(JSONObject obj){
		NewsStory story = null;
		try{
			story = new NewsStory();
			story.setId(obj.getString("id"));
			story.setUrl(obj.getString("url"));
			story.setSourceUrl(obj.getString("source_url"));
			if(obj.has("title"))
				story.setTitle(obj.getString("title"));
			if(obj.has("author"))
				story.setAuthor(obj.getString("author"));
			story.setPublishedAt(obj.getString("published_at"));
			story.setFeatured(obj.getBoolean("featured"));
			if(obj.has("type"))
				story.setType(obj.getString("type"));
			if(obj.has("dek"))
				story.setDek(obj.getString("dek"));
			if(obj.has("body_html"))
				story.setBodyHtml(obj.getString("body_html"));
			JSONObject c = null;
			if(obj.has("category")){
				NewsCategory cat = null;
				c = obj.getJSONObject("category");
				if(c!=null){
					CategoryParser catDS = new CategoryParser();
					cat = catDS.parseObject(c);
				}
				story.setCategory(cat);
			}
			ImageParser ids = new ImageParser();
			if(obj.has("cover_image")){
				NewsImage cimg = null;
				c = obj.getJSONObject("cover_image");
				if(c!=null){
					cimg = ids.parseObject(c);
				}
				story.setCoverImage(cimg);
			}
			if(obj.has("gallery_images")){
				JSONArray ar = obj.getJSONArray("gallery_images");
				ArrayList<NewsImage> aimg = null;
				if(ar!=null){
					aimg = ids.parseObjectArray(ar);
				}
				story.setGalleryImages(aimg);
			}
		}catch(JSONException e){
			e.printStackTrace();
			story = null;
		}
		return story;
	}
	

	
}
