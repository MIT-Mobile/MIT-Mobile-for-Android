package edu.mit.mitmobile2.maps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.R;

public class MapDetailItemLayout extends LinearLayout {

	private View mMainLayout;
	private View mActionButton;
	private ImageView mActionIcon;
	private OnClickListener mOnClickListener;
	

	
	public MapDetailItemLayout(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public MapDetailItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMainLayout = inflater.inflate(R.layout.map_detail_list_item, this);
		mActionIcon = (ImageView) mMainLayout.findViewById(R.id.mapDetailListItemIcon);
		mActionButton = mMainLayout.findViewById(R.id.mapDetailListItemActionButton);
	}
	
	public void setContentType(String value) {
		((TextView) mMainLayout.findViewById(R.id.mapDetailListItemTitle)).setText(value.toUpperCase());
	}
	
	public void setContentValue(String value) {
		((TextView) mMainLayout.findViewById(R.id.mapDetailListItemContent)).setText(value);
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
