package edu.mit.mitmobile2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.about.Config;


public class SectionHeader extends FrameLayout {
	private TextView mTextView;
	
	public enum Prominence {
		PRIMARY,
		SECONDARY
	}
	
	public SectionHeader(Context context, AttributeSet attributeSet) {
			super(context, attributeSet);
			
			String initialText = attributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
			int prominenceInt = attributeSet.getAttributeIntValue("http://schemas.android.com/apk/res/" + Config.release_project_name, "prominence", 0);
			Prominence prominence = Prominence.values()[prominenceInt];
			initializeHelper(context, initialText, prominence);
	}
	
	public SectionHeader(Context context, String initialText) {
		super(context);
		initializeHelper(context, initialText, Prominence.PRIMARY);
	}
	
	public SectionHeader(Context context, String initialText, Prominence prominence) {
		super(context);
		initializeHelper(context, initialText, prominence);
	}
	
	private void initializeHelper(Context context, String initialText, Prominence prominence) {
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.section_header, this);
		
		mTextView = (TextView) findViewById(R.id.sectionHeaderTV);
		mTextView.setText(initialText);
		
		
		int color = -1;
		if(prominence == Prominence.PRIMARY) {
		    color = context.getResources().getColor(R.color.primarySectionHeaderBackground);
		} else if(prominence == Prominence.SECONDARY) {
		    color = context.getResources().getColor(R.color.secondarySectionHeaderBackground);
		}
		setBackgroundColor(color);
	}
	
	public void setText(String text) {
		mTextView.setText(text);
	}
	
	public TextView getTextView() {
		return mTextView;
	}
}

