package edu.mit.mitmobile2.news.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Handler;

import android.webkit.WebChromeClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
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
import edu.mit.mitmobile2.news.beans.NewsStory;

public class NewsDetailsView extends WebView {
	private Handler mHandler = new Handler();


	NewModuleActivity mModuleActivity;
	NewsStory mNewsItem;
	static final String TAG = "NewsDetailsView";
	
    static final SimpleDateFormat sDateFormat = new SimpleDateFormat("EEE d, MMM yyyy",Locale.US);
    static final SimpleDateFormat fromDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZZZZZ", Locale.US);
    SliderActivity mSliderActivity;
    int size_width;
	/****************************************************/
    
    public NewsDetailsView(Context context){
    	this(context,new NewsStory());
    }
	public NewsDetailsView(Context context, NewsStory newsItem) {
		super(context);
		mModuleActivity = (NewModuleActivity) context;
		mNewsItem = newsItem;
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		size_width = (int)( metrics.widthPixels/metrics.density);
		Log.d("NEWS", "Width is: "+size_width + " density: "+metrics.density);
		populateView();
	}
	

	@SuppressLint("SetJavaScriptEnabled")
	private void populateView() {
		// standard view
			
		//NewsModel newsModel = new NewsModel(mModuleActivity);
		//newsModel.populateImages(mNewsItem);
		
		// Web template
		setFocusable(false);
		String templateHtml = readTextFromResource(R.raw.news_detail);
		
		// Set Title
		if(mNewsItem.getTitle()!=null)
			templateHtml = templateHtml.replace("__TITLE__", mNewsItem.getTitle());
		else
			templateHtml = templateHtml.replace("__TITLE__", "");
		// Set Author
		if(mNewsItem.getAuthor()!=null)
			templateHtml = templateHtml.replace("__AUTHOR__", mNewsItem.getAuthor());
		else
			templateHtml = templateHtml.replace("__AUTHOR__", "");
		// Set Date
		if(mNewsItem.getPublishedAt()!=null){
			try {
				templateHtml = templateHtml.replace("__DATE__", sDateFormat.format(fromDate.parse(mNewsItem.getPublishedAt())));
			} catch (ParseException e) {
				Log.d("NEWS",e.getLocalizedMessage());
				templateHtml = templateHtml.replace("__DATE__","");
			}
		}else{
			templateHtml = templateHtml.replace("__DATE__","");
		}
		// Set Description
		if(mNewsItem.getDek()!=null)
			templateHtml = templateHtml.replace("__DEK__", mNewsItem.getDek());
		else
			templateHtml = templateHtml.replace("__DEK__", "");
		
		
		if(mNewsItem.getCoverImage() != null) {
			templateHtml = templateHtml.replace("__THUMBNAIL_URL__", 
					mNewsItem.getCoverImage().getRepresentationBestFitByWidth(size_width).getUrl());
		}else{
			templateHtml = templateHtml.replace("__THUMBNAIL_URL__", "");
		}
		// Set Image Count
		int galleryCount = 0;
		if(mNewsItem.getGalleryImages()!=null)
				galleryCount = mNewsItem.getGalleryImages().size();
		templateHtml = templateHtml.replace("__GALLERY_COUNT__", galleryCount + "");								
		
		// Set Body
		if(mNewsItem.getBodyHtml()!=null)
			templateHtml = templateHtml.replace("__BODY__", mNewsItem.getBodyHtml());
		else
			templateHtml = templateHtml.replace("__BODY__", "");
		String bookmarkClass = "off"; //newsModel.isBookmarked(mNewsItem) ? "on" : "off";
		templateHtml = templateHtml.replace("__BOOKMARK__", bookmarkClass);

		//Log.d(TAG,"html = " + templateHtml);
		
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
	
	/*
	@SuppressWarnings("unused")
	private static class PictureFailedToLoadHandler extends WebViewClient {
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			view.setVisibility(View.GONE);
		}
	}
*/

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
        	/*NewsModel newsModel = new NewsModel(mModuleActivity);
        	boolean bookmarkStatus = status.equals("on");
        	newsModel.setStoryBookmarkStatus(mNewsItem, bookmarkStatus);*/
        }
 
        public void clickShareButton() {
            mHandler.post(new Runnable() {
                @Override
				public void run() {
        			String url  = "http://" + Global.getMobileWebDomain() + "/n/" + IdEncoder.shortenId(Integer.parseInt(mNewsItem.getId()));
        			CommonActions.shareCustomContent(mModuleActivity, mNewsItem.getTitle(), mNewsItem.getDek(), url);
                }
            });
        }
        

        public void clickViewImage() {
            mHandler.post(new Runnable() {
                @Override
				public void run() {
					NewsImageActivity.launchActivity(mModuleActivity, mNewsItem);

                }
            });
        }

    }

}

