package edu.mit.mitmobile2.news;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.MITPlainSecondaryTitleBar;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.OnMITMenuItemListener;
import edu.mit.mitmobile2.R;

public class NewsBookmarksActivity extends NewModuleActivity {

	private NewsModel mNewsModel;
	Cursor mBookmarksCursor;
	
	ListView mListView;
	View mEmptyMessageTV;
	
	private final static String MENU_CLEAR_BOOKMARKS = "menu_clear_bookmarks";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final Context context = this;
		
		mNewsModel = new NewsModel(this);
		mBookmarksCursor = mNewsModel.getBookmarksCursor();
		
		setContentView(R.layout.news_bookmarks_list);
		
		//initSecondaryTitleBar();
		
		mListView = (ListView) findViewById(R.id.newsBookmarksLV);
		mEmptyMessageTV = findViewById(R.id.newsBookmarksListEmptyTV);
		
		ListAdapter newsAdapter = new NewsCursorAdapter(this, mListView, mNewsModel, mBookmarksCursor);
		
		mListView.setAdapter(newsAdapter);
		mListView.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> listView, View view, int position, long id) {					
					
						Intent i = new Intent(context, NewsDetailsActivity.class);
						i.putExtra(NewsDetailsActivity.KEY_POSITION, position);
						i.putExtra(NewsDetailsActivity.BOOKMARKS_KEY, true);
						startActivity(i);
					}
				}
		);
		
		updateUI();
	}
	
	private void initSecondaryTitleBar() {
		final MITPlainSecondaryTitleBar titleBar = new MITPlainSecondaryTitleBar(this);
		titleBar.setTitle("Bookmarks");
		
		if(mBookmarksCursor.getCount() > 0) {
			final MITMenuItem bookmarkItem = new MITMenuItem(MENU_CLEAR_BOOKMARKS, "", R.drawable.menu_remove_bookmark);
			titleBar.addMenuItem(bookmarkItem);	
			titleBar.setOnMITMenuItemListener(new OnMITMenuItemListener() {
				@Override
				public void onOptionItemSelected(String optionId) {
					// TODO Auto-generated method stub
					if(optionId.equals(MENU_CLEAR_BOOKMARKS)) {
						titleBar.removeMenuItem(bookmarkItem);
						mNewsModel.clearAllBookmarks(new Handler() {
							@Override
							public void handleMessage(Message msg) {
								updateUI();
							}
						});
					}
				}
			});
		}
		getTitleBar().addSecondaryBar(titleBar);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateUI();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBookmarksCursor.close();
	}
	
	private void updateUI() {		
		mBookmarksCursor.requery();
		if(mBookmarksCursor.moveToFirst()) {		
			mListView.setVisibility(View.VISIBLE);
			mEmptyMessageTV.setVisibility(View.GONE);			
		} else {
			mListView.setVisibility(View.GONE);
			mEmptyMessageTV.setVisibility(View.VISIBLE);
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
