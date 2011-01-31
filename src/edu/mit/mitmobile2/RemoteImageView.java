package edu.mit.mitmobile2;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class RemoteImageView extends FrameLayout {	
	
	private ImageView mBusyBox;
	private ImageView mErrorImage;
	protected ImageView mContentView;
	private String mUrl;
	private ImageDiskCache mDiskCache;
	
	private BitmapFactory.Options mBitmapDecodeOptions;
	
	public RemoteImageView(Context context, AttributeSet attrs) {
		
		super(context, attrs);
		
		inflateLayout(context);
		
		int scaleTypeInt= attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "scaleType", -1);		
		
		mBusyBox = (ImageView) findViewById(R.id.remoteImageViewLoader);
		mErrorImage = (ImageView) findViewById(R.id.remoteImageViewError);
		
		mContentView = (ImageView) findViewById(R.id.remoteImageViewContent);
		if(scaleTypeInt >= 0) {
			setScaleType(ScaleType.values()[scaleTypeInt]);
		}	
		
		mDiskCache = new ImageDiskCache(context);
	}

	void inflateLayout(Context context) {
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.remote_imageview, this);
	}
	
	public void updateImage(Bitmap image) {
		mContentView.setImageBitmap(image);
	}
	
	public void setScaleType(ScaleType scaleType) {
		mContentView.setScaleType(scaleType);
	}
	
	public synchronized void setBitmapDecodeOptions(BitmapFactory.Options decodeOptions) {
		mBitmapDecodeOptions = decodeOptions;
	}
	
	public void refresh() {
		if(mErrorImage.getVisibility()  == VISIBLE) {
			setURL(mUrl, true);
		}
	}
	
	public void setURL(String url) {
		setURL(url, false);
	}
	
	private synchronized void setURL(final String url, boolean forceRefresh) {

		if(url == null) {
			mContentView.setImageDrawable(null);
			mUrl = null;
			return;
		}
		
		if(url.equals(mUrl) && !forceRefresh) {
			// nothing to update
			return;
		}
		
		// clear the old contents first
		mContentView.setImageDrawable(null);
		
		mUrl = url;
		
		mErrorImage.setVisibility(GONE);
	   	mBusyBox.setVisibility(VISIBLE);
	   	
		LoadingUIHelper.startLoadingImage(new Handler(), mBusyBox);
		final Handler uiHandler = new Handler();
		
		Thread loadImageThread = new Thread() {
			
			@Override
			public void run() {
				final byte[] imageBytes = mDiskCache.getImageBytes(url);

				synchronized (RemoteImageView.this) {
					if(url.equals(mUrl)) {  // check to make sure URL has not changed
						final Bitmap image;
						if(imageBytes != null) {
							image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, mBitmapDecodeOptions);
						} else {
							image = null;
						}
						
						
						uiHandler.post(new Runnable() {
							@Override
							public void run() {
								if(url.equals(mUrl)) { // check to make sure URL has not changed
									if(image != null) {
										mBusyBox.setVisibility(GONE);
										mErrorImage.setVisibility(GONE);
										updateImage(image);
										mContentView.setVisibility(VISIBLE);					
									} else {
										mBusyBox.setVisibility(GONE);
										mContentView.setVisibility(GONE);
										mErrorImage.setVisibility(VISIBLE);
									}
								}
							}
						}); 
					}
				}
			}
		};
		
		loadImageThread.setPriority(Thread.MIN_PRIORITY);
		loadImageThread.start();
   }
}
