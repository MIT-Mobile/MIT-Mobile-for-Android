package edu.mit.mitmobile2.facilities;

import edu.mit.mitmobile2.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.view.animation.Animation.AnimationListener;

public class FacilitiesBackgroundView extends View implements AnimationListener {

	private static float FADE_IN_FRACTION = 0.20f;
	
	private Context mContext;
	private int[] mBackgroundResourceIds = new int[] { R.drawable.tour_wallpaper_killian, R.drawable.tour_wallpaper_stata, R.drawable.tour_wallpaper_great_sail};
	private int mCurrentDrawable = 0;
	private float mInterpolatedTime = 0;
	private boolean mDrawablesLoaded = false;
	private BitmapDrawable mOutgoingDrawable;
	private BitmapDrawable mIncomingDrawable;
	private float mOutgoingScale;  // scale factor, to get the full height of the image to fit
	private float mIncomingScale;
	private float mOutgoingDeltaX; // the total pixel distance the image needs to translate by in one segment of the animation
	private float mIncomingDeltaX;
	
	private BackgroundAnimation mBackgroundAnimation;
	
	public FacilitiesBackgroundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mBackgroundAnimation = new BackgroundAnimation();
		mBackgroundAnimation.setDuration(8000);
		mBackgroundAnimation.setRepeatCount(-1);
		mBackgroundAnimation.setRepeatMode(Animation.RESTART);
		mBackgroundAnimation.setInterpolator(new LinearInterpolator());
		mBackgroundAnimation.setAnimationListener(this);
	}
	
	private void loadDrawables() {
		if(mIncomingDrawable != null) {
			mOutgoingDrawable = mIncomingDrawable;
		} else {
			mOutgoingDrawable = (BitmapDrawable) mContext.getResources().getDrawable(mBackgroundResourceIds[mCurrentDrawable]);
		}
		mIncomingDrawable = (BitmapDrawable) mContext.getResources().getDrawable(mBackgroundResourceIds[(mCurrentDrawable +1) % mBackgroundResourceIds.length]);

		// calculate scaling and translations
		// if image is too small we rescale it, otherwise we just crop it
		int outgoingHeight = mOutgoingDrawable.getIntrinsicHeight();
		int outgoingWidth = mOutgoingDrawable.getIntrinsicWidth();
		if(outgoingHeight < getHeight()) {
			mOutgoingScale = ((float) getHeight()) / ((float) outgoingHeight);
		} else {
			mOutgoingScale = 1.0f;
		}
		
		mOutgoingDeltaX =  (outgoingWidth * mOutgoingScale) - getWidth();
		
		int incomingHeight = mIncomingDrawable.getIntrinsicHeight();
		int incomingWidth = mIncomingDrawable.getIntrinsicWidth();
		if(incomingHeight < getHeight()) {
			mIncomingScale = ((float) getHeight()) / ((float) incomingHeight);
		} else {
			mIncomingScale = 1.0f;
		}
		mIncomingDeltaX =  (incomingWidth * mIncomingScale) - getWidth();
		
		mDrawablesLoaded = true;
	}
	
	public void setInterpolation(float interpolatedTime) {
		mInterpolatedTime = interpolatedTime;
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		mCurrentDrawable = (mCurrentDrawable + 1) % mBackgroundResourceIds.length;
		setInterpolation(0);
		mDrawablesLoaded = false;
	}
	
	@Override
	public void onAnimationStart(Animation animation) { } // TODO Auto-generated method stub
	
	@Override
	public void onAnimationEnd(Animation animation) { } // TODO Auto-generated method stub

	@Override
	protected void onDraw(Canvas canvas) {
		if(!mDrawablesLoaded) {
			loadDrawables();
		}
		
		// images should start at position x=-deltaX and end at position x=0
		// the images are on the screen for one full cycle plus the fade out time
		// of the next cycle, so a cycle is effectivly 1 + FADE_IN_FRACTION
		if(mInterpolatedTime < FADE_IN_FRACTION) {
			Matrix outgoingMatrix = new Matrix();
			outgoingMatrix.postScale(mOutgoingScale, mOutgoingScale);
			float outgoingTransX = -mOutgoingDeltaX + mOutgoingDeltaX * (1 + mInterpolatedTime) / (1 + FADE_IN_FRACTION);
			outgoingMatrix.postTranslate(Math.round(outgoingTransX), 0.0f);
			canvas.drawBitmap(mOutgoingDrawable.getBitmap(), outgoingMatrix, null);
			
			Matrix incomingMatrix = new Matrix();
			incomingMatrix.postScale(mIncomingScale, mIncomingScale);
			float incomingTransX = -mIncomingDeltaX + mIncomingDeltaX * (mInterpolatedTime) / (1 + FADE_IN_FRACTION);
			incomingMatrix.postTranslate(Math.round(incomingTransX), 0.0f);
			Paint translucentPaint = new Paint();
			translucentPaint.setAlpha(Math.round(255 * mInterpolatedTime / FADE_IN_FRACTION));
			canvas.drawBitmap(mIncomingDrawable.getBitmap(), incomingMatrix, translucentPaint);
		} else {
			Matrix incomingMatrix = new Matrix();
			incomingMatrix.postScale(mIncomingScale, mIncomingScale);
			float incomingTransX = -mIncomingDeltaX + mIncomingDeltaX * (mInterpolatedTime) / (1 + FADE_IN_FRACTION);
			incomingMatrix.postTranslate(Math.round(incomingTransX), 0.0f);
			canvas.drawBitmap(mIncomingDrawable.getBitmap(), incomingMatrix, null);
		}
	}
	
	private class BackgroundAnimation extends Animation {
		
		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			setInterpolation(interpolatedTime);
			invalidate();
		}
	}
	
	public void startBackgroundAnimation() {
		startAnimation(mBackgroundAnimation);
	}
}
