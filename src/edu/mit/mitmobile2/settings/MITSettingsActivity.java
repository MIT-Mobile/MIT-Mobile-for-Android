package edu.mit.mitmobile2.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.HomeScreenActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.touchstone.TouchstonePrefsActivity;

public class MITSettingsActivity extends NewModuleActivity {
	

	private Context mContext;			
	
	public static final String TAG = "MITSettingsActivity";
	TwoLineActionRow touchstoneSettingsButton;
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate()");
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.settings_main);

		// Set Up Buttons
		
		// TOuchstone
		touchstoneSettingsButton = (TwoLineActionRow)findViewById(R.id.touchstoneSettingsButton);

		touchstoneSettingsButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, TouchstonePrefsActivity.class);
				startActivity(intent);
			}
		});
		
	}

	@Override
	protected NewModule getNewModule() {
		return new SettingsModule();
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {

	}

	@Override
	protected boolean isModuleHomeActivity() {
	    return true;
	}

}
