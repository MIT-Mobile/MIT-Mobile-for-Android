package edu.mit.mitmobile2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class DividerView extends View {

	public DividerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		int height = context.getResources().getDimensionPixelSize(R.dimen.dividerHeight);
		setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, height));
		setBackgroundColor(context.getResources().getColor(R.color.dividerColor));
	}

}
