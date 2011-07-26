package edu.mit.mitmobile2;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class FullScreenLoader extends FrameLayout {

	private ImageView mBusyBox;
	private TextView mErrorView;
	private View mLoadingContainer;
	private boolean mIsLoading;
	
	public FullScreenLoader(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.full_screen_loader, this);
		mIsLoading = false;
		
		mBusyBox = (ImageView) findViewById(R.id.fullScreenBusyBox);
		mErrorView = (TextView) findViewById(R.id.fullScreenLoadingErrorTV);
		mLoadingContainer = findViewById(R.id.fullScreenLoadingContainer);		
	}
	
	@Override
	public void setVisibility(int visibility) {
		if (visibility == GONE || visibility == INVISIBLE) {
			LoadingUIHelper.stopLoadingImage(new Handler(), mBusyBox);
		} else if(visibility == VISIBLE) {
			if(mIsLoading) {
				LoadingUIHelper.startLoadingImage(new Handler(), mBusyBox);
			}
		}
		
		super.setVisibility(visibility);
	}
	
	public void showError() {
		LoadingUIHelper.stopLoadingImage(new Handler(), mBusyBox);
		mErrorView.setVisibility(VISIBLE);
		mLoadingContainer.setVisibility(GONE);
		mIsLoading = false;
	}
	
	public void showLoading() {
		try {
			Log.d("ZZZ","showLoading()");
			LoadingUIHelper.startLoadingImage(new Handler(), mBusyBox);
			mErrorView.setVisibility(GONE);
			mLoadingContainer.setVisibility(VISIBLE);
			mIsLoading = true;
		}
		catch (Exception e) {
			Log.d("ZZZ",e.getMessage());
		}
	}
}
