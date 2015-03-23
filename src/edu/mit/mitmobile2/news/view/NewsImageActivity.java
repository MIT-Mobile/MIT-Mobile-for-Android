package edu.mit.mitmobile2.news.view;


import java.util.ArrayList;

import edu.mit.mitmobile2.ActivityPassingCache;
import edu.mit.mitmobile2.LoadingUIHelper;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.SliderListNewModuleActivity;
import edu.mit.mitmobile2.StyledContentHTML;
import edu.mit.mitmobile2.news.NewsModule;
import edu.mit.mitmobile2.news.beans.NewsImage;
import edu.mit.mitmobile2.news.beans.NewsStory;
//import edu.mit.mitmobile2.objs.NewsItem;

import android.content.Context;
import android.content.Intent;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsImageActivity extends SliderListNewModuleActivity {

	static final String NEWS_ITEM_ID_KEY = "news_item_cache_id";
	
	private static ActivityPassingCache<NewsStory> sNewsItemCache = new ActivityPassingCache<NewsStory>();
	
	public static final String TAG = "NewsImageActivity";
	private int size_width;
	
	public static void launchActivity(Context context, NewsStory newsItem) {
		Intent i = new Intent(context, NewsImageActivity.class); 
		long id = sNewsItemCache.put(newsItem);
		i.putExtra(NEWS_ITEM_ID_KEY, id);
		context.startActivity(i);
	}
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		size_width = (int)( metrics.widthPixels/metrics.density);
		
		long newsItemCacheId = getIntent().getLongExtra(NEWS_ITEM_ID_KEY, -1);
		NewsStory newsItem = sNewsItemCache.get(newsItemCacheId);
		
		ArrayList<NewsImage> allImages = newsItem.getGalleryImages();
		for(int i=0; i < allImages.size(); i++) {
			NewsImage image = allImages.get(i);
			addScreen(new NewsImageSliderInterface(image), "" + (i+1) + " of " + allImages.size());
		}
		setPosition(getPositionValue());
	}
	
	private class NewsImageSliderInterface implements SliderInterface {
		NewsImage mImage;
		NewsImageView mView;
		
		NewsImageSliderInterface(NewsImage image) {
			mImage = image;
			mView = new NewsImageView(NewsImageActivity.this);
		}
		
		@Override
		public View getView() {	
			return mView;
		}

		@Override
		public void onSelected() { }

		@Override
		public void updateView() {
			mView.populateView(mImage);			
		}

		@Override
		public LockingScrollView getVerticalScrollView() {
			return mView;
		}

		@Override
		public void onDestroy() {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class NewsImageView extends LockingScrollView {

		public NewsImageView(Context context) {
			super(context);
			LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflator.inflate(R.layout.news_image, this);
		}
		
		public void populateView(NewsImage image) {
			final ImageView loadingView = (ImageView) findViewById(R.id.newsImageLoadingView);
			LoadingUIHelper.startLoadingImage(new Handler(), loadingView);
			
			WebView imageWV = (WebView) findViewById(R.id.newsLargeImageWV);
			imageWV.getSettings().setBuiltInZoomControls(false);
			imageWV.loadDataWithBaseURL(null, 
					StyledContentHTML.imageHtml(NewsImageActivity.this, image.getRepresentationBestFitByWidth(size_width).getUrl()),
					"text/html", "utf-8", null);
			
			// turn off loading view after picture completes loading
			imageWV.setPictureListener(new WebView.PictureListener() {
				@Override
				public void onNewPicture(WebView view, Picture picture) {
					LoadingUIHelper.stopLoadingImage(new Handler(), loadingView);
					loadingView.setVisibility(GONE);
				}
			});			
			
			
			TextView captionView = (TextView) findViewById(R.id.newsImageCaption);
			if(image.getDescription()!=null)
				captionView.setText(image.getDescription());
			else
				captionView.setVisibility(GONE);
			
			TextView creditView = (TextView) findViewById(R.id.newsImageCredit);
			if(image.getCredits()!=null)
				creditView.setText(image.getCredits());
			else
				creditView.setVisibility(GONE);
		}
	}
	
	
	
	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected NewModule getNewModule() {
		return new NewsModule();
	}

	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}
}
