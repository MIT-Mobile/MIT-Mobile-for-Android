package edu.mit.mitmobile2.news;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;

public class NewsBookmarksActivity extends ModuleActivity {

	private NewsModel mNewsModel;
	Cursor mBookmarksCursor;
	
	ListView mListView;
	View mEmptyMessageTV;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		final Context context = this;
		
		mNewsModel = new NewsModel(this);
		mBookmarksCursor = mNewsModel.getBookmarksCursor();
		
		setContentView(R.layout.news_bookmarks_list);
		
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
	protected Module getModule() {
		return new NewsModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	final static int MENU_CLEAR_BOOKMARKS = MENU_SEARCH + 1;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == MENU_CLEAR_BOOKMARKS) {
			mNewsModel.clearAllBookmarks(new Handler() {
				@Override
				public void handleMessage(Message msg) {
					updateUI();
				}
			});
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		if(mBookmarksCursor.getCount() > 0) {
			menu.add(0, MENU_CLEAR_BOOKMARKS, Menu.NONE, "Clear Bookmarks")
				.setIcon(R.drawable.menu_clear_recent);
		}
	}
}
