package edu.mit.mitmobile2.news.beans;

import java.util.ArrayList;

import edu.mit.mitmobile2.news.beans.NewsImageRepresentation;

public class NewsImage{
	private String description;
	private String credits;
	private ArrayList<NewsImageRepresentation> representations;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCredits() {
		return credits;
	}
	public void setCredits(String credits) {
		this.credits = credits;
	}
	public ArrayList<NewsImageRepresentation> getRepresentations() {
		return representations;
	}
	public void setRepresentations(ArrayList<NewsImageRepresentation> representations) {
		this.representations = representations;
	}
	
}