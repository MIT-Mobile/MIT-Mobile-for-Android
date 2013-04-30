package edu.mit.mitmobile2.dining;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.DiningModel.DiningHall;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DiningHallHeaderView extends LinearLayout {

	private ImageView mActionImage;
	
	public DiningHallHeaderView(Context context, DiningHall hall, long currentTime) {
		super(context);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.dining_hall_header, this);
		
		TextView titleView = (TextView) findViewById(R.id.diningHallHeaderTitle);
		TextView subtitleView = (TextView) findViewById(R.id.diningHallHeaderSubtitle);
		mActionImage = (ImageView) findViewById(R.id.diningHallActionImage);	
		
		titleView.setText(hall.getName());
		subtitleView.setText(hall.getCurrentStatusSummary(currentTime));
		
		int colorResID = 0;
		switch (hall.getCurrentStatus(currentTime)) {
			case OPEN:
				colorResID = R.color.dining_open;
				break;
			case CLOSED:
				colorResID = R.color.dining_closed;
				break;				
		}
		
		subtitleView.setTextColor(context.getResources().getColor(colorResID));			
	}
	
	public void setActionImageResourceId(int resId) {
		mActionImage.setImageResource(resId);
	}
	
	public void setActionClickListener(OnClickListener clickListener) {
		mActionImage.setOnClickListener(clickListener);
	}
}
