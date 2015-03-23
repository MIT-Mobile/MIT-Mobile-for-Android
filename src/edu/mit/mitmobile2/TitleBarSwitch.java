package edu.mit.mitmobile2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TitleBarSwitch extends LinearLayout {

	public interface OnToggledListener {
		public void onToggled(String label);
	}
	
	TextView mTextView1;
	TextView mTextView2;
	View mSelectedView;
	View mUnselectedView;
	
	String mLabel1;
	String mLabel2;
	
	OnToggledListener mListener;	
	
	boolean mTouchEventsComplete = true;
	
	public TitleBarSwitch(Context context) {
		super(context);
		
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflator.inflate(R.layout.title_bar_switch, this);
		
		mTextView1 = (TextView) findViewById(R.id.titleBarSwitchLabel1);
		mTextView2 = (TextView) findViewById(R.id.titleBarSwitchLabel2);		
	}
	
	public void setLabels(String label1, String label2) {
		mLabel1 = label1;
		mLabel2 = label2;
		mTextView1.setText(label1);
		mTextView2.setText(label2);
		setSelected(label1);
	}

	public void setSelected(String label) {
		if (label.equals(mLabel1)) {
			mTextView1.setSelected(true);
			mTextView2.setSelected(false);
			mSelectedView = mTextView1;
			mUnselectedView = mTextView2;
		} else if (label.equals(mLabel2)) {
			mTextView1.setSelected(false);
			mTextView2.setSelected(true);
			mSelectedView = mTextView2;
			mUnselectedView = mTextView1;		
		}		
	}
	
	public void setOnToggledListener(OnToggledListener listener) {
		mListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		
		switch(action) {
			case MotionEvent.ACTION_DOWN:
				mTouchEventsComplete = false;
				mUnselectedView.setPressed(true);				
				break;
			
			case MotionEvent.ACTION_UP:
				if (!mTouchEventsComplete) {
					mUnselectedView.setPressed(false);
					handleClick();
					mTouchEventsComplete = true;
				}
				break;
				
			case MotionEvent.ACTION_CANCEL:
				mTouchEventsComplete = true;
				mUnselectedView.setPressed(false);
				break;
			case MotionEvent.ACTION_MOVE:
				float x = event.getX();
				float y = event.getY();
				if (x < 0 || y < 0 || x > getWidth() || y > getHeight()) {
					mUnselectedView.setPressed(false);
					mTouchEventsComplete = true;
				}
		}
		return true;
	}
	
	public void handleClick() {
		if (mUnselectedView == mTextView1) {
			setSelected(mLabel1);
			if (mListener != null) {
				mListener.onToggled(mLabel1);
			}
		} else if (mUnselectedView == mTextView2) {
			setSelected(mLabel2);
			if (mListener != null) {
				mListener.onToggled(mLabel2);
			}			
		}
	} 
}
