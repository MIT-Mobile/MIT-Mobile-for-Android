package edu.mit.mitmobile2.news.beans;

import java.util.ArrayList;

import android.text.Html;

import edu.mit.mitmobile2.news.beans.NewsCategory;

public class NewsStory {

	private String id;
	private String url;
	private String source_url;
	private String title;
	private String author;
	private String published_at;
	private Boolean featured;
	private NewsCategory category;
	private String type;
	private String dek;
	private String body_html;
	private NewsImage cover_image;
	private ArrayList<NewsImage> gallery_images;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSourceUrl() {
		return source_url;
	}

	public void setSourceUrl(String source_url) {
		this.source_url = source_url;
	}

	public String getTitle() {
		return title;
	}
	public String getTitleText(){
		if(title!=null)
			return Html.fromHtml(title).toString();
		else
			return null;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublishedAt() {
		return published_at;
	}

	public void setPublishedAt(String published_at) {
		this.published_at = published_at;
	}

	public Boolean getFeatured() {
		return featured;
	}

	public void setFeatured(Boolean featured) {
		this.featured = featured;
	}

	public NewsCategory getCategory() {
		return category;
	}

	public void setCategory(NewsCategory category) {
		this.category = category;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDek() {
		return dek;
	}
	public String getDekText(){
		if(dek!=null)
			return Html.fromHtml(dek).toString();
		else
			return null;
	}
	
	public void setDek(String dek) {
		this.dek = dek;
	}

	public String getBodyHtml() {
		return body_html;
	}

	public void setBodyHtml(String body_html) {
		this.body_html = body_html;
	}

	public NewsImage getCoverImage() {
		return cover_image;
	}

	public void setCoverImage(NewsImage cover_image) {
		this.cover_image = cover_image;
	}

	public ArrayList<NewsImage> getGalleryImages() {
		return gallery_images;
	}

	public void setGalleryImages(ArrayList<NewsImage> gallery_images) {
		this.gallery_images = gallery_images;
	}
	
	
}
