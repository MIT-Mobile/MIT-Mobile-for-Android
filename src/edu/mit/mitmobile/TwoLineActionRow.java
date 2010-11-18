package edu.mit.mitmobile;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

public class TwoLineActionRow extends ActionRow {
	private TextView mTitleView;
	private TextView mSubtitleView;
	private ImageView mActionIconView;
	
	public TwoLineActionRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mTitleView = (TextView) findViewById(R.id.simpleRowTitle);
		mSubtitleView = (TextView) findViewById(R.id.simpleRowSubtitle);
		mActionIconView = (ImageView) findViewById(R.id.simpleRowActionIcon);	
		
		if(attrs != null) {
			String initialText = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
			setTitle(initialText);
			
			int actionResId = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "src", -1);
			if(actionResId > 0) {
				setActionIconResource(actionResId);
			}
		}
	}
	
	public void setTitle(String title, int color) {
		setTitle(title, TextView.BufferType.NORMAL);
		mTitleView.setTextColor(color);
	}
	
	public void setTitle(String title) {
		setTitle(title, TextView.BufferType.NORMAL);
	}
	
	public void setTitle(String title, TextView.BufferType bufferType) {
		if(title != null) {
			mTitleView.setText(title, bufferType);
			mTitleView.setVisibility(VISIBLE);
		} else {
			mTitleView.setVisibility(GONE);
		}
	}

	public CharSequence getTitle() {
		return mTitleView.getText();
	}
	
	public void setSubtitle(String subtitle) {
		if(subtitle != null) {
			mSubtitleView.setText(subtitle);
			mSubtitleView.setVisibility(VISIBLE);
		} else {
			mSubtitleView.setVisibility(GONE);
		}
	}
	
	public void setActionIconResource(int resId) {
		mActionIconView.setImageResource(resId);
		mActionIconView.setVisibility(VISIBLE);
	}
	
	public void removeActionIcon() {
		mActionIconView.setVisibility(GONE);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.two_line_action_row;
	}
}
