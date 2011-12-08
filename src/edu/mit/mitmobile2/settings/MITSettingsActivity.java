package edu.mit.mitmobile2.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.touchstone.TouchstonePrefsActivity;

public class MITSettingsActivity extends Activity {
	

	private Context mContext;			
	
	public static final String TAG = "MITSettingsActivity";
	Button touchstoneSettingsButton;
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate()");
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.settings_main);

		// Set Up Buttons
		
		// TOuchstone
		touchstoneSettingsButton = (Button)findViewById(R.id.touchstoneSettingsButton);

		touchstoneSettingsButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, TouchstonePrefsActivity.class);
				startActivity(intent);
			}
		});
		
	}

}
