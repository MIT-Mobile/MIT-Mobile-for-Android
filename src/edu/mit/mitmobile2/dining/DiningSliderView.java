package edu.mit.mitmobile2.dining;

import android.content.Context;
import android.util.AttributeSet;
import edu.mit.mitmobile2.AttributesParser;
import edu.mit.mitmobile2.SliderView;
import edu.mit.mitmobile2.SliderViewLayout;

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

	@Override
	protected SliderViewLayout createSliderViewLayout(Context context) {
		return new DiningSliderViewLayout(context);
	}
	
	private class DiningSliderViewLayout extends SliderViewLayout {

		public DiningSliderViewLayout(Context context) {
			super(context);
		}
	
		@Override
		protected int getMeasureChildWidth(int widthMeasureSpec) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			if (width > mMinimumWidth) {
				return width;
			} else {
				return mMinimumWidth;
			}
		}
	}	
}
