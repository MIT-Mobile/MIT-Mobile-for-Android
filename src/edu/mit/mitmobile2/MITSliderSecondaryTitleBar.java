package edu.mit.mitmobile2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MITSliderSecondaryTitleBar extends RelativeLayout {

	private TextView mTitle;
	private TextView mPrevious;
	private TextView mNext;
	
	public MITSliderSecondaryTitleBar(Context context) {
		this(context, null);
	}
	
	public MITSliderSecondaryTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout container = (RelativeLayout) inflater.inflate(R.layout.mit_sub_slider_title_bar, this);
		
		mTitle = (TextView) container.findViewById(R.id.subSliderTitleBarTitleText);
		mPrevious = (TextView) container.findViewById(R.id.subSliderTitleBarLeftArrow);
		mNext = (TextView) container.findViewById(R.id.subSliderTitleBarRightArrow);
	}
	
	public void setSlideListener(final OnSlideToListener listener) {
		mPrevious.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listener.onSlideToPrevious();
			}
		});
		
		mNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				listener.onSlideToNext();
			}
		});
	}
	
	public void enablePreviousButton(boolean enabled) {
		mPrevious.setEnabled(enabled);
	}
	
	public void enableNextButton(boolean enabled) {
		mNext.setEnabled(enabled);
	}
	
	public void setTitle(String title) {
		mTitle.setText(title);
	}
	
	public static interface OnSlideToListener {
		public void onSlideToPrevious();
		
		public void onSlideToNext();
	}

	public void showPreviousNext() {
		// TODO Auto-generated method stub
		mPrevious.setVisibility(VISIBLE);
		mNext.setVisibility(VISIBLE);
	}	
}
