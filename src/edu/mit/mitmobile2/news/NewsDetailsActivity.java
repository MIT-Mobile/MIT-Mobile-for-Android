package edu.mit.mitmobile2.news;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.IdEncoder;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.MITPlainSecondaryTitleBar;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.OnMITMenuItemListener;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderNewModuleActivity;
import edu.mit.mitmobile2.SliderView;
import edu.mit.mitmobile2.news.NewsSliderCursorAdapter.OnLoadingScreenListener;
import edu.mit.mitmobile2.objs.NewsItem;

public class NewsDetailsActivity extends SliderNewModuleActivity implements StorySliderListener, OnLoadingScreenListener {
	
	static final String TAG = "NewsDetailsActivity";
	
	public static final String CATEGORY_ID_KEY = "category_id";
	static final String SEARCH_TERM_KEY = "search_term";
	static final String BOOKMARKS_KEY = "bookmarks";
	
	private static final String MENU_BOOKMARKED = "menu_bookmark";
	private static final String MENU_SHARE = "menu_share";
	
	boolean mBookmarks = false;
	int mCategoryId = -1;
	String mSearchTerm = null;
	MITPlainSecondaryTitleBar mSecondaryTitleBar;
	
	Context ctx;
	private int mStartPosition;
	private int mLastSavedPosition;
	public static String KEY_POSITION = "position";
	
	NewsModel mNewsModel;
	
	private MITMenuItem mBookmarkMenuItem;
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);

		if (mSliderAdapter.getStoriesCount() == 0) {
			// gracefull exit
			finish();
			return;
		}
	}

	protected int getPositionValue() {
		if(mLastSavedPosition > 0) {
			return mLastSavedPosition;
		}
		
		Bundle extras = getIntent().getExtras();
		
		if (extras != null){
			return extras.getInt(KEY_POSITION);
		} else {
			return 0;
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		
		super.onNewIntent(intent);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null){
		    	mSliderAdapter.seekToNewsItem(extras.getInt(KEY_POSITION));
		    	refreshScreens();
		}
		
	}
	
	private void initSecondaryTitleBar() {
		mSecondaryTitleBar = new MITPlainSecondaryTitleBar(this);
		mSecondaryTitleBar.setTitle(newsCategoryTitle());
		mSecondaryTitleBar.addMenuItem(new MITMenuItem(MENU_SHARE, "", R.drawable.menu_share));
		
		mBookmarkMenuItem = new MITMenuItem(MENU_BOOKMARKED, "", R.drawable.menu_add_bookmark);
		mSecondaryTitleBar.addMenuItem(mBookmarkMenuItem);
		
		mSecondaryTitleBar.setOnMITMenuItemListener(new OnMITMenuItemListener() {
			@Override
			public void onOptionItemSelected(String optionId) {
				// TODO Auto-generated method stub
				NewsItem newsItem = mSliderAdapter.getCurrentNewsItem();
				if(optionId.equals(MENU_BOOKMARKED)) {			
					// toggle bookmark status
					updateBookmarkMenuItem();
					mNewsModel.setStoryBookmarkStatus(newsItem, !mNewsModel.isBookmarked(newsItem));
					
				} else if(optionId == MENU_SHARE) {
					String url  = "http://" + app.getMobileWebDomain() + "/n/" + IdEncoder.shortenId(newsItem.story_id);
					CommonActions.shareCustomContent(NewsDetailsActivity.this, newsItem.title, newsItem.description, url);
				}
			}
		});
		getTitleBar().addSecondaryBar(mSecondaryTitleBar);
	}
	
	private void updateBookmarkMenuItem() {
		if (mBookmarkMenuItem.getIconResId() == R.drawable.menu_remove_bookmark) {
			mBookmarkMenuItem.setIconResId(R.drawable.menu_add_bookmark);
		} else {
			mBookmarkMenuItem.setIconResId(R.drawable.menu_remove_bookmark);
		}
		mSecondaryTitleBar.updateMenuItem(mBookmarkMenuItem);
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
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}

	static private NewsSliderAdapter mSliderAdapter;
	
	interface NewsSliderAdapter extends SliderView.Adapter {
	    public void seekToNewsItem(int position);
	
	    public NewsItem getCurrentNewsItem();
	    
	    public int getStoriesCount();
	}
	
	static interface StoryListener {

	}
	
	@Override
	protected SliderView.Adapter getSliderAdapter() {
		mStartPosition = getPositionValue();

		//initSecondaryTitleBar();
		
		mNewsModel = new NewsModel(this);
		
		Bundle extras = getIntent().getExtras();
		if(extras.containsKey(CATEGORY_ID_KEY)) {
		    
			mCategoryId = extras.getInt(CATEGORY_ID_KEY);
			Cursor newsCursor = mNewsModel.getNewsCursor(mCategoryId);
			mSliderAdapter = new NewsSliderCursorAdapter(this, newsCursor, this, true);
			((NewsSliderCursorAdapter) mSliderAdapter).setOnLoadingScreenListener(this);
			
		} else if(extras.containsKey(SEARCH_TERM_KEY)) {
		    
			mSearchTerm = extras.getString(SEARCH_TERM_KEY);
			List<NewsItem> newsItems = mNewsModel.executeLocalSearch(mSearchTerm);
			mSliderAdapter = new NewsSliderListAdapter(this, newsItems, this);
			
		} else if(extras.containsKey(BOOKMARKS_KEY)) {
			mBookmarks = true;
			Cursor newsCursor = mNewsModel.getBookmarksCursor();
			mSliderAdapter = new NewsSliderCursorAdapter(this, newsCursor, this, false);
		}
 
		mSliderAdapter.seekToNewsItem(mStartPosition);
		
		return mSliderAdapter;
	}

	@Override
	protected String getCurrentHeaderTitle() {
	    return "";
	}

	boolean mIsLoadingMoreStories = false;
	
	@Override
	public void onLoadingScreenSelected(final NewsSliderCursorAdapter adapter, final Cursor cursor) {
	    if (!mIsLoadingMoreStories) {
		mIsLoadingMoreStories = true;
		int lastStoryID = adapter.getLastStoryItem().story_id;
		final int currentSize = cursor.getCount();
		
		mNewsModel.fetchCategory(mCategoryId, lastStoryID, false, new Handler() {
		    
		    @Override
		    public void handleMessage(Message msg) {
			mIsLoadingMoreStories = false;
			if (msg.arg1 == MobileWebApi.SUCCESS) {
			    cursor.requery();
			    cursor.moveToPosition(currentSize);
			    adapter.stopLoading();
			    refreshScreens();
			} else {
			    adapter.showError();
			}
		    }
		});
	    }	    
	}

	@Override
	public void onStorySelected(NewsItem newsItem) {
	    
	    	/*
		if (mNewsModel.isBookmarked(newsItem)) {
			mBookmarkMenuItem.setIconResId(R.drawable.menu_remove_bookmark);
		} else {
			mBookmarkMenuItem.setIconResId(R.drawable.menu_add_bookmark);
		}
		mSecondaryTitleBar.updateMenuItem(mBookmarkMenuItem);
		*/
	}
}
