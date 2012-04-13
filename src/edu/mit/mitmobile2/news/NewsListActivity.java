package edu.mit.mitmobile2.news;

import java.util.Date;
import java.util.HashMap;

import edu.mit.mitmobile2.CategoryNewModuleActivity;
import edu.mit.mitmobile2.HighlightEffects;
import edu.mit.mitmobile2.LoaderBar;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.NewsItem;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

public class NewsListActivity extends CategoryNewModuleActivity {
	
	final static int MAX_STORIES = 200;
	final static String LOAD_MORE_ARTICLES = "Load 10 more articles...";
	final static String LOADING_ARTICLES = "Loading...";
	public static final String TAG = "NewsListSliderActivity";
	
	private boolean mCurrentlyLoading = false;
	
	NewsCursorAdapter mCursorAdapter;
	private NewsModel mNewsModel;
	
	EditText searchET;
	
	int category_id;
	
	HashMap<Integer, Integer> last_story_ids;

	
	Context mContext;

	protected Cursor mCursor;
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Log.d(TAG,"onCreate()");
		super.onCreate(savedInstanceState);
		
		mContext = this;
		
		mNewsModel = new NewsModel(this);
		
		createView();
	}
	/****************************************************/
	void createView() {
		for(int i = 0; i < NewsModel.category_ids.length; i++) {
			addCategory(new NewsListSliderInterface(NewsModel.category_ids[i]), NewsModel.category_titles[i], NewsModel.category_titles[i]);
		}		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mNewsModel.stop();
	}	

	private class NewsListSliderInterface implements SliderInterface {

		private int mCategoryId;
		private Integer mLastStoryId = null;
		private View mView;
		private ListView mNewsListView;
		private LoaderBar mLoaderBar;
		private Cursor mNewsCursor;
		private TwoLineActionRow mFooterView;
		
		NewsListSliderInterface(int categoryId) {
			mCategoryId = categoryId;
		}
		
		@Override
		public View getView() {
			LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			mView = inflater.inflate(R.layout.news, null);
			
			mNewsListView = (ListView) mView.findViewById(R.id.newsCategoryLV);
			mNewsListView.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
						if(view == mFooterView) {
							getMoreNewsStories();
							return;
						}
						
						Cursor newsCursor = (Cursor) listView.getItemAtPosition(position);
						NewsItem newsItem = NewsDB.retrieveNewsItem(newsCursor);
						NewsModel newsModel = new NewsModel(mContext);
						newsModel.markAsRead(newsItem);
						
						
						Intent i = new Intent(mContext, NewsDetailsActivity.class);
						i.putExtra(NewsDetailsActivity.KEY_POSITION, position);
						i.putExtra(NewsDetailsActivity.CATEGORY_ID_KEY, mCategoryId);
						startActivity(i);
					}
				}
			);
			
			mLoaderBar = (LoaderBar) mView.findViewById(R.id.newsLoaderBar);
			mLoaderBar.setFailedMessage("Error loading news headlines.");
			mLoaderBar.enableAnimation();
			
			return mView;
		}

		private void getMoreNewsStories() {
			// this Handler will run on this thread (UI)
			final Handler uiHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					
					mCurrentlyLoading = false;
					mFooterView.setTitle(LOAD_MORE_ARTICLES);
					HighlightEffects.restoreDefaultHighlightingEffects(mFooterView);
					
					if(msg.arg1 == NewsModel.FETCH_SUCCESSFUL) {
						// update the UI
						if (mNewsCursor.isClosed()) {
							mNewsListView.removeFooterView(mFooterView);
							mNewsCursor = null;
							initalizeCursorAdapter();
						}
						
						mNewsCursor.requery();
					
						mNewsCursor.moveToLast();
						int newLastStoryId = NewsDB.retrieveNewsItem(mNewsCursor).story_id;
						
						if(mLastStoryId == null || newLastStoryId != mLastStoryId) {
							mLastStoryId = newLastStoryId; 
						} else {
							mNewsListView.removeFooterView(mFooterView);
						}
						
						if(mNewsCursor.getCount() >= MAX_STORIES) {
							mNewsListView.removeFooterView(mFooterView);
						}
						
						mLoaderBar.endLoading();
					} else if(msg.arg1 == NewsModel.FETCH_FAILED) {
						mLoaderBar.errorLoading();
					}
				}
			};

			if(mCurrentlyLoading) {
				// early exit loading already in progress
				return;
			}
			
			initalizeCursorAdapter();
			
			// check to see if we really need to call the network for data
			// last story id being null means this is the initial query
			if(mLastStoryId == null && mNewsModel.isCategoryFresh(mCategoryId)) {
				// skip the fetch step
				mLoaderBar.setLastLoaded(mNewsModel.getCategoryLastLoaded(mCategoryId));
				uiHandler.sendMessage(NewsModel.messageFetchSuccess());
			} else {
				mCurrentlyLoading = true;
				mLoaderBar.setLastLoaded(new Date());
				mLoaderBar.startLoading();
				mFooterView.setTitle(LOADING_ARTICLES);
				HighlightEffects.turnOffHighlightingEffects(mFooterView);
				mNewsModel.fetchCategory(mCategoryId, mLastStoryId, false, uiHandler);
			}
		}
		
		@Override
		public void onSelected() {			
			if(mLastStoryId == null) {
				// we use a delay (this allows the UI to be more responsive)
				Runnable runnable = new Runnable() {					
					@Override
					public void run() {
						getMoreNewsStories();						
					}
				};
				
				new Handler().postDelayed(runnable, 200);
			}
		}

		@Override
		public void updateView() {
			
			// delay makes UI more responsive
			new Handler().postDelayed(
				new Runnable() {
					@Override
					public void run() {
						initalizeCursorAdapter();						
					}
				},
				400
			);
		}
		
		private void initalizeCursorAdapter() {
			if(mNewsCursor == null) {
				// create a footer row (that allows for asking more articles)
				Context context = NewsListActivity.this;

				mFooterView = new TwoLineActionRow(context);
				mFooterView.setTitle(LOAD_MORE_ARTICLES);
				mNewsListView.addFooterView(mFooterView);
				
				
				mNewsCursor = mNewsModel.getNewsCursor(mCategoryId);
				ListAdapter newsAdapter = new NewsCursorAdapter(mContext, mNewsListView, mNewsModel, mNewsCursor); 
				mNewsListView.setAdapter(newsAdapter);																
			}
		}

		@Override
		public LockingScrollView getVerticalScrollView() {
			return null;
		}

		@Override
		public void onDestroy() {
			if(mNewsCursor != null) {
				mNewsCursor.close();
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
		if(item.getItemId() == MENU_NEWSBOOKMARKS) {
			Intent intent = new Intent(this, NewsBookmarksActivity.class);
			startActivity(intent);
			return true;
		} 
		
		return super.onOptionsItemSelected(item);
		*/
		return false;
	}
	
	
	@Override
	public boolean isModuleHomeActivity() {
		return true;
	}
	
	@Override
	protected NewModule getNewModule() {
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
