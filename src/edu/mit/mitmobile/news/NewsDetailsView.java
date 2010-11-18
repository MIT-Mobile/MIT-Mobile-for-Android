package edu.mit.mitmobile.news;

import java.text.SimpleDateFormat;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ScrollView;
import android.widget.TextView;

import edu.mit.mitmobile.LockingScrollView;
import edu.mit.mitmobile.R;
import edu.mit.mitmobile.StyledContentHTML;
import edu.mit.mitmobile.objs.NewsItem;

public class NewsDetailsView extends LockingScrollView {
	
	Context mContext;
	NewsItem mNewsItem;
	
    static final SimpleDateFormat sDateFormat = new SimpleDateFormat("EEE d, MMM yyyy");
	
	/****************************************************/
	public NewsDetailsView(final Context context, NewsItem newsItem) {
		super(context);
		mContext = context;
		mNewsItem = newsItem;

		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		vi.inflate(R.layout.news_details, this);
	}
	
	public void populateView() {
	
		TextView tv;		
		
		NewsModel newsModel = new NewsModel(mContext);
		newsModel.populateImages(mNewsItem);
		
		tv = (TextView) findViewById(R.id.newsDetailsTitleTV);
		tv.setText(mNewsItem.title);
		
		tv = (TextView) findViewById(R.id.newsAuthorTV);
		tv.setText(mNewsItem.author);
		
		tv = (TextView) findViewById(R.id.newsDateTV);
		tv.setText(sDateFormat.format(mNewsItem.postDate));
		
		tv = (TextView) findViewById(R.id.newsDeckTV);
		tv.setText(mNewsItem.description);
		
		////////////////
		
		// Images...
		
		final WebView wv = (WebView) findViewById(R.id.newsDetailsWV);
		wv.setFocusable(false);
		
		if(mNewsItem.img != null) {
			wv.setWebViewClient(new PictureFailedToLoadHandler());
			wv.loadUrl(mNewsItem.img.smallURL);
		} else {
			wv.setVisibility(GONE);
		}		
		
		//wv.setFocusable(false); // needed so OnClick() can work
		wv.setBackgroundColor(0); 
		wv.invalidate();
		wv.requestLayout();
		
		// Expand images
		OnTouchListener clickListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP) {
					NewsImageActivity.launchActivity(mContext, mNewsItem);
				}
				return true;
			}
		};	
		wv.setOnTouchListener(clickListener);	
		findViewById(R.id.newsMoreIV).setOnTouchListener(clickListener);
		
		
		int n = mNewsItem.getAllImages().size();
		
		if (n == 0) {
			View view = findViewById(R.id.newsDetailsImageContainer);
			view.setVisibility(GONE);
		} else if(n == 1) {
			tv = (TextView) findViewById(R.id.newsMoreTV);
			tv.setText("View image");
		} else {
			tv = (TextView) findViewById(R.id.newsMoreTV);
			tv.setText("View " + Integer.toString(n) + " images");
		}
		
		WebView webview = (WebView) findViewById(R.id.newsBodyTV);
		webview.setFocusable(false);
		webview.loadDataWithBaseURL(null, StyledContentHTML.html(mNewsItem.body), "text/html", "utf-8", null);	
		
	}
	
	public void destroy() {
		WebView wv = (WebView) findViewById(R.id.newsDetailsWV);
		wv.destroy();
		
		wv = (WebView) findViewById(R.id.newsBodyTV);
		wv.destroy();
		
		removeAllViews();
	}
	
	public ScrollView getScrollView() {
		return (ScrollView) findViewById(R.id.newsDetailScrollView);
	}
	
	private static class PictureFailedToLoadHandler extends WebViewClient {
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			view.setVisibility(View.GONE);
		}
	}
}

