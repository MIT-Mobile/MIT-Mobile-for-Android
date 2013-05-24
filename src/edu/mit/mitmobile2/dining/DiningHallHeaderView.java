package edu.mit.mitmobile2.dining;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.RemoteImageView;
import edu.mit.mitmobile2.dining.DiningModel.DiningHall;

public class DiningHallHeaderView extends LinearLayout {

	private Context mContext;
	private TextView mTitleView;
	private TextView mSubtitleView;
	private RemoteImageView mIconView;
	private ImageView mActionImage;
	
	public DiningHallHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		inflateLayout(context);
	}
	
	private void inflateLayout(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.dining_hall_header, this);
		
		mTitleView = (TextView) findViewById(R.id.diningHallHeaderTitle);
		mSubtitleView = (TextView) findViewById(R.id.diningHallHeaderSubtitle);
		mIconView = (RemoteImageView) findViewById(R.id.diningHallHeaderImage);
		mActionImage = (ImageView) findViewById(R.id.diningHallActionImage);
	}
	
	
	public DiningHallHeaderView(Context context, DiningHall hall, long currentTime) {
		super(context);
		mContext = context; 
		inflateLayout(context);	
		
		setHall(hall, currentTime);
	}
	
	public void setActionImageResourceId(int resId) {
		mActionImage.setImageResource(resId);
	}
	
	public void setActionClickListener(OnClickListener clickListener) {
		mActionImage.setOnClickListener(clickListener);
	}
	
	public void setHall(DiningHall hall, long currentTime) {
		mTitleView.setText(hall.getName());
		mSubtitleView.setText(hall.getCurrentStatusSummary(currentTime));
		mIconView.setURL(hall.getIconUrl());
		
		int colorResID = 0;
		switch (hall.getCurrentStatus(currentTime)) {
			case OPEN:
				colorResID = R.color.dining_open;
				break;
			case CLOSED:
				colorResID = R.color.dining_closed;
				break;				
		}
		mSubtitleView.setTextColor(mContext.getResources().getColor(colorResID));
	}
}
