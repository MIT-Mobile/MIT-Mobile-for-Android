package edu.mit.mitmobile2.news.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.IdEncoder;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.MITPlainSecondaryTitleBar;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.OnMITMenuItemListener;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderNewModuleActivity;
import edu.mit.mitmobile2.SliderView;
import edu.mit.mitmobile2.news.NewsModule;
import edu.mit.mitmobile2.news.beans.NewsStory;
import edu.mit.mitmobile2.news.net.NewsDownloader;

public class NewsDetailsActivity extends SliderNewModuleActivity implements LoadingScreenListener, StorySliderListener {
	
	static final String TAG = "NewsDetailsActivity";
	
	public static final String STORY_ID_KEY = "story_id";
	public static final String CATEGORY_ID_KEY = "category_id";
	public static final String SEARCH_TERM_KEY = "search_term";
	public static final String SEARCH_LIMIT = "search_limit";
	//public static final String BOOKMARKS_KEY = "bookmarks";
	
	private static final String MENU_BOOKMARKED = "menu_bookmark";
	private static final String MENU_SHARE = "menu_share";
	
	boolean mBookmarks = false;
	String mCategoryId = "";
	String mSearchTerm = null;
	MITPlainSecondaryTitleBar mSecondaryTitleBar;
	
	Context ctx;
	private int mLastSavedPosition;
	public static String KEY_POSITION = "position";
	
	static private NewsSliderAdapter mSliderAdapter;
	
	//NewsModel mNewsModel;
	
	private MITMenuItem mBookmarkMenuItem;
	NewsDownloader np;
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		np = NewsDownloader.getInstance(this);
		if (!mSliderAdapter.isLoading()  && mSliderAdapter.getStoriesCount() == 0) {
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
		if (extras != null && extras.getString(STORY_ID_KEY)!=null){
		    	mSliderAdapter.seekToNewsItem(extras.getString(STORY_ID_KEY));
		    	refreshScreens();
		}
		
	}
	
	@SuppressWarnings("unused")
	private void initSecondaryTitleBar() {
		Log.d(TAG,"initSecondaryTitleBar()");
		mSecondaryTitleBar = new MITPlainSecondaryTitleBar(this);
		mSecondaryTitleBar.setTitle(newsCategoryTitle());
		mSecondaryTitleBar.addMenuItem(new MITMenuItem(MENU_SHARE, "", R.drawable.menu_share));
		
		mBookmarkMenuItem = new MITMenuItem(MENU_BOOKMARKED, "", R.drawable.menu_add_bookmark);
		mSecondaryTitleBar.addMenuItem(mBookmarkMenuItem);
		
		mSecondaryTitleBar.setOnMITMenuItemListener(new OnMITMenuItemListener() {
			@Override
			public void onOptionItemSelected(String optionId) {
				// TODO Auto-generated method stub
				NewsStory newsItem = mSliderAdapter.getCurrentNewsItem();
				if(optionId.equals(MENU_BOOKMARKED)) {			
					// toggle bookmark status
					//updateBookmarkMenuItem();
					
				} else if(optionId == MENU_SHARE) {
					@SuppressWarnings("static-access")
					String url  = "http://" + app.getMobileWebDomain() + "/n/" + IdEncoder.shortenId(Integer.valueOf(newsItem.getId()));
					CommonActions.shareCustomContent(NewsDetailsActivity.this, newsItem.getTitle(), newsItem.getDekText(), url);
				}
			}
		});
		getTitleBar().addSecondaryBar(mSecondaryTitleBar);
	}
	
	/*
	private void updateBookmarkMenuItem() {
		if (mBookmarkMenuItem.getIconResId() == R.drawable.menu_remove_bookmark) {
			mBookmarkMenuItem.setIconResId(R.drawable.menu_add_bookmark);
		} else {
			mBookmarkMenuItem.setIconResId(R.drawable.menu_remove_bookmark);
		}
		mSecondaryTitleBar.updateMenuItem(mBookmarkMenuItem);
	}
	*/
	
	private String newsCategoryTitle() {
		NewsStory newsItem = mSliderAdapter.getCurrentNewsItem();
		if(newsItem.getCategory()!=null && newsItem.getCategory().getName()!=null){
			return newsItem.getCategory().getName();
		}else if (mSearchTerm != null) {
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
	
	@Override
	protected SliderView.Adapter getSliderAdapter() {
		//mStartPosition = getPositionValue();

		//initSecondaryTitleBar();
		

		
		Bundle extras = getIntent().getExtras();
		String story_id = null;
		if(extras.containsKey(STORY_ID_KEY)){
			story_id = extras.getString(STORY_ID_KEY);
		}
		if(extras.containsKey(CATEGORY_ID_KEY)) {
		    mSliderAdapter = new NewsSliderCursorAdapter(this, extras.getString(CATEGORY_ID_KEY),"category",0,20, this, false);
		} else if(extras.containsKey(SEARCH_TERM_KEY)) {
			int search_limit = 20;
			if(extras.containsKey(SEARCH_LIMIT)){
				search_limit = extras.getInt(SEARCH_LIMIT);
			}
			mSliderAdapter = new NewsSliderCursorAdapter(this, extras.getString(SEARCH_TERM_KEY),"search",0,search_limit, this, false);
		}/* else if(extras.containsKey(BOOKMARKS_KEY)) {
			mBookmarks  = true;
			Cursor newsCursor = mNewsModel.getBookmarksCursor();
			mSliderAdapter = new NewsSliderCursorAdapter(this, newsCursor, this, false);
		}*/
		((NewsSliderCursorAdapter) mSliderAdapter).setStartStory(story_id);
		((NewsSliderCursorAdapter) mSliderAdapter).setLoadingScreenListener(this);
		return mSliderAdapter;
	}

	//boolean mIsLoadingMoreStories = false;
	
	@Override
	public void onStoriesLoaded() {
		((NewsSliderCursorAdapter)mSliderAdapter).stopLoading();
		refreshScreens();
	}

	@Override
	public void onStorySelected(NewsStory newsItem) {
		// TODO Auto-generated method stub
		
	}


}
