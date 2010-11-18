package edu.mit.mitmobile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class LockingScrollView extends ScrollView {

	private int mLastScrollY;
	private long mLastLockTime = -1;
	
	public LockingScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public LockingScrollView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_MOVE) {
			if(mLastScrollY != getScrollY()) {
				requestDisallowInterceptTouchEvent(true);
				mLastLockTime = System.currentTimeMillis();
			}
		}
		
		mLastScrollY = getScrollY();
		
		return super.onTouchEvent(event);
	}

	public boolean isLocked() {
		if(mLastLockTime > 0) {
			return (System.currentTimeMillis() - mLastLockTime) < 500;
		}
		
		return false;
	}
}
