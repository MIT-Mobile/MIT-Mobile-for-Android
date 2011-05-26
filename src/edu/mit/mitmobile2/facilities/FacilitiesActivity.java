package edu.mit.mitmobile2.facilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneNumberUtils;
import android.text.Spannable;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.HighlightEffects;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SearchBar;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.emergency.EmergencyContactsActivity;
import edu.mit.mitmobile2.news.NewsDetailsActivity;
import edu.mit.mitmobile2.news.NewsModel;
import edu.mit.mitmobile2.objs.EmergencyItem;
import edu.mit.mitmobile2.people.PeopleActivity;

//public class FacilitiesActivity extends ModuleActivity implements OnClickListener {
public class FacilitiesActivity extends Activity {

	// this is a test 
	private Button reportButton;
	//private ImageView callButton;
	
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
        Handler uiHandler = new Handler();

        /* 
         * check the version of the local facilities database against the mobile server and update it if necessary
         */ 
        FacilitiesDB.updateCategories(mContext, uiHandler);
        FacilitiesDB.updateLocations(mContext, uiHandler);
		createViews();
	}

	private void createViews() {
		setContentView(R.layout.facilities_main);

		String title1 = "";
		String title2 = "";

		// Report a Problem
		TwoLineActionRow reportAProblemActionRow = (TwoLineActionRow) findViewById(R.id.facilitiesReportAProblemActionRow);
		title1 = "Report a Problem";
		title2 = "";
		reportAProblemActionRow.setTitle(title1 + " " + title2, TextView.BufferType.SPANNABLE);
		//reportAProblemActionRow.setActionIconResource(R.drawable.arrow_right_normal);
		reportAProblemActionRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, FacilitiesProblemLocationActivity.class);
				startActivity(intent);
			}
		});
		
		// Call Facilities
		TwoLineActionRow callFacilitiesActionRow = (TwoLineActionRow) findViewById(R.id.facilitiesCallFacilitiesActionRow);
		title1 = "Call Facilities";
		title2 = "(555.555.5555)";
		callFacilitiesActionRow.setTitle(title1 + " " + title2, TextView.BufferType.SPANNABLE);
				
		callFacilitiesActionRow.setActionIconResource(R.drawable.action_phone);
		callFacilitiesActionRow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CommonActions.callPhone(FacilitiesActivity.this, "6172531000");
			}
		});

	}

	final Runnable updateDataInBackground = new Runnable() {
		Handler uiHandler = new Handler();
		public void run() {
			FacilitiesDB.updateFacilitiesDatabase(mContext, uiHandler);
		}
	};

}
