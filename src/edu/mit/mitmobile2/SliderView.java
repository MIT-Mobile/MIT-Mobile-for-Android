package edu.mit.mitmobile2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.HorizontalScrollView;

public class SliderView extends HorizontalScrollView {
	
	// this factor is used to prevent overzealously
	// going horizontally
	private static int VERTICAL_FAVOR_FACTOR = 2;
	
	protected int mLeftXforMiddle;
	protected int mRightXforMiddle;
	protected int mHeight;
	
	protected Context mContext;
	
	Adapter mSliderAdapter;
	
	static final int SCROLL_DURATION_PER_SCREEN = 250;
	static final int SCROLL_MAX_DURATION = 2500;
	protected boolean isAnimatingScroll = false;
	
	protected View.OnClickListener mClickListener = null;
	protected View.OnTouchListener mTouchListener = null;
	protected OnSeekListener mOnSeekListener = null;
	
	private SliderViewLayout mSliderViewLayout;
	private boolean mHasPreviousScreen;
	private boolean mHasNextScreen;
	private boolean mScrollNeedsResetting;
	
	// keep track of which edge screen want to align
	// true for left edge, false for right edge
	private boolean mAlignLeftEdge = true;
	
	// prevent accidentally starting a horizontal
	// scroll when tapping on bottoms
	private int mStaticFrictionThreshold;
	
	/****************************************************/
	
	public SliderView(Context context) {
		super(context);
		initSliderView(context);
		mHeight = LayoutParams.MATCH_PARENT;
	}
	
