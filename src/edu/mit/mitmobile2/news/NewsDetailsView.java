package edu.mit.mitmobile2.news;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import android.os.Handler;

import android.webkit.WebChromeClient;

import android.app.Activity;
import android.content.Context;

import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JsResult;

import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.IdEncoder;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.NewsItem;

public class NewsDetailsView extends WebView {
	private Handler mHandler = new Handler();

	Context mContext;
	NewsItem mNewsItem;
	static final String TAG = "NewsDetailsView";
	
    static final SimpleDateFormat sDateFormat = new SimpleDateFormat("EEE d, MMM yyyy");
	
	/****************************************************/
	public NewsDetailsView(final Context context, NewsItem newsItem) {
		super(context);
		mContext = context;
		mNewsItem = newsItem;
		
		populateView();
	}
	

	private void populateView() {
		// standard view
			
		NewsModel newsModel = new NewsModel(mContext);
		newsModel.populateImages(mNewsItem);
		
		// Web template
		setFocusable(false);
		String templateHtml = readTextFromResource(R.raw.news_detail);
		
		// Set Title
		templateHtml = templateHtml.replace("__TITLE__", mNewsItem.title);
		
		// Set Author
		templateHtml = templateHtml.replace("__AUTHOR__", mNewsItem.author);
		
		// Set Date
		templateHtml = templateHtml.replace("__DATE__", sDateFormat.format(mNewsItem.postDate));
					
		// Set Description
		templateHtml = templateHtml.replace("__DEK__", mNewsItem.description);
		
		// Set Image Count
		int galleryCount = 0;
		
		if(mNewsItem.img != null) {
			templateHtml = templateHtml.replace("__THUMBNAIL_URL__", mNewsItem.img.smallURL);				
			galleryCount = mNewsItem.getAllImages().size();
		}
		templateHtml = templateHtml.replace("__GALLERY_COUNT__", galleryCount + "");								
		
		// Set Body
		templateHtml = templateHtml.replace("__BODY__", mNewsItem.body);
		
		String bookmarkClass = newsModel.isBookmarked(mNewsItem) ? "on" : "off";
		templateHtml = templateHtml.replace("__BOOKMARK__", bookmarkClass);

		Log.d(TAG,"html = " + templateHtml);
		
		getSettings().setJavaScriptEnabled(true);
		getSettings().setSupportZoom(false);
		setWebChromeClient(new MyWebChromeClient());			
		addJavascriptInterface(new JavaScriptInterface(), "newsDetail");
		loadDataWithBaseURL( "file:///android_asset/", templateHtml, "text/html", "UTF-8", null);

	}
		
	private String readTextFromResource(int newsDetail) {
		InputStream raw = getResources().openRawResource(newsDetail);
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    int i;
	    try {
	    	i = raw.read();
	    	while (i != -1) {
	    		stream.write(i);
	    		i = raw.read();
	    	}
	        raw.close();
	    }
	    catch (IOException e) {
	    	e.printStackTrace();
	    }
	    return stream.toString();
	}
	
	private static class PictureFailedToLoadHandler extends WebViewClient {
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			view.setVisibility(View.GONE);
		}
	}


    /**
     * Provides a hook for calling "alert" from javascript. Useful for
     * debugging your javascript.
     */
    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d(TAG, message);
            result.confirm();
            return true;
        }
    }

    final class JavaScriptInterface {

        JavaScriptInterface() {
        }

        /**
         * This is not called on the UI thread. Post a runnable to invoke
         * loadUrl on the UI thread.
         */

        public void clickBookmarkButton(String status) {
        	NewsModel newsModel = new NewsModel(mContext);
        	boolean bookmarkStatus = status.equals("on");
        	newsModel.setStoryBookmarkStatus(mNewsItem, bookmarkStatus);
        }
 
        public void clickShareButton() {
            mHandler.post(new Runnable() {
                public void run() {
        			String url  = "http://" + Global.getMobileWebDomain() + "/n/" + IdEncoder.shortenId(mNewsItem.story_id);
        			CommonActions.shareCustomContent(mContext, mNewsItem.title, mNewsItem.description, url);
                }
            });
        }
        
        public void clickViewImage() {
            mHandler.post(new Runnable() {
                public void run() {
					NewsImageActivity.launchActivity(mContext, mNewsItem);
                }
            });
        }

    }

}

