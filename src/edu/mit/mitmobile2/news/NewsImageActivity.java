package edu.mit.mitmobile2.news;

import java.util.List;

import edu.mit.mitmobile2.ActivityPassingCache;
import edu.mit.mitmobile2.LoadingUIHelper;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.SliderNewModuleActivity;
import edu.mit.mitmobile2.StyledContentHTML;
import edu.mit.mitmobile2.objs.NewsItem;
import edu.mit.mitmobile2.objs.NewsItem.Image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsImageActivity extends SliderNewModuleActivity {

	static final String NEWS_ITEM_ID_KEY = "news_item_cache_id";
	
	private static ActivityPassingCache<NewsItem> sNewsItemCache = new ActivityPassingCache<NewsItem>();
	
	public static final String TAG = "NewsImageActivity";
	
	public static void launchActivity(Context context, NewsItem newsItem) {
		Intent i = new Intent(context, NewsImageActivity.class); 
		long id = sNewsItemCache.put(newsItem);
		i.putExtra(NEWS_ITEM_ID_KEY, id);
		context.startActivity(i);
	}
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		long newsItemCacheId = getIntent().getLongExtra(NEWS_ITEM_ID_KEY, -1);
		NewsItem newsItem = sNewsItemCache.get(newsItemCacheId);
		List<Image> allImages = newsItem.getAllImages();
		for(int i=0; i < allImages.size(); i++) {
			NewsItem.Image image = allImages.get(i);
			addScreen(new NewsImageSliderInterface(image), image.imageCaption, "" + (i+1) + " of " + allImages.size());
		}
		setPosition(getPositionValue());
	}
	
	private class NewsImageSliderInterface implements SliderInterface {
		NewsItem.Image mImage;
		NewsImageView mView;
		
		NewsImageSliderInterface(NewsItem.Image image) {
			mImage = image;
		}
		
		@Override
		public View getView() {
			mView = new NewsImageView(NewsImageActivity.this);
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
		
		public void populateView(NewsItem.Image image) {
			final ImageView loadingView = (ImageView) findViewById(R.id.newsImageLoadingView);
			LoadingUIHelper.startLoadingImage(new Handler(), loadingView);
			
			WebView imageWV = (WebView) findViewById(R.id.newsLargeImageWV);
			imageWV.getSettings().setBuiltInZoomControls(false);
			imageWV.loadDataWithBaseURL(null, StyledContentHTML.imageHtml(NewsImageActivity.this, image.fullURL), "text/html", "utf-8", null);
			
			// turn off loading view after picture completes loading
			imageWV.setPictureListener(new WebView.PictureListener() {
				@Override
				public void onNewPicture(WebView view, Picture picture) {
					LoadingUIHelper.stopLoadingImage(new Handler(), loadingView);
					loadingView.setVisibility(GONE);
				}
			});			
			
			
			TextView captionView = (TextView) findViewById(R.id.newsImageCaption);
			captionView.setText(image.imageCaption);
			if(image.imageCaption.equals("")) {
				captionView.setVisibility(GONE);
			}
			
			TextView creditView = (TextView) findViewById(R.id.newsImageCredit);
			creditView.setText(image.imageCredits);
			if(image.imageCredits.equals("")) {
				creditView.setVisibility(GONE);
			}
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
	protected boolean isScrollable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}
}
