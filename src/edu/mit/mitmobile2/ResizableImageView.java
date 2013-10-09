package edu.mit.mitmobile2;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class ResizableImageView extends RemoteImageView {

	public ResizableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public interface OnSizeChangedListener {
		public void onSizeChanged(int w, int h, int oldw, int oldh);
	}

	private OnSizeChangedListener mListener;
	
	public void setOnSizeChangedListener(OnSizeChangedListener listener) {
		mListener = listener;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if(mListener != null) {
			mListener.onSizeChanged(w, h, oldw, oldh);
		}
	}
	
	public void notifyOnSizeChangedListener() {
		onSizeChanged(getWidth(), getHeight(), getWidth(), getHeight());
	}
	
	public static interface Overlay {
		public void draw(Canvas canvas);
	}
	Overlay mOverlay;
	
	public void setOverlay(Overlay overlay) {
		mOverlay = overlay;
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		
		if(mOverlay != null && mContentView.getVisibility() == VISIBLE) {
			mOverlay.draw(canvas);
		}
	}
}
