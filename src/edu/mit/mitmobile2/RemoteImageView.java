package edu.mit.mitmobile2;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class RemoteImageView extends FrameLayout {	
	
	private ImageView mBusyBox;
	private ImageView mErrorImage;
	protected ImageView mContentView;
	private List<String> mUrls;
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
			setURLs(mUrls, true);
		}
	}
	
	public void setURL(String url) {
		ArrayList<String> urls = new ArrayList<String>();
		urls.add(url);
		setURLs(urls, false);
	}
	
	public void setURLs(List<String> urls) {
		setURLs(urls, false);
	}
	
	private static boolean compareURLs(List<String> urls1, List<String> urls2) {
		if (urls1 == null || urls2 == null) {
			// only way to be equal is both objects are null
			return urls1 == urls2;
		}
		
		if (urls1.size() == urls2.size()) {
			for (int i = 0; i < urls1.size(); i++) {
				if (!urls1.get(i).equals(urls2.get(i))) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	private synchronized void setURLs(final List<String> urls, boolean forceRefresh) {

		if(urls == null) {
			mContentView.setImageDrawable(null);
			mUrls = null;
			return;
		}
		
		if (!forceRefresh) {
			if (compareURLs(mUrls, urls)) {
				// nothing to update
				return;
			}
		}
		
		// clear the old contents first
		mContentView.setImageDrawable(null);
		
		mUrls = urls;
		
		mErrorImage.setVisibility(GONE);
	   	mBusyBox.setVisibility(VISIBLE);
	   	
		LoadingUIHelper.startLoadingImage(new Handler(), mBusyBox);
		final Handler uiHandler = new Handler();
		
		Thread loadImageThread = new Thread() {
			
			@Override
			public void run() {
				int width = -1;
				int height = -1;
				Bitmap image = null;		
				Canvas canvas = null;
				for (String url : urls) {
					final Bitmap layerImage;
					final byte[] imageBytes = mDiskCache.getImageBytes(url);
					if(imageBytes != null) {
						layerImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, mBitmapDecodeOptions);
					} else {
						layerImage = null;
						Log.d("RemoteImageView", "Failed to decode image: " + url);
						break;
					}
					
					if (urls.size() > 1) {
						if (image == null) {
							// dont worry about
							width = layerImage.getWidth();
							height = layerImage.getHeight();
							image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
							canvas = new Canvas(image);
							canvas.drawBitmap(layerImage, 0, 0, new Paint());
						} else {
							if (width != layerImage.getWidth() || height != layerImage.getHeight()) {
								image = null;
								Log.d("RemoteImageView", "Image Size for " + url + " does not match the size of base image " + urls.get(0));
							} else {
								canvas.drawBitmap(layerImage, 0, 0, new Paint());
							}
						}
					} else {
						// for a single image no need to do any fancy canvas 
						// drawing
						image = layerImage;
					}
				}
				

				synchronized (RemoteImageView.this) {
					if(compareURLs(mUrls, urls)) {  // check to make sure URL has not changed						
						
						final Bitmap finalImage = image;
						
						uiHandler.post(new Runnable() {
							@Override
							public void run() {
								if(compareURLs(mUrls, urls)) { // check to make sure URL has not changed
									if(finalImage != null) {
										mBusyBox.setVisibility(GONE);
										mErrorImage.setVisibility(GONE);
										updateImage(finalImage);
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
