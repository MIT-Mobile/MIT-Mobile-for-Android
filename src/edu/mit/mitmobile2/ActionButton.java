package edu.mit.mitmobile2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActionButton extends LinearLayout {

	TextView mTextView;
	ImageView mImageView;

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";
	
	public ActionButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.action_button, this);
		
		mTextView = (TextView) findViewById(R.id.actionButtonLabel);
		mImageView = (ImageView) findViewById(R.id.actionButtonImage);
		
		String initialText = attrs.getAttributeValue(NAMESPACE, "text");
		setText(initialText);
		
		int imageResId = attrs.getAttributeResourceValue(NAMESPACE, "src", -1);
		if(imageResId > 0) {
			setImageResourceId(imageResId);
		}	
		
		setBackgroundResource(R.drawable.highlight_background);
	}
	

	public void setText(CharSequence label) {
		mTextView.setText(label);
	}
	
	public void setImageResourceId(int imageResId) {
		mImageView.setImageResource(imageResId);
		
	}	
	
}
