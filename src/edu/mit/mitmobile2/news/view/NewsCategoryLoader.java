package edu.mit.mitmobile2.news.view;

import java.util.ArrayList;

import android.content.Context;

import edu.mit.mitmobile2.news.beans.NewsStory;
import edu.mit.mitmobile2.news.net.NewsDownloader;
import edu.mit.mitmobile2.news.net.StoriesProgressListener;

public abstract class NewsCategoryLoader {

	NewsDownloader nd;
	LoadingScreenListener mLoadingScreenListener;
	ArrayList<NewsStory> list;
	boolean isLoading = false;
	int position;
    String start_story_id;
    boolean refreshData;
    //String criteria;
    //String type;
	
    //abstract void categoryLoaded();
    
	public NewsCategoryLoader(Context c){
		nd = NewsDownloader.getInstance(c);
		list = new ArrayList<NewsStory>();
		this.refreshData = false;
	}
	
	/*public void setScreenListener(LoadingScreenListener l){
		this.mLoadingScreenListener = l;
	}
	
	public LoadingScreenListener getScreenListener(){
		return this.mLoadingScreenListener;
	}*/
	
	public void loadStories(String criteria, String type, int start, int limit){
    	String[] cats = new String[]{criteria};
		
    	final NewsCategoryLoader ncl = this;
		
		NewsDownloader.DownloadStoriesTask dst = nd.new DownloadStoriesTask(new StoriesProgressListener(){
			
			//ArrayList<NewsStory> allStories = new ArrayList<NewsStory>();
			{
				ncl.isLoading = true;
				ncl.list.clear();
			}
			@Override
			public void onProgressUpdate(ArrayList<NewsStory>... list) {
				for(ArrayList<NewsStory> st:list){
					if(st!=null && st.size()>0){
						for(int i=0;(i < st.size()); i++){
							ncl.list.add(st.get(i));
						}
					}
				}
				
			}

			@Override
			public void onPostExecute(Long nr) {
				ncl.isLoading = false;
				if(ncl.start_story_id!=null){
					ncl.seekToNewsItem(ncl.start_story_id);
				}
				//categoryLoaded();
				if(mLoadingScreenListener!=null)
					mLoadingScreenListener.onStoriesLoaded();
			}
			
		}, type, start, limit);
		dst.setRefresh(refreshData);
		dst.execute(cats);
    }
	
	public void setLoadingScreenListener(LoadingScreenListener listener) {
    	mLoadingScreenListener = listener;
    }
	
	public void seekToNewsItem(String story_id) {
		for(int i = 0; i< this.list.size();i++){
			if(this.list.get(i).getId().equals(story_id)){
				this.position = i;
				break;
			}
		}
	}
	
	void setStartStory(String storyId) {
		this.start_story_id = storyId;
	}

	String getStartStory() {
		return this.start_story_id;
	}
}
