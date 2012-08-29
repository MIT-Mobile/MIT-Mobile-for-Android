package edu.mit.mitmobile2.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import edu.mit.mitmobile2.HomeScreenActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.touchstone.TouchstonePrefsActivity;

public class MITSettingsActivity extends Activity {
	

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
	
	static final int HOME_ITEM_ID = 0;
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		menu.clear();
		
		menu.add(0, HOME_ITEM_ID, Menu.NONE, "Home")
			.setIcon(R.drawable.menu_home);
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case HOME_ITEM_ID: 
				HomeScreenActivity.goHome(this);
				return true;		}
			
		return super.onOptionsItemSelected(item);
	}

}
