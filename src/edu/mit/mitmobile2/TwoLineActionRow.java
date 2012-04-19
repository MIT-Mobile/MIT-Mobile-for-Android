package edu.mit.mitmobile2;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TwoLineActionRow extends ActionRow {
	private TextView mTitleView;
	private TextView mSubtitleView;
	private ImageView mActionIconView;
	
	private int mListItemPrimaryPadding;
	private int mListItemSecondaryPadding;
	
	
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";
	public TwoLineActionRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initHelper(context);	
		
		if(attrs != null) {
			String initialText = attrs.getAttributeValue(NAMESPACE, "text");
			setTitle(initialText);
			
			int actionResId = attrs.getAttributeResourceValue(NAMESPACE, "src", -1);
			if(actionResId > 0) {
				setActionIconResource(actionResId);
			}
			
			int textColorResId = attrs.getAttributeResourceValue(NAMESPACE, "textColor", -1);
			if(textColorResId > 0) {
				int textColor = context.getResources().getColor(textColorResId);
				mTitleView.setTextColor(textColor);
			} else {
				String colorString = attrs.getAttributeValue(NAMESPACE, "textColor");
				if(colorString != null) {
					mTitleView.setTextColor(Color.parseColor(colorString));
				}
			}
		}
	}
	
	public TwoLineActionRow(Context context) {
		super(context);
		initHelper(context);
	}
	
	private void initHelper(Context context) {
		mTitleView = (TextView) findViewById(R.id.simpleRowTitle);
		mSubtitleView = (TextView) findViewById(R.id.simpleRowSubtitle);
		mActionIconView = (ImageView) findViewById(R.id.simpleRowActionIcon);	
		
		mListItemPrimaryPadding = context.getResources().getDimensionPixelOffset(R.dimen.ListItemPrimaryPadding);
		mListItemSecondaryPadding = context.getResources().getDimensionPixelOffset(R.dimen.ListItemSecondaryPadding);
	}

	/*
	 * This method is to take into account that the 
	 * vertical paddings depend on which views are visible
	 */
	private void refreshPaddings() {
	    int titleBottomPadding = 0;
	    if (mSubtitleView.getVisibility() == View.GONE) {
		titleBottomPadding = mListItemPrimaryPadding;
	    }
	    
	    int subtitleTopPadding = 0;
	    if (mTitleView.getVisibility() == View.GONE) {
		subtitleTopPadding = mListItemSecondaryPadding;
	    }
	    
	    mTitleView.setPadding(0, mListItemPrimaryPadding, 0, titleBottomPadding);
	    mSubtitleView.setPadding(0, subtitleTopPadding, 0, mListItemSecondaryPadding);
	}
	
	public void setTitle(CharSequence title, int color) {
		setTitle(title, TextView.BufferType.NORMAL);
		mTitleView.setTextColor(color);
	}
	
	public void setTitle(CharSequence title) {
		setTitle(title, TextView.BufferType.NORMAL);
	}
	
	public void setTitle(CharSequence title, TextView.BufferType bufferType) {
		if(title != null) {
			mTitleView.setText(title, bufferType);
			mTitleView.setVisibility(VISIBLE);
		} else {
			mTitleView.setVisibility(GONE);
		}
		refreshPaddings();
	}

	public CharSequence getTitle() {
		return mTitleView.getText();
	}
	
	public void setSubtitle(CharSequence subtitle) {
		if(subtitle != null) {
			mSubtitleView.setText(subtitle);
			mSubtitleView.setVisibility(VISIBLE);
		} else {
			mSubtitleView.setVisibility(GONE);
		}
		refreshPaddings();
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
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mTitleView.setEnabled(enabled);
		mSubtitleView.setEnabled(enabled);
	}
}
