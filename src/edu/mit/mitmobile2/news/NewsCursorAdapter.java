package edu.mit.mitmobile2.news;

import edu.mit.mitmobile2.objs.NewsItem;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;

public class NewsCursorAdapter extends CursorAdapter {
	
	NewsAdapterHelper mNewsAdapterHelper;
	
	public NewsCursorAdapter(Context context, ListView listView, NewsModel newsModel, Cursor cursor) {
		super(context, cursor);
		mNewsAdapterHelper  = new NewsAdapterHelper(listView, newsModel);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final NewsItem ni = NewsDB.retrieveNewsItem(cursor);
		mNewsAdapterHelper.populateView(view, ni, true);
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mNewsAdapterHelper.createBlankView(context);
		
		bindView(view, context, cursor);
		
		return view;
	}

}
