package edu.mit.mitmobile2.dining;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import edu.mit.mitmobile2.AttributesParser;

public class DiningDividerLinearLayout extends LinearLayout {
	
	private Integer mDividerColor;
	private boolean mDrawDivider = true;
	
	private int mStrokeWidth;

	public DiningDividerLinearLayout(Context context) {
		super(context);
		init(context);
	}
	
	public DiningDividerLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public boolean getDrawHorizontalDivider() {
		return mDrawDivider;
	}

	public void setDrawHorizontalDivider(boolean mDrawDivider) {
		this.mDrawDivider = mDrawDivider;
	}

	private void init(Context context) {
		mStrokeWidth = AttributesParser.parseDimension("1dip", context);
	}
	
	public void setDividerColor(int dividerColor){
		mDividerColor = dividerColor;
	}
	

	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		if (mDividerColor == null) {
			return;
		}
		
		Paint dividerPaint = new Paint();
		dividerPaint.setStrokeWidth(mStrokeWidth);
		dividerPaint.setColor(mDividerColor);
		
		if (getOrientation() == HORIZONTAL) {
			int x = 0;
			int height = getHeight();
			for (int i = 0; i < getChildCount(); i++) {

				View child = getChildAt(i);
				x += child.getWidth();
				if (child.getVisibility() == View.VISIBLE && x < getWidth()) {
					canvas.drawLine(x, 0, x, height, dividerPaint);
				}
			}			
		} else if (getOrientation() == VERTICAL) {
			int y = 0;
			int width = getWidth();
			for (int i = 0; i < getChildCount(); i++) {

				View child = getChildAt(i);
				y += child.getHeight();
				if (child.getVisibility() == View.VISIBLE && y < getHeight() && mDrawDivider) {
					canvas.drawLine(0, y, width, y, dividerPaint);
				}
			}			
		}
	}
}
