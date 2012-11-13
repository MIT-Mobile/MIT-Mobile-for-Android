package edu.mit.mitmobile2;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MITSliderTitleBar extends RelativeLayout {

	private TextView mTitle;
	private TextView mPrevious;
	private TextView mNext;
	
	public MITSliderTitleBar(Context context) {
		this(context, null);
	}
	
	public MITSliderTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout container = (RelativeLayout) inflater.inflate(R.layout.mit_sub_slider_title_bar, this);
		
		mTitle = (TextView) container.findViewById(R.id.subSliderTitleBarTitleText);
		mPrevious = (TextView) container.findViewById(R.id.subSliderTitleBarLeftArrow);
		mNext = (TextView) container.findViewById(R.id.subSliderTitleBarRightArrow);
	}
	
	public void setPreviousNextListener(final OnPreviousNextListener listener) {
		mPrevious.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onPreviousClicked();
			}
		});
		
		mNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onNextClicked();
			}
		});
	}
	
	public void enablePreviousButton(boolean enabled) {
		mPrevious.setEnabled(enabled);
		if (enabled) {
		    mPrevious.setTextAppearance(getContext(), R.style.ToolbarTappable);
		} else {
		    mPrevious.setTextColor(getContext().getResources().getColor(R.color.ToolbarTappableDisabled));
		}
	}
	
	public void enableNextButton(boolean enabled) {
		mNext.setEnabled(enabled);
		if (enabled) {
		    mNext.setTextAppearance(getContext(), R.style.ToolbarTappable);
		} else {
		    mNext.setTextColor(getContext().getResources().getColor(R.color.ToolbarTappableDisabled));
		}
	}
	
	public void setAllTitles(String previous, String current, String next) {
		if (previous != null) {
			previous = previous.toUpperCase();
		}
		if (current != null) {
			current = current.toUpperCase();
		}
		if (next != null) {
			next = next.toUpperCase();
		}
		mPrevious.setText(previous);
		mTitle.setText(current);
		mNext.setText(next);
	}
	
	public static interface OnPreviousNextListener {
		public void onPreviousClicked();
		
		public void onNextClicked();
	}

	public void showPreviousNext() {
		// TODO Auto-generated method stub
		mPrevious.setVisibility(VISIBLE);
		mNext.setVisibility(VISIBLE);
	}	
}
