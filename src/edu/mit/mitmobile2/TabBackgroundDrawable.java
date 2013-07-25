package edu.mit.mitmobile2;

import android.R.attr;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;

public class TabBackgroundDrawable extends StateListDrawable {

	private int mBackgroundColor;
	private int mUnderlineColor;
	private int mSelectedUnderlineHeight;
	private int mUnselectedUnderlineHeight;

	public TabBackgroundDrawable(Context context, int underlineColor) {
		TabSelectedBackground selected = new TabSelectedBackground();
//		TabUnselectedBackground unselectedPressed = new TabUnselectedBackground();
		TabUnselectedBackground unselected = new TabUnselectedBackground();
		

		addState(new int[] {attr.state_selected}, selected);
		addState(new int[] {attr.state_pressed}, new LayerDrawable(new Drawable[] {getHighlightDrawable(context)/*, unselectedPressed*/}));
		addState(new int[] {}, unselected);		
		
		
		mUnderlineColor = underlineColor;
		mUnselectedUnderlineHeight = AttributesParser.parseDimension("1dip", context);
		mSelectedUnderlineHeight = AttributesParser.parseDimension("5dip", context);
		
	}
	

	private static Drawable getHighlightDrawable(Context context) {
		return context.getResources().getDrawable(R.drawable.highlight_background);
	}
	
	private class TabSelectedBackground extends Drawable {
		@Override
		public void draw(Canvas canvas) {
			Rect bounds = getBounds();
			Paint paint = new Paint();
			paint.setColor(mUnderlineColor);
			RectF rect = new RectF(bounds);
			rect.top = bounds.bottom - mSelectedUnderlineHeight;
			canvas.drawRect(rect, paint);		
		}

		@Override
		public int getOpacity() {
			return PixelFormat.TRANSPARENT;
		}

		@Override
		public void setAlpha(int alpha) { }

		@Override
		public void setColorFilter(ColorFilter cf) { }
	}
	
	private class TabUnselectedBackground extends Drawable {
		@Override
		public void draw(Canvas canvas) {
			Rect bounds = getBounds();
			Paint paint = new Paint();
			paint.setColor(mUnderlineColor);
			RectF rect = new RectF(bounds);
			rect.top = bounds.bottom - mUnselectedUnderlineHeight;
			canvas.drawRect(rect, paint);	
		}

		@Override
		public int getOpacity() {
			return PixelFormat.TRANSPARENT;
		}

		@Override
		public void setAlpha(int alpha) { }

		@Override
		public void setColorFilter(ColorFilter cf) {}
		
		@Override
		public boolean isStateful() {
			return false;
		}
		
	}	

}
