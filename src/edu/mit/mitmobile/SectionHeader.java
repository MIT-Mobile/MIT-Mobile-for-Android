package edu.mit.mitmobile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


public class SectionHeader extends FrameLayout {
	private TextView mTextView;
	private ImageView mBackgroundView;
	
	public SectionHeader(Context context, AttributeSet attributeSet) {
			super(context, attributeSet);
			
			String initialText = attributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "text");			
			initializeHelper(context, initialText);
	}
	
	public SectionHeader(Context context, String initialText) {
		super(context);
		initializeHelper(context, initialText);
	}
	
	public void setBackgroundResourceId(int resourceId) {
		mBackgroundView.setImageResource(resourceId);
	}
	
	private void initializeHelper(Context context, String initialText) {
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.section_header, this);
		
		mTextView = (TextView) findViewById(R.id.sectionHeaderTV);
		mTextView.setText(initialText);
		
		mBackgroundView = (ImageView) findViewById(R.id.sectionHeaderBackgroundIV);
		int height = mBackgroundView.getDrawable().getIntrinsicHeight();
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, height);
		findViewById(R.id.sectionHeaderWrapper)
			.setLayoutParams(params);
	}
	
	public void setText(String text) {
		mTextView.setText(text);
	}
}

