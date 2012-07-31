package edu.mit.mitmobile2.news;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.VideoView;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderActivity;
import edu.mit.mitmobile2.SliderActivity.OnBackPressedListener;
import edu.mit.mitmobile2.SliderActivity.OnPausedListener;
import edu.mit.mitmobile2.objs.NewsItem;

public class NewsDetailsView extends LockingScrollView {
	private Handler mHandler = new Handler();
	private WebView webview;

	NewsItem mNewsItem;
	static final String TAG = "NewsDetailsView";
	
    static final SimpleDateFormat sDateFormat = new SimpleDateFormat("EEE d, MMM yyyy");
    
    SliderActivity mSliderActivity;
	
	/****************************************************/
	public NewsDetailsView(final Context context, NewsItem newsItem) {
		super(context);
		mNewsItem = newsItem;
		mSliderActivity = (SliderActivity) context;

		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vi.inflate(R.layout.news_details, this);
	}
	

	public void populateView() {
		// standard view
			
		NewsModel newsModel = new NewsModel(mSliderActivity);
		newsModel.populateImages(mNewsItem);
		
		// Web template
		webview = (WebView) findViewById(R.id.newsDetailsWV);
		webview.setFocusable(false);
		String templateHtml = readTextFromResource(R.raw.news_detail);
		webview.getSettings().setPluginsEnabled(true);
//		webview.getSettings().setPluginState(WebSettings.PluginState.ON);
		
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

		Log.d(TAG,"html = " + templateHtml);
		
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setSupportZoom(false);
		webview.setWebChromeClient(new MyWebChromeClient());			
		webview.addJavascriptInterface(new JavaScriptInterface(), "newsDetail");
		webview.loadDataWithBaseURL( "file:///android_asset/", templateHtml, "text/html", "UTF-8", null);

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

	public void destroy() {
		WebView wv = (WebView) findViewById(R.id.newsDetailsWV);
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


    /**
     * Provides a hook for calling "alert" from javascript. Useful for
     * debugging your javascript.
     */
    final class MyWebChromeClient extends WebChromeClient implements OnCompletionListener, OnErrorListener {
    	
    	VideoView mVideoView;
    	CustomViewCallback mCustomViewCallback;
    	
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d(TAG, message);
            result.confirm();
            return true;
        }
        
        private void stopVideo() {
			if (mVideoView != null) {
				mVideoView.stopPlayback();
			}
			mVideoView = null;
			mSliderActivity.setOnBackPressedListener(null);
			mSliderActivity.setOnPausedListener(null);
			mSliderActivity.hideFullScreen();
			mCustomViewCallback.onCustomViewHidden();       	
        }
        
        @Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			stopVideo();
			return false;
		}

		@Override
		public void onCompletion(MediaPlayer mp) {
			stopVideo();
			
		}
        
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            mCustomViewCallback = callback;
            if (view instanceof FrameLayout){
                FrameLayout frame = (FrameLayout) view;
                
                // finding the VideoView is only required before honeycomb
                if (frame.getFocusedChild() instanceof VideoView){
                    mVideoView = (VideoView) frame.getFocusedChild();
                    mVideoView.setOnCompletionListener(this);
                    mVideoView.setOnErrorListener(this);
                    mVideoView.start();
                }
            }
            mSliderActivity.showFullScreen(view);
            mSliderActivity.setOnBackPressedListener(new OnBackPressedListener() {
				@Override
				public boolean onBackPressed() {
					stopVideo();
					return true;
				}
            });
            mSliderActivity.setOnPausedListener(new OnPausedListener() {
				@Override
				public void onPaused() {
					if (mVideoView == null) {
						stopVideo();
					}
				}
            });
            
            
        }
		
        
		@Override
        public View getVideoLoadingProgressView() { 
                ProgressBar bar = new ProgressBar(mSliderActivity); 
                return bar; 
        } 
        
    }

    final class JavaScriptInterface {

        JavaScriptInterface() {
        }

        /**
         * This is not called on the UI thread. Post a runnable to invoke
         * loadUrl on the UI thread.
         */

        public void clickViewImage() {
            mHandler.post(new Runnable() {
                public void run() {
					NewsImageActivity.launchActivity(mSliderActivity, mNewsItem);
                }
            });
        }

    }

}

