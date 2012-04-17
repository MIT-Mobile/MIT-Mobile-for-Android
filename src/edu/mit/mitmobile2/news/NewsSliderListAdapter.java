package edu.mit.mitmobile2.news;

import java.util.List;

import android.content.Context;
import android.view.View;

import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.SliderListAdapter;
import edu.mit.mitmobile2.news.NewsDetailsActivity.NewsSliderAdapter;
import edu.mit.mitmobile2.objs.NewsItem;

public class NewsSliderListAdapter extends SliderListAdapter implements NewsSliderAdapter {

    List<NewsItem> mNewsItems;
    
    StorySliderListener mStoryListener;
    
    public NewsSliderListAdapter(Context context, List<NewsItem> newsItems, StorySliderListener storyListener) {
	mNewsItems = newsItems;
	for (NewsItem newsItem : newsItems) {
	    addScreen(new NewsDetailsScreen(context, newsItem));
	}
	mStoryListener = storyListener;
    }
    
    
    @Override
    public void seekToNewsItem(int position) {
	seekTo(position);
    }

    @Override
    public NewsItem getCurrentNewsItem() {
	return mNewsItems.get(getPosition());
    }

    private class NewsDetailsScreen implements SliderInterface {

		NewsItem mNewsItem;
		Context mContext;
		NewsDetailsView mNewsDetailView;
		
		public NewsDetailsScreen(Context context, NewsItem newsItem) {
			mContext = context;
			mNewsItem = newsItem;
		}

		@Override
		public View getView() {
			mNewsDetailView = new NewsDetailsView(mContext, mNewsItem);
			
			return mNewsDetailView;
			
		}

		@Override
		public void onSelected() {
		    mStoryListener.onStorySelected(mNewsItem);
		}

		@Override
		public void updateView() { }

		@Override
		public LockingScrollView getVerticalScrollView() {
			return mNewsDetailView;
		}

		@Override
		public void onDestroy() {
			if(mNewsDetailView != null) {
				mNewsDetailView.destroy();
			}
		}
    }

    @Override
    public int getStoriesCount() {
	return mNewsItems.size();
    }
}
