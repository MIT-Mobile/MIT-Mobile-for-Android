package edu.mit.mitmobile2.people;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.R;

public class PeopleDetailItemLayout extends LinearLayout {

	private View mMainLayout;
	private LinearLayout mIconLayout;
	private OnClickListener mOnClickListener;
	
	private int mSystemSelectionResourceId = android.R.drawable.list_selector_background;
	private int mBackgroundResourceId = R.color.rowBackground;
	
	public PeopleDetailItemLayout(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public PeopleDetailItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMainLayout = inflater.inflate(R.layout.people_detail_list_item, this);
		mIconLayout = (LinearLayout) mMainLayout.findViewById(R.id.peopleDetailListItemIconLayout);
	}
	
	public void setContentType(String value) {
		((TextView) mMainLayout.findViewById(R.id.peopleDetailListItemTitle)).setText(value);
	}
	
	public void setContentValue(String value) {
		((TextView) mMainLayout.findViewById(R.id.peopleDetailListItemContent)).setText(value);
	}
	
	public void setActionIconResouce(int resId) {
		mIconLayout.setVisibility(View.VISIBLE);
		ImageView actionIcon = (ImageView) mIconLayout.findViewById(R.id.peopleDetailListItemIcon);
		actionIcon.setImageResource(resId);
	}
	
	public void hideActionIcon() {
		mIconLayout.setVisibility(View.GONE);
	}
	
	public void setOnItemClickListener(OnClickListener listener) {
		if (null != listener) {
			mOnClickListener = listener;
			this.setOnClickListener(mOnClickListener);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(mOnClickListener == null || !isEnabled()) {
			// do not handle touches for the view
			return false;
		}
		
		if(event.getAction() == MotionEvent.ACTION_DOWN) {			
			// setting the background sometimes causes the height to
			// change, so this is hack to prevent that
			int height = getHeight();
			this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, height));
			setBackgroundResource(mSystemSelectionResourceId);
			setPressed(true);
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
			setBackgroundResource(mBackgroundResourceId);
			setPressed(false);
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP) {
			if (mOnClickListener != null) {
				mOnClickListener.onClick(this);
			}
		}
		return true;
	}
}
