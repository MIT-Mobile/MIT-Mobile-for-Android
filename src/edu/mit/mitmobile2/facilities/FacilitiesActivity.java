package edu.mit.mitmobile2.facilities;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.news.NewsDetailsActivity;
import edu.mit.mitmobile2.news.NewsModel;
import edu.mit.mitmobile2.objs.EmergencyItem;

//public class FacilitiesActivity extends ModuleActivity implements OnClickListener {
public class FacilitiesActivity extends Activity implements OnClickListener {

	// this is a test 
	private Button reportButton;
	private Button callButton;
	
	private WebView mEmergencyMsgTV = null;

	private Context mContext;	

	TextView emergencyContactsTV;

	SharedPreferences pref;
	
	static EmergencyItem emergencyItem;
	
	static String PREF_KEY_EMERGENCY_TEXT = "emergency_text";
	
	//static final int MENU_REFRESH = MENU_SEARCH + 1;
	public static final String TAG = "FacilitiesActivity";
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate()");
		super.onCreate(savedInstanceState);
		mContext = this;
		this.setContentView(R.layout.facilities);
		this.reportButton = (Button)this.findViewById(R.id.facilitiesReportAProblemButton);
		reportButton.setOnClickListener(this);
		this.callButton = (Button)this.findViewById(R.id.facilitiesCallFacilitiesButton);
		callButton.setOnClickListener(this);

	}

//	@Override
//	protected Module getModule() {
//		return new FacilitiesModule();
//	}

//	@Override
//	public boolean isModuleHomeActivity() {
//		return true;
//	}

//	@Override
//	protected void prepareActivityOptionsMenu(Menu menu) { 
//		menu.add(0, MENU_REFRESH, Menu.NONE, "Refresh")
//		  .setIcon(R.drawable.menu_refresh);
//	}

	public void onClick(View v) {
		Log.d(TAG, "clicked " + v.getId());
		switch (v.getId()) {
			case  R.id.facilitiesReportAProblemButton:
				Log.d(TAG,"report a problem");
				reportProblem();
			break;
			case  R.id.facilitiesCallFacilitiesButton:
				Log.d(TAG,"call");
				callFacilities();
			break;
		}
	}
	
	public void reportProblem() {		
		//Intent intent = new Intent(Intent., Uri.parse("tel:" + numericPhone));
		//startActivity(intent);
		Intent i = new Intent(mContext, FacilitiesProblemTypeActivity.class);
		startActivity(i);
	}

	public void callFacilities() {		
		Resources res = getResources();
		String phone = res.getString(R.string.facilities_phone);
		String numericPhone = PhoneNumberUtils.convertKeypadLettersToDigits(phone);
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + numericPhone));
		startActivity(intent);
	}
//	String numericPhone = PhoneNumberUtils.convertKeypadLettersToDigits(c.phone);
	//Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + numericPhone));
	//startActivity(intent);
}
