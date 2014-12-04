package edu.mit.mitmobile2.tour;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;
import edu.mit.mitmobile2.AudioPlayer;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.RemoteImageView;
import edu.mit.mitmobile2.tour.Tour.SideTrip;
import edu.mit.mitmobile2.tour.Tour.Site;


public class TourSideTripActivity extends NewModuleActivity {

	private String mTitle;
	private String mHtml; 
	private String mPhotoUrl;
	private String mAudioUrl;
	
	private static String SITE_GUID = "site_guid";
	private static String SIDETRIP_ID = "sidetrip_id";
	private static String IS_ON_SITE = "is_on_site";
	
	private AudioPlayer mAudioPlayer;
	
	public static void launch(Context context, String siteGuid, String sidetripId, boolean isOnSite) {
		Intent intent = new Intent(context, TourSideTripActivity.class);
		intent.putExtra(SITE_GUID, siteGuid);
		intent.putExtra(SIDETRIP_ID, sidetripId);
		intent.putExtra(IS_ON_SITE, isOnSite);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		String siteGuid = getIntent().getStringExtra(SITE_GUID);
		String sidetripId = getIntent().getStringExtra(SIDETRIP_ID);
		boolean isOnSite = getIntent().getBooleanExtra(IS_ON_SITE, true);
				
		Site site = TourModel.getTour(this).getSite(siteGuid);
		
		SideTrip sidetrip = site.getSideTrip(sidetripId, isOnSite);
		
		mTitle = sidetrip.getTitle();
		mHtml = sidetrip.getHtml();
		mPhotoUrl = sidetrip.getPhotoUrl();
		mAudioUrl = sidetrip.getAudioUrl();
		
		setContentView(R.layout.tour_side_trip);
		
		TextView titleView = (TextView) findViewById(R.id.tourStopTitle);
		titleView.setText(mTitle);
		
		View backRow = findViewById(R.id.tourSideTripBackRow);
		backRow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TourSideTripActivity.this.finish();				
			}
		});
		
		WebView bodyView = (WebView) findViewById(R.id.tourStopWebView);
		String pageHtml = TourHtml.tourStopHtml(this, mHtml, null, null);
		bodyView.loadDataWithBaseURL(null, pageHtml, "text/html", "utf-8", null);
		
		if(mPhotoUrl != null) {
			View imageDividerView = findViewById(R.id.tourStopPhotoDivider);
			imageDividerView.setVisibility(View.VISIBLE);
			
			RemoteImageView imageView = (RemoteImageView) findViewById(R.id.tourStopPhoto);
			imageView.setVisibility(View.VISIBLE);
			imageView.setURL(mPhotoUrl);
		}
		
		final ImageButton audioButton = (ImageButton) findViewById(R.id.tourVoiceOverButton);
		if(mAudioUrl != null) {
			audioButton.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					mAudioPlayer = new AudioPlayer();
					mAudioPlayer.togglePlay(mAudioUrl, audioButton);					
				}
			});
			
		} else {
			audioButton.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected NewModule getNewModule() {
		return new TourModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(mAudioPlayer != null) {
			mAudioPlayer.stop();
		}
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) { }
}
