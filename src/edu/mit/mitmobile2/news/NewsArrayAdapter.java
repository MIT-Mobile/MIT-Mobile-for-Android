package edu.mit.mitmobile2.news;

import java.util.List;


import edu.mit.mitmobile2.objs.NewsItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NewsArrayAdapter extends ArrayAdapter<NewsItem> {

	Context mContext;	
	NewsAdapterHelper mNewsAdapterHelper;
	
	public NewsArrayAdapter(Context context, int textViewResourceId, List<NewsItem> news, NewsModel newsModel, ListView listView) {
		super(context, textViewResourceId, news);
		mNewsAdapterHelper = new NewsAdapterHelper(listView, newsModel);
		mContext = context;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = convertView;
		if (view == null) {
			view = mNewsAdapterHelper.createBlankView(mContext);
		}
		
		NewsItem newsItem = (NewsItem) getItem(position);
		mNewsAdapterHelper.populateView(view, newsItem, false);
		
		return view;
	}

}
