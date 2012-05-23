package edu.mit.mitmobile2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;


public class TruncatingTextView extends TextView {

	private int mPaddingTop;
	private int mPaddingBottom;
	
	private TextPaint mTextPaint;
	private StaticLayout mStaticLayout;
	
	private int mWidth;
	private int mHeight;
	
	public TruncatingTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
	
	public TruncatingTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mPaddingTop = getCompoundPaddingTop();
		mPaddingBottom = getCompoundPaddingBottom();
		initTextView(getTextColors().getDefaultColor(), getTextSize());
	}

	private void initTextView(int color, float textSize) {
		mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		
		mTextPaint.setColor(color);
		mTextPaint.setTypeface(Typeface.defaultFromStyle(0));
		mTextPaint.setTextSize(textSize);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int height = MeasureSpec.getSize(heightMeasureSpec);
		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		height -= mPaddingTop + mPaddingBottom;

		mStaticLayout = null;
		mHeight = mPaddingTop + mPaddingBottom;
		
		if (height > 0) {
			mStaticLayout = new StaticLayout(getText(), mTextPaint, mWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
			if (mStaticLayout.getHeight() > height) {
				int charsUpperLimit = getText().length();
				int charsLowerLimit = 0;
			
				while (charsUpperLimit > charsLowerLimit + 1) {
					int charsCount = (charsUpperLimit + charsLowerLimit) / 2;
					String text = getText().toString().substring(0, charsCount).trim() + "\u2026";
					mStaticLayout = new StaticLayout(text, mTextPaint, mWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false); 
					if (mStaticLayout.getHeight() > height) {
						charsUpperLimit = charsCount;
					} else {
						charsLowerLimit = charsCount;
					}
				}
				int charsThatFit = charsLowerLimit;
				if (charsThatFit > 0) {
					String text = getText().toString().substring(0, charsThatFit).trim()  + "\u2026";
					mStaticLayout = new StaticLayout(text, mTextPaint, mWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
				}
			}
			mHeight += mStaticLayout.getHeight();
		} 
		setMeasuredDimension(mWidth, mHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mStaticLayout != null) {
			canvas.save();
			if (mPaddingTop > 0) {
				canvas.translate(0, mPaddingTop);
			}
			mStaticLayout.draw(canvas);
			canvas.restore();
		}
	}
}
