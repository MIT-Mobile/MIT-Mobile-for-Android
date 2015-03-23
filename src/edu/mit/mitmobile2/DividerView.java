package edu.mit.mitmobile2;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class DividerView extends View {

	private int mDividerColor;
	public DividerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		int height = context.getResources().getDimensionPixelSize(R.dimen.dividerHeight);
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, height));
		mDividerColor = context.getResources().getColor(R.color.dividerColor);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(mDividerColor);
	}
}
