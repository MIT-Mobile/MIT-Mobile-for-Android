package edu.mit.mitmobile2.tour;

import edu.mit.mitmobile2.AudioPlayer;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.RemoteImageView;
import edu.mit.mitmobile2.TitleBar;
import edu.mit.mitmobile2.tour.Tour.SideTrip;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;


public class TourSideTripActivity extends ModuleActivity {

	private String mTitle;
	private String mHtml; 
	private String mPhotoUrl;
	private String mAudioUrl;
	
	private static String TITLE = "title";
	private static String HTML = "html";
	private static String PHOTO_URL = "photo_url";
	private static String AUDIO_URL = "audio_url";
	
	private AudioPlayer mAudioPlayer;
	
	public static void launch(Context context, SideTrip sideTrip) {
		Intent intent = new Intent(context, TourSideTripActivity.class);
		intent.putExtra(TITLE, sideTrip.getTitle());
		intent.putExtra(HTML, sideTrip.getHtml());
		intent.putExtra(PHOTO_URL, sideTrip.getPhotoUrl());
		intent.putExtra(AUDIO_URL, sideTrip.getAudioUrl());
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		mTitle = getIntent().getStringExtra(TITLE);
		mHtml = getIntent().getStringExtra(HTML);
		mPhotoUrl = getIntent().getStringExtra(PHOTO_URL);
		mAudioUrl = getIntent().getStringExtra(AUDIO_URL);
		
		setContentView(R.layout.tour_side_trip);
		
		TextView titleView = (TextView) findViewById(R.id.tourStopTitle);
		titleView.setText(mTitle);
		
		TitleBar titleBar = (TitleBar) findViewById(R.id.tourSideTripTitleBar);
		titleBar.enableBackButtonListener(new View.OnClickListener() {
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
	protected Module getModule() {
		return new TourModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(mAudioPlayer != null) {
			mAudioPlayer.stop();
		}
	}
}
