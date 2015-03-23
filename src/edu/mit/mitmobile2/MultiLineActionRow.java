package edu.mit.mitmobile2;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MultiLineActionRow extends TwoLineActionRow {
//	private TextView mTitleView;
//	private TextView mSubtitleView;
//	private ImageView mActionIconView;
	
	private int layoutId;
	
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";
	public MultiLineActionRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initHelper(context);	
		
		if(attrs != null) {
						
			int actionResId = attrs.getAttributeResourceValue(NAMESPACE, "src", -1);
			if(actionResId > 0) {
				//setActionIconResource(actionResId);
			}
			
			int textColorResId = attrs.getAttributeResourceValue(NAMESPACE, "textColor", -1);
			if(textColorResId > 0) {
				@SuppressWarnings("unused")
				int textColor = context.getResources().getColor(textColorResId);
				//mTitleView.setTextColor(textColor);
			} else {
				String colorString = attrs.getAttributeValue(NAMESPACE, "textColor");
				if(colorString != null) {
					//mTitleView.setTextColor(Color.parseColor(colorString));
				}
			}
		}
	}
	
	public MultiLineActionRow(Context context, int layoutId) {
		super(context);
		this.layoutId = layoutId;
		initHelper(context);
	}
	
	private void initHelper(Context context) {
		//mTitleView = (TextView) findViewById(R.id.simpleRowTitle);
		//mSubtitleView = (TextView) findViewById(R.id.simpleRowSubtitle);
		//mActionIconView = (ImageView) findViewById(R.id.simpleRowActionIcon);	
	}
	
	public void setTextView(int textView, CharSequence text, int color) {
		TextView mTextView = (TextView) findViewById(textView);
		mTextView.setText(text);
		mTextView.setTextColor(color);
		//setTitle(title, TextView.BufferType.NORMAL);
		//mTitleView.setTextColor(color);
	}

	//	public void setTitle(CharSequence title, int color) {
//		setTitle(title, TextView.BufferType.NORMAL);
//		//mTitleView.setTextColor(color);
//	}
//	
//	public void setTitle(CharSequence title) {
//		setTitle(title, TextView.BufferType.NORMAL);
//	}
	
//	public void setTitle(CharSequence title, TextView.BufferType bufferType) {
//		if(title != null) {
//			mTitleView.setText(title, bufferType);
//			mTitleView.setVisibility(VISIBLE);
//		} else {
//			mTitleView.setVisibility(GONE);
//		}
//	}
//
//	public CharSequence getTitle() {
//		return mTitleView.getText();
//	}
//	
//	public void setSubtitle(CharSequence subtitle) {
//		if(subtitle != null) {
//			mSubtitleView.setText(subtitle);
//			mSubtitleView.setVisibility(VISIBLE);
//		} else {
//			mSubtitleView.setVisibility(GONE);
//		}
//	}
//	
//	public void setActionIconResource(int resId) {
//		mActionIconView.setImageResource(resId);
//		mActionIconView.setVisibility(VISIBLE);
//	}
//	
//	public void removeActionIcon() {
//		mActionIconView.setVisibility(GONE);
//	}

	@Override
	protected int getLayoutId() {
		return this.layoutId;
	}
	
	@Override
	public void setLayoutId(int layoutId) {
		this.layoutId = layoutId;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		//mTitleView.setEnabled(enabled);
	//	mSubtitleView.setEnabled(enabled);
	}
}
