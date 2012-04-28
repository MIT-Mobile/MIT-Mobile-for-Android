package edu.mit.mitmobile2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public abstract class ActionRow extends FrameLayout {	
	private OnClickListener mOnClickListener;

	// TEST
	private int layoutId;
	
	protected abstract int getLayoutId();

	// TEST
	protected void setLayoutId(int mLayoutId) {
		this.layoutId = mLayoutId;
	}
	
	private Drawable mSystemSelectionDrawable;
	private int mBackgroundResourceId = -1;
	private Integer mBackgroundColor;
	
	
	public ActionRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initHelper(context);

		mBackgroundResourceId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "background", -1);
		if(mBackgroundResourceId == -1) {
			String colorString = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "background");
			if(colorString != null) {
				mBackgroundColor = Color.parseColor(colorString);
			} else {
				mBackgroundResourceId = R.color.rowBackground;
			}
		}
	}

	public ActionRow(Context context) {
		super(context);
		initHelper(context);
		mBackgroundResourceId = R.color.rowBackground;
	}
	
	private void initHelper(Context context) {
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(getLayoutId(), this);
		
		mSystemSelectionDrawable = context.getResources().getDrawable(R.drawable.highlight_background);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(mOnClickListener == null || !isEnabled()) {
			// do not handle touches for the view
			return false;
		}
		
		if(event.getAction() == MotionEvent.ACTION_DOWN) {			
			// setting the background sometimes causes the height to
			// change, so this is hack to prevent that
			int height = getHeight();
			this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, height));
			setBackgroundDrawable(mSystemSelectionDrawable);
			setPressed(true);
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
			if (mBackgroundColor != null) {
				setBackgroundColor(mBackgroundColor);
			} else {
				setBackgroundResource(mBackgroundResourceId);
			}
			setPressed(false);
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP) {
			if (mOnClickListener != null) {
				mOnClickListener.onClick(this);
			}
		}
		
		return true;
	}
	
	@Override
	public void setOnClickListener(OnClickListener clickListener) {
		mOnClickListener = clickListener;
	}
}