	public SliderView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		initSliderView(context);
		String layout_height = attributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");
		mHeight = AttributesParser.parseDimension(layout_height, mContext);
	}

	protected SliderViewLayout createSliderViewLayout(Context context) {
		return new SliderViewLayout(context);
	}
	
	private void initSliderView(Context context) {
		mContext = context;
		
		mSliderViewLayout = createSliderViewLayout(context);
		addView(mSliderViewLayout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
		
		setHorizontalScrollBarEnabled(false);
		
		mLeftXforMiddle = mSliderViewLayout.getLeftXforMiddle();
		
        setHorizontalFadingEdgeEnabled(false);
        
        mStaticFrictionThreshold = AttributesParser.parseDimension("4dip", mContext);	
        
        setFillViewport(true);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mScrollNeedsResetting) {
			resetScroll();
			mScrollNeedsResetting = false;
		}
	}
	
	@Override
	protected void onSizeChanged (int w, int h, int oldw, int oldh) {
		mLeftXforMiddle = mSliderViewLayout.getLeftXforMiddle();
		mRightXforMiddle = mSliderViewLayout.getRightXforMiddle();
		resetScroll();
		mScrollNeedsResetting = true;
		requestLayout();
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
	
    protected final static int TOUCH_STATE_REST = 0;
    protected final static int TOUCH_STATE_HORIZONTAL_SCROLLING = 1;
    protected final static int TOUCH_STATE_VERTICAL_SCROLLING = 2;

    protected int mTouchState = TOUCH_STATE_REST;
    
	
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
			if (Math.abs(event.getX() - mLastX) < mStaticFrictionThreshold) {
				// do not process move events
				// until the finger has moved a minimum distance
				return false;
			}
			
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
            	int availableToScroll = mHasPreviousScreen ? -getScrollX() : -getScrollX() + mLeftXforMiddle;
            	if (availableToScroll < 0) {                       	   
            		scrollBy(Math.max(availableToScroll, deltaX), 0);
            	} 
            } else if (deltaX > 0) {
            	int right = mHasNextScreen ? mSliderViewLayout.getWidth() : mRightXforMiddle;
            	final int availableToScroll = (right - mLeftXforMiddle) - (getScrollX() - mLeftXforMiddle) - getWidth();

            	if (availableToScroll > 0) {
            		scrollBy(Math.min(availableToScroll, deltaX), 0);
            	}
            }
            return true;
		}
		
		if (action == MotionEvent.ACTION_UP) {
			// calculate the closest position
			mTouchState = TOUCH_STATE_REST;
			
			if (shouldAlwaysSnapToPosition()) {
				snapToPosition(nearestPosition());
			} else {
				int scrollX = getScrollX();
				if (mLeftXforMiddle <= scrollX && scrollX <= mRightXforMiddle) {
					int childWidth = mSliderViewLayout.getChildWidth();
					if (scrollX >= (mRightXforMiddle - getWidth())) {
						// user has scrolled beyond the screen, either let the user
						// go to the next screen or scroll to be right justified
						if (scrollX > mRightXforMiddle - childWidth/2) {
							snapToPosition(ScreenPosition.Next);
						} else {
							smoothScrollTo(mRightXforMiddle - getWidth(), 0);
						}
					}
				} else {
					snapToPosition(nearestPosition());
				}
			}
			
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

	protected boolean shouldAlwaysSnapToPosition() {
		return (getWidth() >= mSliderViewLayout.getChildWidth());
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
		float screenFraction = (float)(getScrollX() - mLeftXforMiddle) / (float)(mSliderViewLayout.getChildWidth());
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
				return mSliderViewLayout.getChildWidth() - getWidth();
			case Current:
				return mLeftXforMiddle;
			case Next:
				return mSliderViewLayout.getLeftXforRight();
		}
		throw new RuntimeException("scroll position not found, must have received null for screen position");
	}
	
	protected void snapToPosition(final ScreenPosition screenPosition) {
		
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
		
		View newScreen = null;
		if (mSliderAdapter.hasScreen(position)) {
		    newScreen = mSliderAdapter.getScreen(position);
		}
		
		if (position == ScreenPosition.Next) {
			mHasPreviousScreen = true;
			mHasNextScreen = (newScreen != null);
			
			if (mSliderViewLayout.addViewToRight(newScreen) != null) {
				mSliderAdapter.destroyScreen(ScreenPosition.Previous);
			}
			
		} else if (position == ScreenPosition.Previous) {
			mHasNextScreen = true;
			mHasPreviousScreen = (newScreen != null);

			if (mSliderViewLayout.addViewToLeft(newScreen) != null) {
				mSliderAdapter.destroyScreen(ScreenPosition.Next);
			}
			
		} else {
			// nothing to do for seek to current screen
			return;
		}
		
		mScrollNeedsResetting = true;
		mAlignLeftEdge = (position != ScreenPosition.Previous);
		
		if (mOnSeekListener != null) {
		    mOnSeekListener.onSeek(this, mSliderAdapter);
		}
	}
	
	private void resetScroll() {
		if (mAlignLeftEdge) {
			scrollTo(mLeftXforMiddle, 0);
		} else {
			scrollTo(mRightXforMiddle - getWidth(), 0);
		}
	}
	
	public void destroy() {
		mSliderViewLayout.clear();
		mSliderAdapter.destroy();
		mSliderAdapter = null;
	}
	
	
	public void refreshScreens() {
		/*
		 * really should change how SliderAdapter is notified about views being removed.
		 */
		mSliderViewLayout.clear();
		View currentView = mSliderAdapter.getScreen(ScreenPosition.Current);
		mSliderViewLayout.addViewToRight(currentView);
		
		mHasNextScreen = false;
		if (mSliderAdapter.hasScreen(ScreenPosition.Next)) {
			mHasNextScreen = true;
			View nextView = mSliderAdapter.getScreen(ScreenPosition.Next);
			mSliderViewLayout.addViewToRight(nextView);
		} 
		
		mHasPreviousScreen = false;
		if (mSliderAdapter.hasScreen(ScreenPosition.Previous)) {
			mHasPreviousScreen = true;
			View previousView = mSliderAdapter.getScreen(ScreenPosition.Previous);
			mSliderViewLayout.addViewToLeft(previousView);
		}
		
		mScrollNeedsResetting = true;

		resetScroll();
		
		mSliderAdapter.seek(ScreenPosition.Current);
		if (mOnSeekListener != null) {
		    mOnSeekListener.onSeek(this, mSliderAdapter);
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
