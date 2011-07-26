package edu.mit.mitmobile2;

import edu.mit.mitmobile2.about.BuildSettings;
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
	
	private static int PRIMARY = 0;
	private static int SECONDARY = 1;
	
	public SectionHeader(Context context, AttributeSet attributeSet) {
			super(context, attributeSet);
			
			String initialText = attributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
			int prominence = attributeSet.getAttributeIntValue("http://schemas.android.com/apk/res/" + BuildSettings.release_project_name, "prominence", PRIMARY);
			initializeHelper(context, initialText, prominence);
	}
	
	public SectionHeader(Context context, String initialText) {
		super(context);
		initializeHelper(context, initialText, PRIMARY);
	}
	
	public void setBackgroundResourceId(int resourceId) {
		mBackgroundView.setImageResource(resourceId);
	}
	
	private void initializeHelper(Context context, String initialText, int prominence) {
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.section_header, this);
		
		mTextView = (TextView) findViewById(R.id.sectionHeaderTV);
		mTextView.setText(initialText);
		
		mBackgroundView = (ImageView) findViewById(R.id.sectionHeaderBackgroundIV);
		
		if(prominence == PRIMARY) {
			setBackgroundResourceId(R.drawable.list_subhead);
		} else if(prominence == SECONDARY) {
			setBackgroundResourceId(R.drawable.list_subhead_gray);
		}
		
		int height = mBackgroundView.getDrawable().getIntrinsicHeight();
		LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, height);
		findViewById(R.id.sectionHeaderWrapper)
			.setLayoutParams(params);
	}
	
	public void setText(String text) {
		mTextView.setText(text);
	}
}

