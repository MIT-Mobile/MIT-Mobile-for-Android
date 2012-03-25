package edu.mit.mitmobile2.news;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.MITPlainSecondaryTitleBar;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.SliderNewModuleActivity;
import edu.mit.mitmobile2.objs.NewsItem;

public class NewsDetailsActivity extends SliderNewModuleActivity {
	
	static final String TAG = "NewsDetailsActivity";
	
	public static final String CATEGORY_ID_KEY = "category_id";
	static final String SEARCH_TERM_KEY = "search_term";
	static final String BOOKMARKS_KEY = "bookmarks";
	
	boolean mBookmarks = false;
	int mCategoryId = -1;
	String mSearchTerm = null;
	MITPlainSecondaryTitleBar mSecondaryTitleBar;
	
	Context ctx;
	private int mStartPosition;
	
	NewsModel mNewsModel;
	List<NewsItem> mNewsItems;
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);

		mStartPosition = getPositionValue();
	
		Bundle extras = getIntent().getExtras();
		if(extras.containsKey(CATEGORY_ID_KEY)) {
			mCategoryId = extras.getInt(CATEGORY_ID_KEY);
		} else if(extras.containsKey(SEARCH_TERM_KEY)) {
			mSearchTerm = extras.getString(SEARCH_TERM_KEY);
		} else if(extras.containsKey(BOOKMARKS_KEY)) {
			mBookmarks = true;
		}
 
		mSecondaryTitleBar = new MITPlainSecondaryTitleBar(this);
		mSecondaryTitleBar.setTitle(newsCategoryTitle());
		getTitleBar().addSecondaryBar(mSecondaryTitleBar);
		
		mNewsModel = new NewsModel(this);		
		initalizeNewsList();

		if (mNewsItems == null || mNewsItems.size() == 0) {
			// gracefull exit
			finish();
			return;
		}
		
		new Handler().postDelayed(
			new Runnable() {
				@Override
				public void run() {
					createScreens();
				}
			}, 
			200
		);
	}

	private String newsCategoryTitle() {
		if(mCategoryId >= 0) {
			NewsModel newsModel = new NewsModel(this);
			return newsModel.getCategoryTitle(mCategoryId);
		} else if (mSearchTerm != null) {
			return "Search Results";
		} else if (mBookmarks) {
			return "Bookmarks";
		}
		return null;
	}
	
	@Override
	protected void onDestroy() {
		mNewsItems = null;
		super.onDestroy();
	}
	
	private void initalizeNewsList() {
		if(mCategoryId >= 0 || mBookmarks) {
			ArrayList<NewsItem> newsItems = new ArrayList<NewsItem>();			
			Cursor newsCursor = null;
			
			if(mCategoryId >= 0) {
				newsCursor = mNewsModel.getNewsCursor(mCategoryId);
			} else if(mBookmarks) {
				newsCursor = mNewsModel.getBookmarksCursor();
			}
			
			newsCursor.moveToFirst();
			while(!newsCursor.isAfterLast()) {
				NewsItem newsItem = NewsDB.retrieveNewsItem(newsCursor);
				newsItems.add(newsItem);
				newsCursor.moveToNext();
			}
			newsCursor.close();
			
			mNewsItems = newsItems;
		
		} else if(mSearchTerm != null) {			
			mNewsItems = mNewsModel.executeLocalSearch(mSearchTerm);
		}	
	}
	
	/****************************************************/
	void createScreens() {
		if(mNewsItems == null) {
			// activity must have already been closed
			// no need to layout screens
			return;
		}
		
		int totalStories = mNewsItems.size();

		for(int index=0; index < totalStories; index++) {
			NewsItem newsItem = mNewsItems.get(index);
			String headerTitle = "Story " + Integer.toString(index+1) + " of " + Integer.toString(totalStories);
			//DEBUG
//			if (index == 0) {
//				Log.d(TAG,"author = " + newsItem.author);
//				Log.d(TAG,"body = " + newsItem.body);
//				Log.d(TAG,"description = " + newsItem.description);
//				Log.d(TAG,"link = " + newsItem.link);
//				Log.d(TAG,"thumbURL = " + newsItem.thumbURL);
//				Log.d(TAG,"title = " + newsItem.title);
//			}
			//DEBUG
			addScreen(new NewsDetailsScreen(this, newsItem), newsItem.title, headerTitle);
		}
		
		setPosition(mStartPosition);
	}
	
	/*
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		NewsItem newsItem = mNewsItems.get(getPosition());
		if(item.getItemId() == MENU_BOOKMARKED) {			
			// toggle bookmark status
			mNewsModel.setStoryBookmarkStatus(newsItem, !mNewsModel.isBookmarked(newsItem));
			return true;
		} else if(item.getItemId() == MENU_SHARE) {
			String url  = "http://" + this.getMobileWebDomain() + "/n/" + IdEncoder.shortenId(newsItem.story_id);
			CommonActions.shareCustomContent(this, newsItem.title, newsItem.description, url);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	*/
	
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
			// TODO Auto-generated method stub
			
		}

		@Override
		public void updateView() {
			mNewsDetailView.populateView();	
		}

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
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return new NewsModule();
	}

	@Override
	protected boolean isScrollable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}
}
