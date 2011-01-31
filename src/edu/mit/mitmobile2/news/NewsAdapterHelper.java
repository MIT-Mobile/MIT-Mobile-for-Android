package edu.mit.mitmobile2.news;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.NewsItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NewsAdapterHelper  {

	// cache bitmaps in memory
	// so png png decompression does not need to done
	// while scrolling the list
	// story_ids -> bitmaps
	HashMap<Integer, SoftReference<Bitmap>> mThumbnails;
	private NewsModel mNewsModel;
	private ListView mListView;
	
	public NewsAdapterHelper(ListView listView, NewsModel newsModel) {
		mThumbnails = new HashMap<Integer, SoftReference<Bitmap>>();
		mNewsModel = newsModel;
		mListView = listView;
	}

	public void populateView(View view, final NewsItem newsItem, boolean saveThumbnail) {
		if (newsItem != null) {
			view.setTag(new Integer(newsItem.story_id));
			TextView newsTitleTV = (TextView) view.findViewById(R.id.newsRowTV);
			newsTitleTV.setText(newsItem.title);
			
			TextView newsDeckTV = (TextView) view.findViewById(R.id.newsRowDeckTV);
			newsDeckTV.setText(newsItem.description);
			
			// ImageView
			final ImageView newsIV = (ImageView) view.findViewById(R.id.newsRowIV);
	
			if(newsItem.thumbURL == null) {
				newsIV.setImageResource(R.drawable.news_placeholder);
			} else {
				// attempt to grab bitmap from cache
				SoftReference<Bitmap> bitmapReference = mThumbnails.get(newsItem.story_id);
				Bitmap thumbnailBitmap = null;
				if(bitmapReference != null) {
					thumbnailBitmap = bitmapReference.get();
				}
				
				if(thumbnailBitmap != null) {
					newsIV.setImageBitmap(thumbnailBitmap);
				} else {
					// TODO show an image loading indicator
					
					// this Handler will run on this thread (UI)
					final Handler imgHandler = new Handler() {
						@Override
						public void handleMessage(Message message) {
							super.handleMessage(message);
							
							if(message.arg1 == NewsModel.FETCH_SUCCESSFUL) {
								Bitmap bitmap = (Bitmap) message.obj;
								mThumbnails.put(newsItem.story_id, new SoftReference<Bitmap>(bitmap));
				            
								// only invalidate list view 
								// if this row is currently visible 
								// this is an optimization to improve responsiveness
								if(isNewsItemVisible(newsItem)) {
									mListView.invalidateViews();
								}
							}
						}
					};
					
					mNewsModel.fetchThumbnail(imgHandler, newsItem, saveThumbnail);
					newsIV.setImageResource(R.drawable.news_placeholder);
				}
			}
		}
	}
	
	private boolean isNewsItemVisible(NewsItem newsItem) {
		int visibleCount = mListView.getChildCount();
		for(int i = 0; i < visibleCount; i++) {
			Integer storyId = (Integer) mListView.getChildAt(i).getTag();
			if( (storyId != null) && (storyId == newsItem.story_id) ) {
				return true;
			}
		} 
		
		return false;
	}
	
	public View createBlankView(Context context) {
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return vi.inflate(R.layout.news_row, null);
	}
}
