package edu.mit.mitmobile2.events;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.mit.mitmobile2.AttributesParser;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.DividerView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.LockingScrollView;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SliderInterface;
import edu.mit.mitmobile2.StyledContentHTML;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.objs.EventDetailsItem;

public class EventDetailsView extends LockingScrollView implements SliderInterface  {

	private Activity mActivity;
	private EventDetailsItem mBriefDetails;
	private boolean mBriefMode;
	private LinearLayout mLinearLayout;
	private EventDetailsItem mFullDetails = null;
	private FullScreenLoader mLoaderView = null;
	private WebView mDescriptionView;
	public static final String TAG = "EventDetailsView";
	public EventDetailsView(Activity activity, EventDetailsItem briefDetails, boolean briefMode) {
		super(activity);
		
		mActivity = activity;
		mBriefDetails = briefDetails;
		mBriefMode = briefMode;

		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.event_details, this);
		
		mLinearLayout = (LinearLayout) findViewById(R.id.eventDetailsLinearLayout);
		
		if(briefDetails.description != null && !briefDetails.description.equals("")) {
			mFullDetails = mBriefDetails;
		}
		
	}

	public boolean hasLoadingCompleted() {
		return (mFullDetails != null) || (mBriefMode);
	}
	
	void addEvent() {
		

		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		
		EventDetailsItem eventDetails = mBriefMode ? mBriefDetails : mFullDetails;
		
		intent.putExtra("beginTime", eventDetails.start * 1000);
		intent.putExtra("endTime", eventDetails.end * 1000);
		intent.putExtra("title", eventDetails.title);
		intent.putExtra("description", eventDetails.description);
		intent.putExtra("eventLocation", eventDetails.location);
		
		mActivity.startActivity(Intent.createChooser(intent, "Calendar"));
	}
	
	void shareEvent() {
		EventDetailsItem eventDetails = mBriefMode ? mBriefDetails : mFullDetails;
		
		String url = eventDetails.infourl;
		//String url  = "http://" + BuildSettings.MOBILE_WEB_DOMAIN + "/e/" + IdEncoder.shortenId(eventDetails.id);
		CommonActions.shareContent(mActivity, eventDetails.title, eventDetails.description, url);
	}
	
	@Override
	public void updateView() {
		
		TextView titleView = (TextView) findViewById(R.id.eventDetailsTitleTV);
		titleView.setText(mBriefDetails.title);
		

		TextView dateView = (TextView) findViewById(R.id.eventDetailsDateTV);
		Log.d(TAG,"event date = " + mBriefDetails.getStartDate().toString());
		dateView.setText(mBriefDetails.getTimeSummary(EventDetailsItem.LONG_DAY_TIME));	
		
		// add the location row				
		if(mBriefDetails.getLocationName() != null) {
			TwoLineActionRow locationRow = new TwoLineActionRow(mActivity);
			locationRow.setTitle(mBriefDetails.getLocationName());
			
			if (mBriefDetails.coordinates != null) {
				locationRow.setActionIconResource(R.drawable.action_map);
				locationRow.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CommonActions.searchMap(mActivity, mBriefDetails.shortloc);
					}
				});
			}
			
			mLinearLayout.addView(locationRow);
			mLinearLayout.addView(new DividerView(mActivity, null));
			
			
		}
		
		if(mFullDetails != null) {
			populateFullDetails();
		} else if(!mBriefMode) {
			// loader for more detail info
			mLoaderView = new FullScreenLoader(mActivity, null);
			mLoaderView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, AttributesParser.parseDimension("200dip", mActivity)));
			mLinearLayout.addView(mLoaderView);
		}
	}
	
	@Override
	public View getView() {
		return this;
	}

	@Override
	public void onSelected() {
		if(mBriefMode || mFullDetails != null) {
			// nothing to load (in brief mode) or already loaded
			return;
		}
		
		mLoaderView.showLoading();
		EventsModel.fetchEventDetails(mBriefDetails.id, mActivity, 
			new Handler () {
			
				@Override
				public void handleMessage(Message message) {
					if(mFullDetails != null) {
						// exit early already loaded
						return;
					}
					
					if(message.arg1 == MobileWebApi.SUCCESS) {
						mFullDetails = EventsModel.getFullEvent(mBriefDetails.id);
						mLoaderView.setVisibility(GONE);
						populateFullDetails();
						

					} else {
						mLoaderView.showError();
					}
				}
			}
		);
	}


	private void populateFullDetails() {
		// add the location row
		if(!mFullDetails.infophone.equals("")) {
			TwoLineActionRow phoneRow = new TwoLineActionRow(mActivity);
			phoneRow.setTitle(mFullDetails.infophone);
			phoneRow.setActionIconResource(R.drawable.action_phone);
			phoneRow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					CommonActions.callPhone(mActivity, mFullDetails.infophone);
				}
			});							
			
			mLinearLayout.addView(phoneRow);
			mLinearLayout.addView(new DividerView(mActivity, null));
		}
		
		// add the external link row
		if(!mFullDetails.infourl.equals("")) {
			TwoLineActionRow urlRow = new TwoLineActionRow(mActivity);
			urlRow.setTitle(mFullDetails.infourl);
			urlRow.setActionIconResource(R.drawable.action_external);
			urlRow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String url = mFullDetails.infourl;
					if(!CommonActions.hasProtocol(url)) {
						url = "http://" + url;
					}
					CommonActions.viewURL(mActivity, url);
				}
			});							
			
			mLinearLayout.addView(urlRow);
			mLinearLayout.addView(new DividerView(mActivity, null));
		}
		
		// add description
		if(!mFullDetails.description.equals("")) {
			mDescriptionView = new WebView(mActivity);
			mDescriptionView.setFocusable(false);
			mDescriptionView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			mDescriptionView.loadDataWithBaseURL(null, StyledContentHTML.html(mActivity, mFullDetails.description), "text/html", "utf-8", null);							
			
			mLinearLayout.addView(mDescriptionView);
			mLinearLayout.addView(new DividerView(mActivity, null));
		}
	}
	
	@Override
	public LockingScrollView getVerticalScrollView() {
		return this;
	}

	@Override
	public void onDestroy() {
		if(mDescriptionView != null) {
			mDescriptionView.destroy();
		}
		removeAllViews();
	}
}
