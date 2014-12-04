package edu.mit.mitmobile2.news.view;

import android.content.Context;
import android.view.View;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.SliderView.ScreenPosition;
import edu.mit.mitmobile2.news.beans.NewsStory;
import edu.mit.mitmobile2.news.view.NewsSliderAdapter;

public class NewsSliderCursorAdapter extends NewsCategoryLoader implements NewsSliderAdapter {

    Context mContext;
    StorySliderListener mStoryListener;
    
    NewsDetailsView mPreviousScreen;
    NewsDetailsView mCurrentScreen;
    NewsDetailsView mNextScreen;
    FullScreenLoader mLoadingScreen;
    
    boolean mHasDynamicLoading;
    
    //ArrayList<NewsStory> list;
    
    
    
    NewsSliderCursorAdapter(Context context, String criteria,String type,int start, int limit, StorySliderListener storyListener, boolean hasDynamicLoading) {
		super(context);
    	mContext = context;
		this.position = 0;
		mStoryListener = storyListener;
		mHasDynamicLoading = hasDynamicLoading;
		loadStories(criteria,type,start,limit);
    }

    @Override
    public boolean hasScreen(ScreenPosition screenPosition) {
		if (list.isEmpty()) {
		    return false;
		}
		
		switch (screenPosition) {
			case Previous:
			    return position!=0;
			    
			case Current:
			    return true;
			    
			case Next:
			    if (position < list.size() -1) {
			    	return true;
			    }
			    /*if (mCursor.isLast()) {
				return hasMore();
			    }*/
			    return false;
		}
		return false;
    }

    @Override
    public View getScreen(ScreenPosition screenPosition) {
		if (isLoadingScreen(screenPosition)) {
		    if (mLoadingScreen == null) {
		    	mLoadingScreen = new FullScreenLoader(mContext, null);
		    }
		    return mLoadingScreen;
		    
		}
		if (screenPosition == ScreenPosition.Previous) {
		    if (mPreviousScreen == null) {
		    	NewsStory newsItem = null;
		    	if(this.position>0)
		    		newsItem = list.get(this.position-1);
		    	mPreviousScreen = new NewsDetailsView(mContext, newsItem);
		    }
		    return mPreviousScreen;
		} else if (screenPosition == ScreenPosition.Current) {
		    if (mCurrentScreen == null) {
		    	NewsStory newsItem = list.get(this.position);
		    	mCurrentScreen = new NewsDetailsView(mContext, newsItem);
		    }
		    return mCurrentScreen;
		} else if (screenPosition == ScreenPosition.Next) {
		    if (mNextScreen == null) {
		    	NewsStory newsItem = null;
		    	if(this.position < this.list.size()-1)
		    		newsItem = this.list.get(this.position+1);
		    	mNextScreen = new NewsDetailsView(mContext, newsItem);
		    }
		    return mNextScreen;
		}
		return null;
    }

    @Override
    public void destroyScreen(ScreenPosition screenPosition) {
    	// TODO Auto-generated method stub
	
    }

    @Override
    public void seek(ScreenPosition screenPosition) {
		if (isLoadingScreen(screenPosition)) {
		    if (screenPosition == ScreenPosition.Next) {
		    	if(this.position < this.list.size() -1 )
		    		this.position ++;
		    	mPreviousScreen = mCurrentScreen;
		    	mCurrentScreen = null;
		    	mNextScreen = null;
		    }
		    mLoadingScreen.showLoading();
		    
		}
		
		if (screenPosition == ScreenPosition.Previous) {
		    //mCursor.move(-1);
		    if(this.position > 0)
		    	this.position --;
		    mNextScreen = mCurrentScreen;
		    mCurrentScreen = mPreviousScreen;
		    mPreviousScreen = null;
		    
		} else if (screenPosition == ScreenPosition.Next) {
			if(this.position < this.list.size() -1 )
				this.position ++;
		    
		    mPreviousScreen = mCurrentScreen;
		    mCurrentScreen = mNextScreen;
		    mNextScreen = null;
		    
		}
		
		mStoryListener.onStorySelected(getCurrentNewsItem());
    }
    
    public void showError() {
    	mLoadingScreen.showError();
    }

    public void stopLoading() {
    	mLoadingScreen.stopLoading();
    }
    
    @Override
    public void destroy() {
    	// TODO Auto-generated method stub
	
    }

    /*private boolean hasMore() {
    	//return mHasDynamicLoading && (NewsModel.MAX_STORIES_PER_CAREGORY > mCursor.getCount());
    	return false;
    }*/
    
    @Override
    public boolean isLoading(){
    	return this.isLoading;
    }
    
    private boolean isLoadingScreen(ScreenPosition screenPosition) {
    	return this.isLoading;
    }


    public NewsStory getLastStoryItem() {
    	NewsStory item = null;
    	if(this.list.size()>0)
    		item = this.list.get(this.list.size()-1);
		
    	return item;
    }
    
    @Override
    public void seekToNewsItem(int position) {
    	if(position >=0 && position < this.list.size()){
    		this.position = position;
    	}
    	//mCursor.moveToPosition(position);
    }


    @Override
    public NewsStory getCurrentNewsItem() {
    	//return NewsDB.retrieveNewsItem(mCursor);
    	if(this.list.size()>this.position)
    		return this.list.get(this.position);
    	else
    		return null;
    }

    @Override
    public int getStoriesCount() {
    	return this.list.size();
    }
}
