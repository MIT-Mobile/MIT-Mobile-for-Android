package edu.mit.mitmobile2.news;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.SliderView.ScreenPosition;
import edu.mit.mitmobile2.news.NewsDetailsActivity.NewsSliderAdapter;
import edu.mit.mitmobile2.objs.NewsItem;

public class NewsSliderCursorAdapter implements NewsSliderAdapter {

    Cursor mCursor;
    Context mContext;
    StorySliderListener mStoryListener;
    
    NewsDetailsView mPreviousScreen;
    NewsDetailsView mCurrentScreen;
    NewsDetailsView mNextScreen;
    FullScreenLoader mLoadingScreen;
    
    boolean mHasDynamicLoading;
    OnLoadingScreenListener mLoadingScreenListener;
    
    public interface OnLoadingScreenListener {
	void onLoadingScreenSelected(NewsSliderCursorAdapter adapter, Cursor cursor);
    }
    
    NewsSliderCursorAdapter(Context context, Cursor cursor, StorySliderListener storyListener, boolean hasDynamicLoading) {
	mContext = context;
	mCursor = cursor;
	mCursor.moveToFirst();
	mStoryListener = storyListener;
	mHasDynamicLoading = hasDynamicLoading;
    }
    
    public void setOnLoadingScreenListener(OnLoadingScreenListener listener) {
	mLoadingScreenListener = listener;
    }
    
    @Override
    public boolean hasScreen(ScreenPosition screenPosition) {
	if (mCursor.getCount() == 0) {
	    return false;
	}
	
	switch (screenPosition) {
		case Previous:
		    return !mCursor.isFirst();
		    
		case Current:
		    return true;
		    
		case Next:
		    if (mCursor.getPosition() < mCursor.getCount()-1) {
			return true;
		    }
		    if (mCursor.isLast()) {
			return hasMore();
		    }
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
		mCursor.move(-1);	
		NewsItem newsItem = NewsDB.retrieveNewsItem(mCursor);
		mCursor.move(1);
		mPreviousScreen = new NewsDetailsView(mContext, newsItem);
	    }
	    return mPreviousScreen;
	    
	} else if (screenPosition == ScreenPosition.Current) {
	    
	    if (mCurrentScreen == null) {
		NewsItem newsItem = NewsDB.retrieveNewsItem(mCursor);
		mCurrentScreen = new NewsDetailsView(mContext, newsItem);
	    }
	    return mCurrentScreen;
	    
	} else if (screenPosition == ScreenPosition.Next) {

	    if (mNextScreen == null) {
		mCursor.move(1);
		NewsItem newsItem = NewsDB.retrieveNewsItem(mCursor);
		mCursor.move(-1);
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
		mCursor.move(1);
		mPreviousScreen = mCurrentScreen;
		mCurrentScreen = null;
		mNextScreen = null;
	    }
	    mLoadingScreen.showLoading();
	    mLoadingScreenListener.onLoadingScreenSelected(this, mCursor);
	    return;
	}
	
	if (screenPosition == ScreenPosition.Previous) {
	    mCursor.move(-1);
	    
	    mNextScreen = mCurrentScreen;
	    mCurrentScreen = mPreviousScreen;
	    mPreviousScreen = null;
	    
	} else if (screenPosition == ScreenPosition.Next) {

	    mCursor.move(1);
	    
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

    private boolean hasMore() {
	return mHasDynamicLoading && (NewsModel.MAX_STORIES_PER_CAREGORY > mCursor.getCount());
    }
    
    private boolean isLoadingScreen(ScreenPosition screenPosition) {
	if (screenPosition == ScreenPosition.Current) {
	    
	    return (mCursor.isAfterLast());
	    
	} else if (screenPosition == ScreenPosition.Next) {
	    
	    if (mCursor.isLast()) {
		return hasMore();
	    }
	}
	
	return false;
    }


    public NewsItem getLastStoryItem() {
	int currentPosition = mCursor.getPosition();
	mCursor.moveToLast();
	NewsItem item = NewsDB.retrieveNewsItem(mCursor);
	mCursor.moveToPosition(currentPosition);
	return item;
    }
    
    @Override
    public void seekToNewsItem(int position) {
	mCursor.moveToPosition(position);
    }


    @Override
    public NewsItem getCurrentNewsItem() {
	return NewsDB.retrieveNewsItem(mCursor);
    }

    @Override
    public int getStoriesCount() {
	return mCursor.getCount();
    }
}
