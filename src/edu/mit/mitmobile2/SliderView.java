package edu.mit.mitmobile2;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SliderView extends HorizontalScrollView {
	
	// this factor is used to prevent overzealously
	// going horizontally
	private static int VERTICAL_FAVOR_FACTOR = 2;
	
	protected int mWidth, mHeight;
	
	protected Context mContext;
	
	Adapter mSliderAdapter;
	
	static final int SCROLL_DURATION_PER_SCREEN = 250;
	static final int SCROLL_MAX_DURATION = 2500;
	protected boolean isAnimatingScroll = false;
	
	protected View.OnClickListener mClickListener = null;
	protected View.OnTouchListener mTouchListener = null;
	protected OnSeekListener mOnSeekListener = null;
	
	private LinearLayout mLinearLayout;
	private boolean mHasPreviousScreen;
	private boolean mHasNextScreen;
	private boolean mScrollNeedsResetting;
	
	/****************************************************/
	public SliderView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		mContext = context;
		
		mLinearLayout = new LinearLayout(context);
		mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		addView(mLinearLayout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		
		setHorizontalScrollBarEnabled(false);
		mWidth = 0;
		String layout_height = attributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");
		mHeight = AttributesParser.parseDimension(layout_height, mContext);
		
        setHorizontalFadingEdgeEnabled(false);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int newWidth = right - left;
		if (newWidth != mWidth) {
			mWidth = newWidth;
			for(int i = 0; i < mLinearLayout.getChildCount(); i++) {
				FrameLayout frame = (FrameLayout) mLinearLayout.getChildAt(i);
				frame.setLayoutParams(new LinearLayout.LayoutParams(mWidth, LayoutParams.MATCH_PARENT));
			}
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					mScrollNeedsResetting = true;
					mLinearLayout.requestLayout();
				}
			});
		}
		super.onLayout(changed, left, top, right, bottom);
		if (mScrollNeedsResetting) {
			scrollTo(mWidth, 0);
			mScrollNeedsResetting = false;
		}
	}

	public void setAdapter(Adapter sliderAdapter) {
		mSliderAdapter = sliderAdapter;
		refreshScreens();
	}
	
	// used to keep track of which page
	// was on the screen at the last down event
	private float mFingerX = -1;
	private float mFingerY = -1;
	private boolean mFingerIsStill;
	private static float FINGER_MOTION_TOLERANCE = 15.0f;
	
	private float mLastMotionX;
	
	private float mLastX;
	private float mLastY;
	
	private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_HORIZONTAL_SCROLLING = 1;
    private final static int TOUCH_STATE_VERTICAL_SCROLLING = 2;

    private int mTouchState = TOUCH_STATE_REST;
    
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if(mTouchListener != null) {
			if(mTouchListener.onTouch(this, event)) {
				return true;
			}
		}
		
		switch (action) {
		case MotionEvent.ACTION_MOVE:
			if (mTouchState == TOUCH_STATE_REST) {
				if (mTouchState != TOUCH_STATE_HORIZONTAL_SCROLLING) {
					if (VERTICAL_FAVOR_FACTOR * Math.abs(mLastY - event.getY()) > Math.abs(mLastX - event.getX())) {
						// do not intercept vertical move events
						mTouchState = TOUCH_STATE_VERTICAL_SCROLLING;
						mLastX = event.getX();
						mLastY = event.getY();
						return false;
					} 
				}
				
				if (mTouchState != TOUCH_STATE_VERTICAL_SCROLLING) {
					if (VERTICAL_FAVOR_FACTOR * Math.abs(mLastX - event.getX()) > Math.abs(mLastY - event.getY())) {
						mTouchState = TOUCH_STATE_HORIZONTAL_SCROLLING;
						mLastX = event.getX();
						mLastY = event.getY();
						return true;
					}
				}
			}
			
			break;
		case MotionEvent.ACTION_UP:
			if (mClickListener != null) {
				if(mFingerIsStill) {
					mClickListener.onClick(this);
				}
			}
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_DOWN:
			mLastX = event.getX();
			mLastY = event.getY();
			
			mLastMotionX = event.getX();
			
			mFingerX = event.getX();
			mFingerY = event.getY();
			mTouchState = TOUCH_STATE_REST;
			mFingerIsStill = true;
			break;
		}
		
		mLastX = event.getX();
		mLastY = event.getY();
		
		updateFingerIsStillStatus(event);
		return false;		
	}
	
	private void updateFingerIsStillStatus(MotionEvent event) {
		if(!mFingerIsStill) {
			return;
		}
		
		// for purist this is not a Pythagorean distance, but is
		// quicker to calculate
		float motionDistance = Math.abs(event.getX() - mFingerX) + Math.abs(event.getY() - mFingerY);
		if(motionDistance > FINGER_MOTION_TOLERANCE) {
			mFingerIsStill = false;
		}
	}
	
	/****************************************************/
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mTouchListener != null) {
			if(mTouchListener.onTouch(this, event)) {
				return true;
			}
		}
		
		int action = event.getAction();
		updateFingerIsStillStatus(event);
		if (action == MotionEvent.ACTION_MOVE) {
			// Scroll to follow the motion event
            final int deltaX = (int) (mLastMotionX - event.getX());
            mLastMotionX = event.getX();
            if (deltaX < 0) {
            	int availableToScroll = mHasPreviousScreen ? -getScrollX() : -getScrollX() + mWidth;
            	if (availableToScroll < 0) {                       	   
            		scrollBy(Math.max(availableToScroll, deltaX), 0);
            	} 
            } else if (deltaX > 0) {
            	int right = mHasNextScreen ? 3 * mWidth : 2 * mWidth;
            	final int availableToScroll = right - getScrollX() - getWidth();

            	if (availableToScroll > 0) {
            		scrollBy(Math.min(availableToScroll, deltaX), 0);
            	}
            }
            return true;
		}
		
		if (action == MotionEvent.ACTION_UP) {
			// calculate the closest position
			mTouchState = TOUCH_STATE_REST;
			snapToPosition(nearestPosition());
			
			if(mClickListener != null) {
				if(mFingerIsStill) {
					mClickListener.onClick(this);
				}
			}
			return true;
		}
		
		if (action == MotionEvent.ACTION_CANCEL) {
			mTouchState = TOUCH_STATE_REST;
		}
		
		if (action == MotionEvent.ACTION_DOWN) {
			mLastMotionX = event.getX();
		}
				
		return true;
	}

	
	@Override
	public void setOnClickListener(View.OnClickListener clickListener) {
		mClickListener = clickListener;
	}
	
	@Override
	public void setOnTouchListener(View.OnTouchListener touchListener) {
		mTouchListener = touchListener;
	}

	private ScreenPosition nearestPosition() {
		float screenFraction = (float)(getScrollX() - mWidth) / (float)(mWidth);
		if (screenFraction < -0.50) {
			return ScreenPosition.Previous;
		}
		if (screenFraction > 0.50) {
			return ScreenPosition.Next;
		}
		return ScreenPosition.Current;
	}
	
	private int scrollX(ScreenPosition screenPosition) {
		switch (screenPosition) {
			case Previous:
				return 0;
			case Current:
				return mWidth;
			case Next:
				return 2 * mWidth;
		}
		throw new RuntimeException("scroll position not found, must have received null for screen position");
	}
	
	private void snapToPosition(final ScreenPosition screenPosition) {
		
		isAnimatingScroll = true;
		
		//final int finalX  = scrollX(screenPosition);
		final int finalX = scrollX(screenPosition);
		
		final ScrollAnimation scrollAnimation = new ScrollAnimation(finalX);
		scrollAnimation.setDuration(SCROLL_DURATION_PER_SCREEN);
		scrollAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		
		scrollAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				isAnimatingScroll = false;
				scrollAnimation.mEndAnimation = true;
				seekPosition(screenPosition);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {}
			
		});

		startAnimation(scrollAnimation);
	}
	
	/*
	 * This is not a typical Animation class, instead of using the builtin matrix transformation
	 * of the android animations, this just uses the scroll properties to animate a scrolling
	 */
	protected class ScrollAnimation extends Animation {
		private int mToX;
		private int mFromX;
		public boolean mEndAnimation = false;
		
		ScrollAnimation(int toX) {
			mToX = toX;
			mFromX = getScrollX();
		}
		
		@Override
		protected void applyTransformation(float interpolation, Transformation t) {
			if (!mEndAnimation) {
				float nextXFloat = (mToX - mFromX) * interpolation + mFromX;
				int nextX = Math.round(nextXFloat);
				scrollTo(nextX, 0);
			}
		}
	}
	
	protected void seekPosition(ScreenPosition position) {

		if (position != ScreenPosition.Current) {
			mSliderAdapter.seek(position);
		}
		
		View newScreen = mSliderAdapter.getScreen(position);
		//FrameLayout wrapperView = new FrameLayout(mContext);
		
		FrameLayout previousWrapper = (FrameLayout) mLinearLayout.getChildAt(0);
		FrameLayout currentWrapper = (FrameLayout) mLinearLayout.getChildAt(1);
		FrameLayout nextWrapper = (FrameLayout) mLinearLayout.getChildAt(2);
		
		if (position == ScreenPosition.Next) {
			if (previousWrapper.getChildCount() > 0) {
				previousWrapper.removeViewAt(0);
				mSliderAdapter.destroyScreen(ScreenPosition.Previous);
			}
			
			mHasPreviousScreen = true;
			View current = currentWrapper.getChildAt(0);
			currentWrapper.removeView(current);
			previousWrapper.addView(current);
			
			View next = nextWrapper.getChildAt(0);
			nextWrapper.removeView(next);
			currentWrapper.addView(next);
			
			mHasNextScreen = (newScreen != null);
			if (mHasNextScreen) {
				nextWrapper.addView(newScreen);
			}
			
		} else if (position == ScreenPosition.Previous) {
			
			if (nextWrapper.getChildCount() > 0) {
				nextWrapper.removeViewAt(0);
				mSliderAdapter.destroyScreen(ScreenPosition.Next);
			}
			
			mHasNextScreen = true;
			View current = currentWrapper.getChildAt(0);
			currentWrapper.removeView(current);
			nextWrapper.addView(current);
			
			View previous = previousWrapper.getChildAt(0);
			previousWrapper.removeView(previous);
			currentWrapper.addView(previous);
			
			mHasPreviousScreen = (newScreen != null);
			if (mHasPreviousScreen) {
				previousWrapper.addView(newScreen);
			}
			
		} else {
			// nothing to do for seek to current screen
			return;
		}
		
		mScrollNeedsResetting = true;
		scrollTo(mWidth, 0);
		
		if (mOnSeekListener != null) {
		    mOnSeekListener.onSeek(this, mSliderAdapter);
		}
	}
	
	public void destroy() {
		clear();
		mSliderAdapter.destroy();
		mSliderAdapter = null;
	}
	
	private void addScreen(ScreenPosition screenPosition) {
		FrameLayout wrapperView = new FrameLayout(mContext);
		mLinearLayout.addView(wrapperView, new LayoutParams(mWidth, mHeight));
		if (mSliderAdapter.hasScreen(screenPosition)) {
			wrapperView.addView(mSliderAdapter.getScreen(screenPosition));
		}		
	}
	
	
	public void refreshScreens() {
		clear();
		addScreen(ScreenPosition.Previous);
		mHasPreviousScreen = mSliderAdapter.hasScreen(ScreenPosition.Previous);
		
		addScreen(ScreenPosition.Current);
		
		addScreen(ScreenPosition.Next);
		mHasNextScreen = mSliderAdapter.hasScreen(ScreenPosition.Next);
		
		mScrollNeedsResetting = true;
		scrollTo(mWidth, 0);
		
		mSliderAdapter.seek(ScreenPosition.Current);
		if (mOnSeekListener != null) {
		    mOnSeekListener.onSeek(this, mSliderAdapter);
		}
	}
	
	private void clearScreen(int childIndex, ScreenPosition screenPosition) {
		FrameLayout view = (FrameLayout) mLinearLayout.getChildAt(0);
		if (view.getChildCount() > 0) {
			mSliderAdapter.destroyScreen(screenPosition);
		}
	}
	private void clear() {
		if (mLinearLayout.getChildCount() > 0) {
			clearScreen(0, ScreenPosition.Previous);
			clearScreen(1, ScreenPosition.Current);
			clearScreen(2, ScreenPosition.Next);
			mLinearLayout.removeAllViews();
		}
	}
	
	public void slideRight() {
		if (mSliderAdapter.hasScreen(ScreenPosition.Next)) {
			snapToPosition(ScreenPosition.Next);
		}
	}
	
	public void slideLeft() {
		if (mSliderAdapter.hasScreen(ScreenPosition.Previous)) {
			snapToPosition(ScreenPosition.Previous);
		}
	}
	
	public boolean isAnimatingScroll() {
		return isAnimatingScroll;
	}
	
	public void setOnSeekListener(OnSeekListener seekListener) {
	    mOnSeekListener = seekListener;
	}
	
	public static enum ScreenPosition {
		Previous,
		Current,
		Next
	}
	
	public boolean isAtBeginning() {
	    return !mHasPreviousScreen;
	}
	
	public boolean isAtEnd() {
	    return !mHasNextScreen;
	}
	
	public interface Adapter {		
		public boolean hasScreen(ScreenPosition screenPosition);
		
		public View getScreen(ScreenPosition screenPosition);

		public void destroyScreen(ScreenPosition screenPosition);
		
		public void seek(ScreenPosition screenPosition);
		
		public void destroy();
	}
	
	public interface OnSeekListener {
	    	public void onSeek(SliderView view, Adapter adapter);
	}
}
