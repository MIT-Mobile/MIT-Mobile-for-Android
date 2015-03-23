package edu.mit.mitmobile2.tour;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.DividerView;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.tour.Tour.FooterLink;
import edu.mit.mitmobile2.tour.Tour.TourFooter;

public class TourThankYouSliderInterface implements SliderInterface {

	private View mView;
	private LinearLayout mContentView;
	private Context mContext;
	private TourFooter mTourFooter;
	
	public TourThankYouSliderInterface(Context context, TourFooter tourFooter) {
		mContext = context;
		mTourFooter = tourFooter;
	}
	
	@Override
	public LockingScrollView getVerticalScrollView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getView() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = inflater.inflate(R.layout.tour_thankyou, null);
		mContentView = (LinearLayout) mView.findViewById(R.id.tourThankYouContent);
		return mView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSelected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateView() {
		TwoLineActionRow emailActionRow = new TwoLineActionRow(mContext);
		emailActionRow.setTitle("Send feedback");
		emailActionRow.setActionIconResource(R.drawable.action_email);
		emailActionRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String email = mContext.getResources().getString(R.string.feedback_email);
				CommonActions.composeEmail(mContext, email, mTourFooter.getFeedbackSubject());
			}
		});
		
		mContentView.addView(emailActionRow);
		mContentView.addView(new DividerView(mContext, null));
		
		for(final FooterLink link : mTourFooter.getLinks()) {
			TwoLineActionRow linkActionRow = new TwoLineActionRow(mContext);
			linkActionRow.setTitle(link.getTitle());
			linkActionRow.setActionIconResource(CommonActions.getActionIconId(link.getUrl()));
			linkActionRow.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CommonActions.doAction(mContext, link.getUrl());
				}
			});
			
			mContentView.addView(linkActionRow);
			mContentView.addView(new DividerView(mContext, null));
		}
	}
}
