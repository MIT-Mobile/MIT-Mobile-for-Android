package edu.mit.mitmobile.events;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.mit.mitmobile.AttributesParser;
import edu.mit.mitmobile.CommonActions;
import edu.mit.mitmobile.DividerView;
import edu.mit.mitmobile.FullScreenLoader;
import edu.mit.mitmobile.IdEncoder;
import edu.mit.mitmobile.LockingScrollView;
import edu.mit.mitmobile.MobileWebApi;
import edu.mit.mitmobile.R;
import edu.mit.mitmobile.SliderInterface;
import edu.mit.mitmobile.StyledContentHTML;
import edu.mit.mitmobile.TwoLineActionRow;
import edu.mit.mitmobile.about.BuildSettings;
import edu.mit.mitmobile.objs.EventDetailsItem;

public class EventDetailsView extends LockingScrollView implements SliderInterface  {

	private Activity mActivity;
	private EventDetailsItem mBriefDetails;
	private boolean mBriefMode;
	private LinearLayout mLinearLayout;
	private EventDetailsItem mFullDetails = null;
	private FullScreenLoader mLoaderView = null;
	private WebView mDescriptionView;
	
	private static final SimpleDateFormat sStartFormat = new SimpleDateFormat("EEEE, MMMM d yyyy h:mm a");
	private static final SimpleDateFormat sEndFormat = new SimpleDateFormat("h:mm a");
	
	
	public EventDetailsView(Activity activity, EventDetailsItem briefDetails, boolean briefMode) {
		super(activity);
		
		mActivity = activity;
		mBriefDetails = briefDetails;
		mBriefMode = briefMode;

		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.event_details, this);
		
		mLinearLayout = (LinearLayout) findViewById(R.id.eventDetailsLinearLayout);
		
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
		
		String url  = "http://" + BuildSettings.MOBILE_WEB_DOMAIN + "/e/" + IdEncoder.shortenId(eventDetails.id);
		CommonActions.shareContent(mActivity, eventDetails.title, eventDetails.description, url);
	}
	
	@Override
	public void updateView() {
		
		TextView titleView = (TextView) findViewById(R.id.eventDetailsTitleTV);
		titleView.setText(mBriefDetails.title);
		
		Date startDate = new Date(mBriefDetails.start * 1000);
		Date endDate = new Date(mBriefDetails.end * 1000);
		String dateText = sStartFormat.format(startDate) + "-" + sEndFormat.format(endDate);
		
		TextView dateView = (TextView) findViewById(R.id.eventDetailsDateTV);
		dateView.setText(dateText);	
		
		// add the location row				
		if(mBriefDetails.getLocationName() != null) {
			TwoLineActionRow locationRow = new TwoLineActionRow(mActivity, null);
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
		
		if(!mBriefMode) {
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
		if(mBriefMode) {
			// nothing to load (in brief mode)
			return;
		}
		
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

						
						// add the location row
						if(!mFullDetails.infophone.equals("")) {
							TwoLineActionRow phoneRow = new TwoLineActionRow(mActivity, null);
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
							TwoLineActionRow urlRow = new TwoLineActionRow(mActivity, null);
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
							mDescriptionView.loadDataWithBaseURL(null, StyledContentHTML.html(mFullDetails.description), "text/html", "utf-8", null);							
							
							mLinearLayout.addView(mDescriptionView);
							mLinearLayout.addView(new DividerView(mActivity, null));
						}
					} else {
						mLoaderView.showError();
					}
				}
			}
		);
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
