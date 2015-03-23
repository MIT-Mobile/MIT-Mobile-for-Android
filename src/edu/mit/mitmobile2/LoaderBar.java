package edu.mit.mitmobile2;

import java.util.Date;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LoaderBar extends LinearLayout {

	Context ctx;
	ImageView mLoadingImage;
	TextView tv;
	TextView mLoadingMessageTV;
	private boolean mAnimationEnabled = false;
	private boolean mHidden = false;
	private static final int ANIMATION_OFFSET = 2000;
	private Animation mOutAnimation;
	
	private String mLoadingMessage = "Loading..";
	private String mFailedMessage = "Update failed!";
		
	private Date mDate;
	
	public LoaderBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initLoaderBar(context);
	}

	public LoaderBar(Context context) {
		super(context);
		initLoaderBar(context);
	}

	public void enableAnimation() {
		mAnimationEnabled = true;
	}
	
	private void initLoaderBar(Context context) {
		ctx = context;

		LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		vi.inflate(R.layout.loader, this);		
		
		mLoadingImage = (ImageView) findViewById(R.id.loaderLoadingImage);
		
        tv = (TextView) findViewById(R.id.loaderTV);   
        
        mLoadingMessageTV = (TextView) findViewById(R.id.loaderLoadingMessageTV);
        
		mOutAnimation = AnimationUtils.loadAnimation(ctx, R.anim.loader_slide_down);        
	}

	public void startLoading() {
	
		setAnimation(null);
		setVisibility(View.VISIBLE);
		mHidden = false;
		
		mLoadingMessageTV.setText(mLoadingMessage);
        mLoadingMessageTV.setVisibility(VISIBLE);
        tv.setVisibility(GONE);
		
		mLoadingImage.setVisibility(VISIBLE);
		LoadingUIHelper.startLoadingImage(new Handler(), mLoadingImage);
	}
	
	public void endLoading() {
		setLastLoaded(null);		
	}
	
	public void setLastLoaded(Date date) {
		if(date != null) {
			mDate = date;
		}
		
		mLoadingImage.setVisibility(GONE);
		LoadingUIHelper.stopLoadingImage(new Handler(), mLoadingImage);
		
		tv.setText("Last updated: " + mDate.toLocaleString());
        mLoadingMessageTV.setVisibility(GONE);
        tv.setVisibility(VISIBLE);
        
        if(mAnimationEnabled) {
        	mHidden = true;
        	new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if(mHidden) {
        				setAnimation(mOutAnimation);
						setVisibility(View.GONE);
					}
				}
        	}, ANIMATION_OFFSET);
        }
	}

	public void errorLoading() {
		
		mLoadingImage.setVisibility(GONE);
		LoadingUIHelper.stopLoadingImage(new Handler(), mLoadingImage);
		
		tv.setText(mFailedMessage);
        mLoadingMessageTV.setVisibility(GONE);
        tv.setVisibility(VISIBLE);
	}
	
	public void setLoadingMessage(String loadingMessage) {
		mLoadingMessage = loadingMessage;
	}
	
	public void setFailedMessage(String failedMessage) {
		mFailedMessage = failedMessage;
	}
}
