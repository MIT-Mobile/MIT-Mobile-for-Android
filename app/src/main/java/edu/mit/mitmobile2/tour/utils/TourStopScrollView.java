package edu.mit.mitmobile2.tour.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class TourStopScrollView extends ScrollView {
    private TourStopScrollViewListener scrollViewListener = null;

    public TourStopScrollView(Context context) {
        super(context);
    }

    public TourStopScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TourStopScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(TourStopScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if(scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
}
