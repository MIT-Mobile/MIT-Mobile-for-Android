package edu.mit.mitmobile2.news.view;

import edu.mit.mitmobile2.SliderView;
import edu.mit.mitmobile2.news.beans.NewsStory;

public interface NewsSliderAdapter extends SliderView.Adapter{
	public void seekToNewsItem(int position);
	public void seekToNewsItem(String story_id);
	
    public NewsStory getCurrentNewsItem();
    
    public int getStoriesCount();
    public boolean isLoading();
}
