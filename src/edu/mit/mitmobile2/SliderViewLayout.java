package edu.mit.mitmobile2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class SliderViewLayout extends ViewGroup {
	View mLeft;
	View mMiddle;
	View mRight;
	
	private int mChildWidth;
	private int mDividerWidth;
	
	public SliderViewLayout(Context context) {
		super(context);
		mDividerWidth = AttributesParser.parseDimension("8dip", context);
	}

	public int getChildWidth() {
		return mChildWidth;
	}
	
	public int getLeftXforMiddle() {
		return mChildWidth + mDividerWidth;
	}
	
	public int getRightXforMiddle() {
		return 2 * mChildWidth + mDividerWidth;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = 0;
		int childWidthSpec = 0;
		mChildWidth = MeasureSpec.getSize(widthMeasureSpec);
		width = 3 * MeasureSpec.getSize(widthMeasureSpec) + 2 * mDividerWidth;			
		childWidthSpec = MeasureSpec.makeMeasureSpec(mChildWidth, MeasureSpec.EXACTLY);
		
		if (mLeft != null) {
			mLeft.measure(childWidthSpec, heightMeasureSpec);
		}
		if (mMiddle != null) {
			mMiddle.measure(childWidthSpec, heightMeasureSpec);
		}
		if (mRight != null) {
			mRight.measure(childWidthSpec, heightMeasureSpec);
		}		
		
		setMeasuredDimension(width, MeasureSpec.getSize(heightMeasureSpec));
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int height = bottom - top;
		if (mLeft != null) {
			mLeft.layout(0, 0, mChildWidth, height);
		}
		if (mMiddle != null) {
			mMiddle.layout(mChildWidth+mDividerWidth, 0, 2*mChildWidth+mDividerWidth, height);
		}
		if (mRight != null) {
			mRight.layout(2*mChildWidth+2*mDividerWidth, 0, 3*mChildWidth+2*mDividerWidth, height);
		}
	}
	
	
	/*
	 *  these methods add views to the left or right and push off
	 *  a view which no longer fits and returns it.
	 */
	protected View addViewToLeft(View view) {
		View removedView = null;
		if (mMiddle == null) {
			mMiddle = view;
		} else if (mLeft == null) {
			mLeft = view;
		} else {
			if (mRight != null) {
				removeView(mRight);
				removedView = mRight;
			}
			mRight = mMiddle;
			mMiddle = mLeft;
			mLeft = view;
		}
		if (view != null) {
			addView(view);
		}
		return removedView;
	}
	
	protected View addViewToRight(View view) {
		View removedView = null;
		if (mMiddle == null) {
			mMiddle = view;
		} else if (mRight == null) {
			mRight = view;
		} else {
			if (mLeft != null) {
				removeView(mLeft);
				removedView = mLeft;
			}
			mLeft = mMiddle;
			mMiddle = mRight;
			mRight = view;
		}
		if (view != null) {
			addView(view);
		}
		return removedView;
	}
	
	protected void clear() {
		mLeft = null;
		mMiddle = null;
		mRight = null;
		removeAllViews();
	}
}
