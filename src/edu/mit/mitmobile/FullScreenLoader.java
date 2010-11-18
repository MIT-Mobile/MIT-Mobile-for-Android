package edu.mit.mitmobile;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class FullScreenLoader extends FrameLayout {

	private ImageView mBusyBox;
	private TextView mErrorView;
	private View mLoadingContainer;
	
	public FullScreenLoader(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.full_screen_loader, this);
		
		mBusyBox = (ImageView) findViewById(R.id.fullScreenBusyBox);
		mErrorView = (TextView) findViewById(R.id.fullScreenLoadingErrorTV);
		mLoadingContainer = findViewById(R.id.fullScreenLoadingContainer);
		
		if (getVisibility() == VISIBLE) {
			LoadingUIHelper.startLoadingImage(new Handler(), mBusyBox);
		}
		
	}
	
	@Override
	public void setVisibility(int visibility) {
		if (visibility == GONE || visibility == INVISIBLE) {
			LoadingUIHelper.stopLoadingImage(new Handler(), mBusyBox);
		} else if(visibility == VISIBLE) {
			LoadingUIHelper.startLoadingImage(new Handler(), mBusyBox);
		}
		
		super.setVisibility(visibility);
	}
	
	public void showError() {
		mErrorView.setVisibility(VISIBLE);
		mLoadingContainer.setVisibility(GONE);
	}
	
	public void showLoading() {
		mErrorView.setVisibility(GONE);
		mLoadingContainer.setVisibility(VISIBLE);
	}
}
