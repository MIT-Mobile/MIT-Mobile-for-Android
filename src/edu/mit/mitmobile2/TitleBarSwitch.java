package edu.mit.mitmobile2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TitleBarSwitch extends LinearLayout implements OnClickListener {

	public interface OnToggledListener {
		public void onToggled(String label);
	}
	
	TextView mTextView1;
	TextView mTextView2;
	
	String mLabel1;
	String mLabel2;
	String mCurrentState;
	
	OnToggledListener mListener;	
	
	public TitleBarSwitch(Context context) {
		super(context);
		
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.title_bar_switch, this);
		
		mTextView1 = (TextView) findViewById(R.id.titleBarSwitchLabel1);
		mTextView1.setOnClickListener(this);
		
		mTextView2 = (TextView) findViewById(R.id.titleBarSwitchLabel2);
		mTextView2.setOnClickListener(this);		
	}
	
	public void setLabels(String label1, String label2) {
		mLabel1 = label1;
		mLabel2 = label2;
		mTextView1.setText(label1);
		mTextView2.setText(label2);
	}

	public void setSelected(String label) {
		if (label.equals(mLabel1)) {
			mTextView1.setSelected(true);
			mTextView1.setClickable(false);
			mTextView2.setSelected(false);
			mTextView2.setClickable(true);
			
		} else if (label.equals(mLabel2)) {
			mTextView1.setSelected(false);
			mTextView1.setClickable(true);
			mTextView2.setSelected(true);
			mTextView2.setClickable(false);
		}		
	}
	
	public void setOnToggledListener(OnToggledListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(View view) {
		if (view == mTextView1) {
			setSelected(mLabel1);
			if (mListener != null) {
				mListener.onToggled(mLabel1);
			}
		} else if (view == mTextView2) {
			setSelected(mLabel2);
			if (mListener != null) {
				mListener.onToggled(mLabel2);
			}			
		}
	} 
}
