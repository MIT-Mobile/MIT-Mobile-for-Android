package edu.mit.mitmobile2.dining;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import edu.mit.mitmobile2.AttributesParser;

public class DiningColumnLinearLayout extends LinearLayout {

	private boolean mRightBorderEnabled = true;
	private boolean mLeftBorderEnabled = true;
	
	private int mDarkBorderColor;
	private int mLightBorderColor;
	private Paint mDarkPaint;
	private Paint mLightPaint;
	
	
	private int mWidth;
	private int mStrokeWidth;

	public DiningColumnLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mStrokeWidth = AttributesParser.parseDimension("2dip", context);
		mDarkBorderColor = Color.parseColor("#222222");
		mDarkPaint = new Paint();
		mDarkPaint.setColor(mDarkBorderColor);
		mDarkPaint.setStrokeWidth(mStrokeWidth);

		mLightBorderColor = Color.parseColor("#ffffff");
		mLightPaint = new Paint();
		mLightPaint.setColor(mLightBorderColor);
		mLightPaint.setStrokeWidth(mStrokeWidth);
	}

	public void setLeftBorderEnabled(boolean enabled) {
		mLeftBorderEnabled = enabled;
	}
	
	public void setRightBorderEnabled(boolean enabled) {
		mRightBorderEnabled = enabled;
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		
		mWidth = getWidth();
		
		// the first two children are special
		// title, and time summary
		float y = 0;
		
		// title view
		drawVerticalBorders(0, getChildAt(0).getHeight(), mDarkPaint, canvas);
		y = getChildAt(0).getHeight();
		
		// time/subtitle view
		drawVerticalBorders(y, y+getChildAt(1).getHeight(), mLightPaint, canvas);
		y += getChildAt(1).getHeight();
		
		// the remaining view
		drawVerticalBorders(y, getHeight(), mDarkPaint, canvas);
		
		for (int i = 2; i < getChildCount(); i++) {
			View child = getChildAt(i);
			y += child.getHeight();
			if (child.getVisibility() == View.VISIBLE && y < getHeight()) {
				drawHorizontalBorder(y, mDarkPaint, canvas);
			}
		}
	}

	private void drawVerticalBorders(float y1, float y2, Paint borderPaint, Canvas canvas) {
		if (mLeftBorderEnabled) {
			canvas.drawLine(0, y1, 0, y2, borderPaint);
		}
		if (mRightBorderEnabled) {
			canvas.drawLine(mWidth, y1, mWidth, y2, borderPaint);
		}
	}

	private void drawHorizontalBorder(float y, Paint borderPaint, Canvas canvas) {
		canvas.drawLine(0, y, mWidth, y, borderPaint);
	}
}
