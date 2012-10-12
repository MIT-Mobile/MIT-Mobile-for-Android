package edu.mit.mitmobile2.facilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;

//public class FacilitiesActivity extends ModuleActivity implements OnClickListener {
public class FacilitiesInsideOutsideActivity extends ModuleActivity {

	// this is a test 
	private Button reportButton;
	//private ImageView callButton;
	

	private Context mContext;	

	TextView emergencyContactsTV;

	SharedPreferences pref;
	
	
	
	public static final String TAG = "FacilitiesInsideOutsideActivity";

	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate()");
		super.onCreate(savedInstanceState);
		mContext = this;
        Handler uiHandler = new Handler();

		createViews();
	}

	private void createViews() {
		setContentView(R.layout.facilities_inside_outside);

		// Inside
		TwoLineActionRow insideLocationActionRow = (TwoLineActionRow) findViewById(R.id.facilitiesInsideLocationActionRow);
		insideLocationActionRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Global.sharedData.getFacilitiesData().setBuildingRoomName("inside");
				Intent intent;
				intent = new Intent(mContext, FacilitiesProblemTypeActivity.class);					
				startActivity(intent);
			}
		});
		
		// Outside
		TwoLineActionRow outsideLocationActionRow = (TwoLineActionRow) findViewById(R.id.facilitiesOutsideLocationActionRow);
		outsideLocationActionRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Global.sharedData.getFacilitiesData().setBuildingRoomName("outside");
				Intent intent;
				intent = new Intent(mContext, FacilitiesProblemTypeActivity.class);					
				startActivity(intent);
			}
		});

	}

	@Override
	protected Module getModule() {
		return new FacilitiesModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) { 
	}

}
