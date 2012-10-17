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
	private View mActionButton;
	private ImageView mActionIcon;
	private OnClickListener mOnClickListener;
	
	private int mSystemSelectionResourceId = R.drawable.highlight_background;
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
		mActionIcon = (ImageView) mMainLayout.findViewById(R.id.peopleDetailListItemIcon);
		mActionButton = mMainLayout.findViewById(R.id.peopleDetailListItemActionButton);
	}
	
	public void setContentType(String value) {
		((TextView) mMainLayout.findViewById(R.id.peopleDetailListItemTitle)).setText(value.toUpperCase());
	}
	
	public void setContentValue(String value) {
		((TextView) mMainLayout.findViewById(R.id.peopleDetailListItemContent)).setText(value);
	}
	
	public void setActionIconResource(int resId) {
	    	mActionIcon.setVisibility(View.VISIBLE);
		mActionIcon.setImageResource(resId);
	}
	
	public void hideActionIcon() {
	    	mActionIcon.setVisibility(View.GONE);
	}
	
	public void setOnItemClickListener(OnClickListener listener) {
		if (null != listener) {
			mOnClickListener = listener;
			mActionButton.setOnClickListener(mOnClickListener);
		}
	}
}
