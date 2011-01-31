package edu.mit.mitmobile2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SliderView extends HorizontalScrollView {
	
	// this factor is used to prevent overzealously
	// going horizontally
	private static int VERTICAL_FAVOR_FACTOR = 2;

	private List<SliderInterface> sliderInterfaces = new ArrayList<SliderInterface>();
	
	private int mFrozenX;
	private int mFrozenY;
	
	protected int mWidth, mHeight;
	
	protected Context mContext;
	
	protected GestureDetector mFlingDetector;
	
	protected LinearLayout ll_scroll;
	
	protected int mStartPosition = 0;
	protected int mPosition = -1;
	
	static final int SCROLL_DURATION_PER_SCREEN = 250;
	static final int SCROLL_MAX_DURATION = 2500;
	protected boolean isAnimatingScroll = false;
	
	protected OnPositionChangedListener mPositionChangedListener = null;
	
	protected View.OnClickListener mClickListener = null;
	protected View.OnTouchListener mTouchListener = null;
	
	private Handler mUIHandler;
	
	private LinkedList<Integer> mPositionsToLayout = new LinkedList<Integer>();
	
	/****************************************************/
	protected int getSelectedIndex() {
		return mPosition;
	}
	
	/****************************************************/
	public SliderView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		mContext = context;
		
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.slider_view, this);
		
		setHorizontalScrollBarEnabled(false);
		
		mWidth = 0;
		String layout_height = attributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");
		mHeight = AttributesParser.parseDimension(layout_height, mContext);
		
        ll_scroll = (LinearLayout) findViewById(R.id.ll_horscr);
		
        setHorizontalFadingEdgeEnabled(false);
        setSmoothScrollingEnabled(true);
        
        
        
        // This controls consumption of events
        GestureDetector.OnGestureListener flingListener = new GestureDetector.SimpleOnGestureListener() {			
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				// only care about horizontal flings
				if((VERTICAL_FAVOR_FACTOR * Math.abs(velocityY)) > Math.abs(velocityX)) {
					// ignoring vertical flings
					return false;
				}
				
				if(velocityX > 0 ) {
					if(mPosition > 0) {
						snapToPosition(mPosition - 1);
					}  
					return true;
				}
				
				if(velocityX < 0) {
					if(mPosition < sliderInterfaces.size()-1) {
						snapToPosition(mPosition + 1);
					} 
					return true;
				}
				
				return false;
			}
		};
		
		mFlingDetector = new GestureDetector(mContext, flingListener);		
		
		// this starts the layout thread
		// we have a start/stop call which can be used by activities
		// to prevent memory leaks
		mUIHandler = new Handler();
		start();
	}
	
	
	public void setWidth(int width) {
		mWidth = width;
	}
	
	public void onWidthChanged(int width) {
		setWidth(width);
		
		for(int i = 0; i < ll_scroll.getChildCount(); i++) {
			FrameLayout frame = (FrameLayout) ll_scroll.getChildAt(i);
			frame.setLayoutParams(new LinearLayout.LayoutParams(mWidth, mHeight));
		}

		requestLayout();
		setScrollPosition(mWidth * mPosition, 0, false);
	}
	
	public void setOnPositionChangedListener(OnPositionChangedListener listener) {
		mPositionChangedListener = listener;
	}
	
	
	// used to keep track of which page
	// was on the screen at the last down event
	private float mFingerX = -1;
	private float mFingerY = -1;
	private boolean mFingerIsStill;
	private static float FINGER_MOTION_TOLERANCE = 15.0f;
	
	private float mLastX;
	private float mLastY;
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		int action = event.getAction();
		
		if(action == MotionEvent.ACTION_MOVE) {
			if(VERTICAL_FAVOR_FACTOR * Math.abs(mLastY - event.getY()) > Math.abs(mLastX - event.getX()) ) {
				// do not intercept vertical move events
				mLastX = event.getX();
				mLastY = event.getY();
				return false;
			}
		}
		mLastX = event.getX();
		mLastY = event.getY();
		
		
		if(mTouchListener != null) {
			if(mTouchListener.onTouch(this, event)) {
				return true;
			}
		}
		
		// if this action was a click we may need to send it to a listener
		if(event.getAction()==MotionEvent.ACTION_UP) {
			if(mClickListener != null) {
				if(mFingerIsStill) {
					mClickListener.onClick(this);
				}
			}
		}
		

		if(action == MotionEvent.ACTION_DOWN) {
			mFingerX = event.getX();
			mFingerY = event.getY();
			mFingerIsStill = true;
		}
		
		if(mPosition >=0 && sliderInterfaces != null) {
			LockingScrollView scrollView = sliderInterfaces.get(mPosition).getVerticalScrollView();
			if(scrollView != null) {
				if(scrollView.isLocked()) {
					return false;
				}
			}
		}
	    
		updateFingerIsStillStatus(event);
		
		return super.onInterceptTouchEvent(event);		
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
		
		if(isAnimatingScroll) {
			// to simplify things we ignore all events
			// if the scroll view is currently undergoing an animated scroll
			return true;
		}
		
		if(mFlingDetector.onTouchEvent(event)) {
			return true;
		}
		
		int action = event.getAction();
		
		updateFingerIsStillStatus(event);
		
		if (action==MotionEvent.ACTION_UP) {
			// calculate the closest position
			int approximatePosition = (getScrollX() + (mWidth/2)) / mWidth;
			snapToPosition(approximatePosition);
			
			if(mClickListener != null) {
				if(mFingerIsStill) {
					mClickListener.onClick(this);
				}
			}
			return true;
		}
				
		return super.onTouchEvent(event);
	}
	
	@Override
	public void setOnClickListener(View.OnClickListener clickListener) {
		mClickListener = clickListener;
	}
	
	@Override
	public void setOnTouchListener(View.OnTouchListener touchListener) {
		mTouchListener = touchListener;
	}
	
	protected void snapToPosition(final int position) {
		
		isAnimatingScroll = true;
		
		final int finalX = position * mWidth;
		Animation scrollAnimation = new ScrollAnimation(finalX);
		int widths = Math.abs((finalX - getScrollX())/mWidth);
		int duration = Math.max(SCROLL_DURATION_PER_SCREEN, SCROLL_DURATION_PER_SCREEN * widths);
		duration = Math.min(duration, SCROLL_MAX_DURATION);
		scrollAnimation.setDuration(duration);
		scrollAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		
		scrollAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				isAnimatingScroll = false;
				setPosition(position, true);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationStart(Animation animation) {
				scrollTo(finalX, 0);
			}
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
		
		ScrollAnimation(int toX) {
			mToX = toX;
			mFromX = getScrollX();
		}
		
		@Override
		protected void applyTransformation(float interpolation, Transformation t) {
			float nextXFloat = (mToX - mFromX) * interpolation + mFromX;
			int nextX = Math.round(nextXFloat);
			scrollTo(nextX, 0);
		}
	}
	
	
	protected void layoutPosition(int position) {
		if(sliderInterfaces == null) {
			// attempting to layout after View already destroyed
			return;
		}
		
		FrameLayout viewWrapper = (FrameLayout) ll_scroll.getChildAt(position);
		if(viewWrapper.getChildCount() == 0) {
			SliderInterface sliderInterface = sliderInterfaces.get(position);
			viewWrapper.addView(sliderInterface.getView());
			sliderInterface.updateView();
		}
	}
	
	protected void setPosition(int position){
		setPosition(position, false);
	}
	
	protected void setPosition(final int position, boolean safe) {
		int oldPosition = mPosition;		
		mPosition = position;
		
		layoutPosition(position);
		
		// Preemptively layout screens to the left and right
		// laying them out in the future prevents
		// a flicker that sometimes happens upon
		// initialization
		if(position > 0) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					layoutPosition(position-1);					
				}}, 150);
		}
		
		if(position+1 < sliderInterfaces.size()) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					layoutPosition(position+1);					
				}}, 150);
		}
		
		if(mPositionChangedListener != null) {
			if(oldPosition != position) {
				doSliderOptimizations(position, oldPosition);
				mPositionChangedListener.onPositionChanged(position, oldPosition);
			}
		}
		
		sliderInterfaces.get(position).onSelected();
		
		setScrollPosition(mWidth * mPosition, 0, safe);
	}
	
	private void setScrollPosition(final int scrollPosition, final int attemptCount, final boolean safe) {
		// a cruel hack, because on initialization
		// before any width is assigned scrollTo
		// seems to be ignored so we delay setting the scroll
		// until the width is defined
		
		if(attemptCount > 40) {
			// we limit the amount of time
			// we try to to initialize the scroll position
			// this is only to prevent any accidental memory leak/infinite loops
			return;
		}
		
		if(getWidth() == mWidth && (safe || attemptCount > 0)) {
			scrollTo(scrollPosition, 0);
			
			// sometimes scrollTo fails, in those
			// cases we use smoothScrollTo (in the cases scrollTo fails)
			// there will be an extra flicker from smoothScrollTo
			// so we dont not use the smoothScroll to if the method
			// is marked as safe, meaning we are sure the normal
			// scrollTo will succeed
			if(!safe) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						smoothScrollTo(scrollPosition, 0);
					}
				}, 50);
			}
		} else {
			new Handler().postDelayed(
				new Runnable() {
					@Override
					public void run() {
						setScrollPosition(scrollPosition, attemptCount+1, safe);
					}
				},
				50
			);
		}
	}
	

	
	protected void addScreen(SliderInterface sliderInterface) {
		final int position = sliderInterfaces.size(); // the index position the sliderInterface will have after being added
		
		sliderInterfaces.add(sliderInterface);
		ll_scroll.addView(new FrameLayout(mContext), new LayoutParams(mWidth, mHeight));
		
		// there could be many screens to layout
		// most of which are not visible so we schedule
		// them to run later in the event loop (so the application does not pause)
		// note we schedule them based on position (this is just to insure
		// that event loop does not try to lay them all out at once)
		synchronized(mPositionsToLayout) {
			mPositionsToLayout.add(position);
		}
		
	}
	
	
	public void start() {
		if(mLayoutThread == null) {
			mLayoutThread = new LayoutThread();
			mLayoutThread.start();
		}
	}
	
	public void stop() {
		if(mLayoutThread != null) {
			mLayoutThread.requestStop();
			mLayoutThread = null;
		}
	}
	
	public void destroy() {
		for(SliderInterface sliderInterface : sliderInterfaces) {
			sliderInterface.onDestroy();
		}
		sliderInterfaces = null;
		ll_scroll.removeAllViews();
	}
	
	LayoutThread mLayoutThread = null;
	
	private class LayoutThread extends Thread {
		
		boolean mStopRequested = false;
		public void requestStop() {
			mStopRequested = true;
		}
		
		@Override
		public void run() {
			while(!mStopRequested) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					mStopRequested = true;
					continue;
				}
				
				synchronized(mPositionsToLayout) {					
					if(!mPositionsToLayout.isEmpty()) {
						
						final int position = mPositionsToLayout.removeFirst();
						mUIHandler.post(new Runnable() {
							@Override
							public void run() {
								layoutPosition(position);
							}
						});
					}
				}
			};
		}
	}
	
	public void clear() {
		sliderInterfaces = new ArrayList<SliderInterface>();
		ll_scroll.removeAllViews();
		mPosition = 0;
	}
	
	public void slideRight() {
		if(mPosition+1 < sliderInterfaces.size()) {
			snapToPosition(mPosition+1);
		} 
	}
	
	public void slideLeft() {
		if(mPosition > 0) {
			snapToPosition(mPosition-1);
		} 
	}
	
	public void freezeScroll() {
		mFrozenX = getScrollX();
		mFrozenY = getScrollY();
	}
	
	public void unfreezeScroll() {
		scrollTo(mFrozenX, mFrozenY);
		
	}

	protected View getScreen(int index) {
		return sliderInterfaces.get(index).getView();
	}
	
	protected int getScreenCount() {
		return sliderInterfaces.size();
	}
	
	protected boolean isAnimatingScroll() {
		return isAnimatingScroll;
	}
	
	protected boolean isAtBeginning() {
		return (mPosition == 0);
	}
	
	protected boolean isAtEnd() {
		return (mPosition == (sliderInterfaces.size()-1));
	}

	public int getPosition() {
		return mPosition;
	}
	
	public static interface OnPositionChangedListener {
		void onPositionChanged(int newPosition, int oldPosition);
	}
	
	private void doSliderOptimizations(int newPosition, int oldPosition) {
		completelyUpdatePosition(newPosition);
		if(newPosition > 0) {
			completelyUpdatePosition(newPosition-1);
		}
		
		if(newPosition < sliderInterfaces.size()-1) {
			completelyUpdatePosition(newPosition+1);
		}
		
		// tell views to let go of large bitmaps as they are no longer needed
		for(int releasePosition = oldPosition-1; releasePosition < oldPosition+2; releasePosition++) {
			if(releasePosition > -1 && releasePosition < sliderInterfaces.size()) {
				// do not release an image close to the new Position
				if(!((releasePosition >= newPosition-1) && (releasePosition <= newPosition+1))) {
					releaseLargeMemoryChunks(releasePosition);
				}
			}
		}
	}
	
	private void completelyUpdatePosition(int position) {
		if(OptimizedSliderInterface.class.isInstance(sliderInterfaces.get(position))) {
			OptimizedSliderInterface sliderInterface = (OptimizedSliderInterface) sliderInterfaces.get(position);
			layoutPosition(position);
			sliderInterface.completelyUpdateView();
		}
	}
	
	private void releaseLargeMemoryChunks(int position) {
		if(OptimizedSliderInterface.class.isInstance(sliderInterfaces.get(position))) {
			// check if the position has already been layed out
			// do not release memory for views that have not yet been layed out
			FrameLayout viewWrapper = (FrameLayout) ll_scroll.getChildAt(position);
			if(viewWrapper.getChildCount() > 0) {
				OptimizedSliderInterface sliderInterface = (OptimizedSliderInterface) sliderInterfaces.get(position);
				sliderInterface.releaseLargeMemoryChunks();
			}
		}
	}
}
