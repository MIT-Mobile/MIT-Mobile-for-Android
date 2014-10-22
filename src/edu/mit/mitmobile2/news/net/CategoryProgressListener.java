package edu.mit.mitmobile2.news.net;

import java.util.ArrayList;

import edu.mit.mitmobile2.news.beans.NewsCategory;

public interface CategoryProgressListener {
	void onPostExecute(ArrayList<NewsCategory> list);
}
