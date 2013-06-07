package edu.mit.mitmobile2.dining;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import edu.mit.mitmobile2.AttributesParser;
import edu.mit.mitmobile2.SliderView;

public class DiningSliderView extends SliderView {

	private String mMinimumDimen = "800dip"; 
	private int mMinimumWidth;
	public DiningSliderView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		mMinimumWidth = AttributesParser.parseDimension(mMinimumDimen, context);
	}

	public DiningSliderView(Context context) {
		super(context);
		mMinimumWidth = AttributesParser.parseDimension(mMinimumDimen, context);
	}

	private int mContainerWidth;
	
	@Override
	public void onMeasure(int widthSpec, int heightSpec) {
		int width = MeasureSpec.getSize(widthSpec);
		mContainerWidth = width;
		if (width >= mMinimumWidth) {
			super.onMeasure(widthSpec, heightSpec);
		} else {
			widthSpec = MeasureSpec.makeMeasureSpec(mMinimumWidth, MeasureSpec.EXACTLY);
			super.onMeasure(widthSpec, heightSpec);
		}
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
	    if (action == MotionEvent.ACTION_UP) {
	    	int scrollX = getScrollX();
			if (mLeftXforMiddle <= scrollX && scrollX <= mRightXforMiddle) {
				// override the default snap to position behavior
				if (scrollX >= (mRightXforMiddle - mContainerWidth)) {
					// user has scrolled beyond the screen, either let the user
					// go to the next screen or scroll to be right justified
					if (scrollX > mRightXforMiddle - mContainerWidth / 2) {
						snapToPosition(ScreenPosition.Next);
					} else {
						smoothScrollTo(mRightXforMiddle - mContainerWidth, 0);
					}
				}
				mTouchState = TOUCH_STATE_REST;
				return true;
			}
		}
		
		return super.onTouchEvent(event);
	}
	
}
