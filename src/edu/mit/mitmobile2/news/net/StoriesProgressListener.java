package edu.mit.mitmobile2.news.net;

import java.util.ArrayList;

import edu.mit.mitmobile2.news.beans.NewsStory;

public interface StoriesProgressListener {
	
	void onProgressUpdate(ArrayList<NewsStory>... list);
	void onPostExecute(Long nr);
	
	
	//void onPostExecuteStory(ArrayList<NewsStory> list);
}
