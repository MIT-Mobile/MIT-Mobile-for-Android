package edu.mit.mitmobile2.news.beans;

import java.util.ArrayList;

import android.util.Log;

import edu.mit.mitmobile2.news.beans.NewsImageRepresentation;

public class NewsImage{
	private String description;
	private String credits;
	private ArrayList<NewsImageRepresentation> representations;
	private int min_index = -1;
	
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
	public NewsImageRepresentation getSmallestRepresentationsByDiagonal() {
		if(this.min_index == -1){
			int mIndex = 0;
			double min_size = 10000;
			double tmp_size = 0;
			int i = 0;
			for(NewsImageRepresentation ir : representations){
				tmp_size = Math.sqrt(ir.getHeight()^2 + ir.getWidth()^2); 
				if(tmp_size < min_size){
					min_size = tmp_size;
					mIndex = i; 
				}
				i++;
			}
			this.min_index = mIndex;
		}
		return representations.get(this.min_index);
	}
	public NewsImageRepresentation getRepresentationBestFitByWidthOrHeight(int width, int height) {
		int mIndex = -1;
		double max_size = 0;
		int i = 0;
		for(NewsImageRepresentation ir : representations){
			if(ir.getWidth() > max_size && ((ir.getWidth() < width) && (ir.getHeight() < height))){
				max_size = ir.getWidth();
				mIndex = i; 
			}
			i++;
		}
		if(mIndex == -1){
			return getSmallestRepresentationsByDiagonal();
		}else{
			return representations.get(mIndex);
		}
	}
	public NewsImageRepresentation getRepresentationBestFitByWidth(int width) {
		int mIndex = 0;
		double max_size = 0;
		int i = 0;
		Log.d("NewsImageRepresentation", "width = " + width);
		for(NewsImageRepresentation ir : representations) {
			int imageWidth = ir.getWidth();
			Log.d("NewsImageRepresentation", "imageWidth = " + imageWidth);
			if(ir.getWidth() > max_size && ir.getWidth() < width) {
				max_size = ir.getWidth();
				mIndex = i;
			}
			i++;
		}
		Log.d("NewsImageRepresentation", "chosenWidth = " + representations.get(mIndex).getWidth());
		return representations.get(mIndex);
	}
	// TODO: make sure this isn't just always getting the largest image possible.
	// TODO: make this not use overly large images for the given width. The  
	//       Around Campus story "MathWorks expands its support for digital  
	//       learning at MIT" has images that are 228, 468, and 3760 pixels wide.
	//       If the desired width is 800, 3760 is 4x the width needed, whereas 
	//       the 468 image is slightly more than half. It would make sense to 
	//       say that if the closest image with a minimum width of x is more 
	//       than double the requested width, use the next smallest image than that.
	//       So, requesting 800 and getting 1500 would be ok, but not 1700.
	public NewsImageRepresentation getSmallestRepresentationWithMinimumWidth(int minWidth) {
		int mIndex = 0;
		double bestSize = -1.0;
		int i = 0;
		Log.d("getSmallestRepresentationWithMinimumWidth", "minWidth = " + minWidth);
		for(NewsImageRepresentation ir : representations) {
			int imageWidth = ir.getWidth();
			Log.d("getSmallestRepresentationWithMinimumWidth", "imageWidth = " + imageWidth);
			if ((bestSize < minWidth && imageWidth > bestSize) || (imageWidth >= minWidth && imageWidth < bestSize)) {
				bestSize = imageWidth;
				mIndex = i;
			}
			i++;
		}
		Log.d("getSmallestRepresentationWithMinimumWidth", "chosenWidth = " + representations.get(mIndex).getWidth());
		return representations.get(mIndex);
	}
	public void setRepresentations(ArrayList<NewsImageRepresentation> representations) {
		this.representations = representations;
	}
	
}