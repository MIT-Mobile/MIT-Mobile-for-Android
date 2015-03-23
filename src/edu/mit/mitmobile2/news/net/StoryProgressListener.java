package edu.mit.mitmobile2.news.net;

import edu.mit.mitmobile2.news.beans.NewsStory;

public interface StoryProgressListener {
	void onProgressUpdate(NewsStory... list);
	void onPostExecute(Long nr);
}
