package edu.mit.mitmobile2.news;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import edu.mit.mitmobile2.AttributesParser;
import edu.mit.mitmobile2.LoadingUIHelper;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.objs.NewsItem;

public class NewsHomeItem implements SliderInterface {
	
	private Context mContext;
	private NewsModel mNewsModel;
	private NewsItem mNewsItem;
	private View mView;
	private TextView mTitleView;
	private TextView mSubtitleView;
	private ImageView mThumbnailView;
	private String[] mWords;
	private String mCurrentSubtitle;
	private int mTitleSubtitleLimit;
	private int mWordIndex;
	private Handler mLayoutHandler;
	private View mTeaserBox;

	public NewsHomeItem(NewsItem newsItem, NewsModel newsModel, Context context) {
		mNewsItem = newsItem;
		mContext = context;
		mNewsModel = newsModel;
	}

	@Override
	public View getView() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = inflater.inflate(R.layout.news_home_item, null);
		
		mTitleView = (TextView) mView.findViewById(R.id.newsSummaryTitleTV);
		mSubtitleView = (TextView) mView.findViewById(R.id.newsSummarySubtitleTV);
		mThumbnailView = (ImageView) mView.findViewById(R.id.newsSummaryIV);
		mTeaserBox = mView.findViewById(R.id.newHomeTeaserBox);
		
		return mView;
	}

	@Override
	public void onSelected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateView() {
		mTitleView.setText(mNewsItem.title);
		
		if(mNewsItem.thumbURL != null) {
			mThumbnailView.setImageResource(R.drawable.busybox);
			mThumbnailView.setScaleType(ScaleType.CENTER);
			LoadingUIHelper.startLoadingImage(new Handler(), mThumbnailView);
			mNewsModel.fetchThumbnail(mThumbnailHandler, mNewsItem, true);
		} else {
			mThumbnailView.setImageResource(R.drawable.news_placeholder);
		}
		
		
		// iteratively add words until we are over the limit
		mWords = mNewsItem.description.split("\\s");
		mWordIndex = 0;
		mCurrentSubtitle = "";
		mTitleSubtitleLimit = AttributesParser.parseDimension("122dip", mContext);		
		mLayoutHandler = new Handler();
		
		layoutWords();
	}
	
	private void layoutWords() {
		if(mWordIndex < mWords.length) {
			mSubtitleView.setText(mCurrentSubtitle + mWords[mWordIndex] + " ");
		
			mLayoutHandler.post(new Runnable() {
				@Override
				public void run() {
					int totalHeight = mSubtitleView.getHeight()+ mTitleView.getHeight();
					if(totalHeight == 0) {
						// not really layed out yet try again
						layoutWords();
						return;
					}
					
					if(totalHeight > mTitleSubtitleLimit) {
						mSubtitleView.setText(mCurrentSubtitle);
						Log.d("mem", mCurrentSubtitle);
						mTeaserBox.setVisibility(View.VISIBLE);
					} else {
						mCurrentSubtitle = mCurrentSubtitle + mWords[mWordIndex] + " ";
						mWordIndex++;
						layoutWords();
					}
				}
			});
		} else {
			mSubtitleView.setText(mCurrentSubtitle);
			mTeaserBox.setVisibility(View.VISIBLE);
		}
	}
	
	final Handler mThumbnailHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bitmap thumbBitmap = (Bitmap) msg.obj;
			mThumbnailView.setScaleType(ScaleType.FIT_XY);
			mThumbnailView.setImageBitmap(thumbBitmap);
		}
	};

	@Override
	public LockingScrollView getVerticalScrollView() {
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}
}
