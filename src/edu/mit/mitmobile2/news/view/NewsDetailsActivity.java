package edu.mit.mitmobile2.news.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.IdEncoder;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
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
	
	//private static final String MENU_BOOKMARKED = "menu_bookmark";
	//private static final String MENU_SHARE = "menu_share";
	private Handler mHandler = new Handler();
	boolean mBookmarks = false;
	String mCategoryId = "";
	String mSearchTerm = null;
	//MITPlainSecondaryTitleBar mSecondaryTitleBar;
	
	Context ctx;
	//private int mLastSavedPosition;
	public static String KEY_POSITION = "position";
	
	static private NewsSliderAdapter mSliderAdapter;
	
	//NewsModel mNewsModel;
	
	//private MITMenuItem mBookmarkMenuItem;
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
	
	@Override
	protected void onNewIntent(Intent intent) {
		
		super.onNewIntent(intent);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.getString(STORY_ID_KEY)!=null){
		    	mSliderAdapter.seekToNewsItem(extras.getString(STORY_ID_KEY));
		    	refreshScreens();
		}
		
	}
	@Override
	protected List<MITMenuItem> getSecondaryMenuItems(){
		ArrayList<MITMenuItem> items = new ArrayList<MITMenuItem>();
		
		items.add(new MITMenuItem("share", "Share"));
		return items;
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
	protected void onOptionSelected(String optionId) {
		//Toast.makeText(this,optionId,Toast.LENGTH_LONG).show();
		if(optionId.equals("share")){
			final NewsStory n = mSliderAdapter.getCurrentNewsItem();
			mHandler.post(new Runnable() {
                @Override
				public void run() {
        			String url  = "http://" + Global.getMobileWebDomain() + "/n/" + IdEncoder.shortenId(Integer.parseInt(n.getId()));
        			CommonActions.shareCustomContent(NewsDetailsActivity.this, n.getTitle(), n.getDek(), url);
                }
            });
		}
	}
	
	@Override
	protected SliderView.Adapter getSliderAdapter() {
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
		}
		((NewsSliderCursorAdapter) mSliderAdapter).setStartStory(story_id);
		((NewsSliderCursorAdapter) mSliderAdapter).setLoadingScreenListener(this);
		return mSliderAdapter;
	}

	
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
