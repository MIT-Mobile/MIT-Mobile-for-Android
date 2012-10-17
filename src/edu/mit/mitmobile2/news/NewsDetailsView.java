package edu.mit.mitmobile2.news;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Handler;

import android.webkit.WebChromeClient;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JsResult;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.VideoView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.IdEncoder;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.NewModuleActivity.OnBackPressedListener;
import edu.mit.mitmobile2.NewModuleActivity.OnPausedListener;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderActivity;
import edu.mit.mitmobile2.objs.NewsItem;

public class NewsDetailsView extends WebView {
	private Handler mHandler = new Handler();


	NewModuleActivity mModuleActivity;
	NewsItem mNewsItem;
	static final String TAG = "NewsDetailsView";
	
    static final SimpleDateFormat sDateFormat = new SimpleDateFormat("EEE d, MMM yyyy");
    
    SliderActivity mSliderActivity;
	
	/****************************************************/
	public NewsDetailsView(Context context, NewsItem newsItem) {
		super(context);
		mModuleActivity = (NewModuleActivity) context;
		mNewsItem = newsItem;
		
		populateView();
	}
	

	private void populateView() {
		// standard view
			
		NewsModel newsModel = new NewsModel(mModuleActivity);
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
		getSettings().setPluginsEnabled(true);
		//getSettings().setPluginState(PluginState.ON);

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
			mModuleActivity.setOnBackPressedListener(null);
			mModuleActivity.setOnPausedListener(null);
			mModuleActivity.hideFullScreen();
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
        		
        // http://stackoverflow.com/questions/3815090/webview-and-html5-video
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
            mModuleActivity.showFullScreen(view);
            mModuleActivity.setOnBackPressedListener(new OnBackPressedListener() {
				@Override
				public boolean onBackPressed() {
					stopVideo();
					return true;
				}
            });
            mModuleActivity.setOnPausedListener(new OnPausedListener() {
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
                ProgressBar bar = new ProgressBar(mModuleActivity); 
                return bar; 
        }       
    }
	   
    @Override
    public void onSizeChanged(final int w, int h, int oldw, int oldh) {
    	super.onSizeChanged(w, h, oldw, oldh);
    	
    	new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
		    	if (w > 0) {
		    		loadUrl("javascript:resizeVideos(" + w + ")");
		    	}
			}
    		
    	}, 200);
    }
    
    final class JavaScriptInterface {

        JavaScriptInterface() {
        }

        /**
         * This is not called on the UI thread. Post a runnable to invoke
         * loadUrl on the UI thread.
         */
        public void clickBookmarkButton(String status) {
        	NewsModel newsModel = new NewsModel(mModuleActivity);
        	boolean bookmarkStatus = status.equals("on");
        	newsModel.setStoryBookmarkStatus(mNewsItem, bookmarkStatus);
        }
 
        public void clickShareButton() {
            mHandler.post(new Runnable() {
                public void run() {
        			String url  = "http://" + Global.getMobileWebDomain() + "/n/" + IdEncoder.shortenId(mNewsItem.story_id);
        			CommonActions.shareCustomContent(mModuleActivity, mNewsItem.title, mNewsItem.description, url);
                }
            });
        }
        

        public void clickViewImage() {
            mHandler.post(new Runnable() {
                public void run() {
					NewsImageActivity.launchActivity(mModuleActivity, mNewsItem);

                }
            });
        }

    }

}

