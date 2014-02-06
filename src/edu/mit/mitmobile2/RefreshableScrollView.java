package edu.mit.mitmobile2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class RefreshableScrollView extends ScrollView {

	private final static float MINIMUM_PULL_DISTANCE = 30.0f;
	private float mMinimumPullPixels;
	private float mInitialY = -1;
	private boolean mHandlePullToRefreshTouches = false;
	
	public RefreshableScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}
	
	public RefreshableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	
	public RefreshableScrollView(Context context) {
		super(context);
		initView(context);
	}
	
	private void initView(Context context) {
		mMinimumPullPixels = MINIMUM_PULL_DISTANCE * context.getResources().getDisplayMetrics().density;
	}
	
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
    	int action = event.getActionMasked();
    	if ((action == MotionEvent.ACTION_DOWN) || (action == MotionEvent.ACTION_MOVE)) {
    		if (getScrollY() == 0) {
    			if (mInitialY < 0) {
    				mInitialY = event.getY();
    			} else {
    				if (event.getY() - mInitialY > mMinimumPullPixels) {
    					mHandlePullToRefreshTouches = true;
    					return true;
    				}
    			}
    		}
    	}
    	
    	return super.onInterceptTouchEvent(event);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (mHandlePullToRefreshTouches) {
    		switch (event.getActionMasked()) {
    			case MotionEvent.ACTION_DOWN:
    			case MotionEvent.ACTION_MOVE:
    				return true;
    			
    			default:
    				mHandlePullToRefreshTouches = false;
    				mInitialY = -1;
    				return false;
    		}
    	} else {
    		return super.onTouchEvent(event);
    	}
    }
}
