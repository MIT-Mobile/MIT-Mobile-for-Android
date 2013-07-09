package edu.mit.mitmobile2;

import java.util.Arrays;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.mitmobile2.MobileWebApi.DefaultErrorListener;
import edu.mit.mitmobile2.MobileWebApi.JSONObjectResponseListener;
import edu.mit.mitmobile2.MobileWebApi.ServerResponseException;
import edu.mit.mitmobile2.about.AboutActivity;
import edu.mit.mitmobile2.alerts.NotificationsHelper;
import edu.mit.mitmobile2.dining.DiningModule;
import edu.mit.mitmobile2.emergency.EmergencyModule;
import edu.mit.mitmobile2.events.EventsModule;
import edu.mit.mitmobile2.facilities.FacilitiesModule;
import edu.mit.mitmobile2.libraries.LibrariesModule;
import edu.mit.mitmobile2.links.LinksModule;
import edu.mit.mitmobile2.maps.MapsModule;
import edu.mit.mitmobile2.news.NewsModule;
import edu.mit.mitmobile2.people.PeopleModule;
import edu.mit.mitmobile2.qrreader.QRReaderModule;
import edu.mit.mitmobile2.settings.MITSettingsActivity;
import edu.mit.mitmobile2.shuttles.ShuttlesModule;
import edu.mit.mitmobile2.tour.TourModule;

public class HomeScreenActivity extends Activity implements OnSharedPreferenceChangeListener {
	
	
	private static final int ABOUT_MENU_ID = 0; 
        private static final int SETTINGS_MENU_ID = 1;

	Context ctx;
	
	private GridView mSpringBoard;
	private RemoteImageView mBannerView;
	private Banner mBanner;

	public static final String TAG = "MITNewsWidgetActivity";
	private Global app;
	private SharedPreferences prefs;
	
	private SharedPreferences mHomePreferences;
	private final static String PHOTO_URL_KEY="photo_url";
	private final static String ACTION_URL_KEY="action_url";
	private final static String HEIGHT_KEY="height_url";
	private final static String WIDTH_KEY="width_url";

	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate()");
    
		// get App
		app = (Global)this.getApplication();
		Log.d(TAG,"app = " + app);
		
		// get preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		
		ctx = this;
		
		createView();
		
		NotificationsHelper.setupAlarmData(getApplicationContext());
		
		mHomePreferences = this.getSharedPreferences("home_prefs", MODE_PRIVATE);
		
		String url = getIntent().getDataString();
		if (url != null && url.startsWith("mitmobile://")) {
			CommonActions.doAction(this, url);
			finish();
		}		
	}
	
	@Override
	public void onWindowFocusChanged (boolean hasFocus) {
		if (hasFocus && mBanner == null) {
			getBannerData();
		}
	}
	
	/****************************************************/
	void createView() {
	
		setContentView(R.layout.home);
		
		Module[] modules = new Module[] {
				new NewsModule(),
				new ShuttlesModule(),
				new MapsModule(),
				new EventsModule(),
				new PeopleModule(),
				new TourModule(),
				new EmergencyModule(),
				new LibrariesModule(),
				new FacilitiesModule(),
				new DiningModule(),
				new QRReaderModule(),
				new LinksModule(),
		};
		
		mSpringBoard = (GridView) findViewById(R.id.homeSpringBoardGV);
		
		SimpleArrayAdapter<Module> springBoardAdapter = new SimpleArrayAdapter<Module>(
			this, Arrays.asList(modules), R.layout.springboard_item) {

				@Override
				public void updateView(Module item, View view) {
					Resources resources = mContext.getResources();
					
					ImageView iconView = (ImageView) view.findViewById(R.id.springBoardImage);
					iconView.setImageDrawable(resources.getDrawable(item.getHomeIconResourceId()));
					
					TextView titleView = (TextView) view.findViewById(R.id.springBoardTitle);
					titleView.setText(item.getShortName());				
				}
				
		};
		
		springBoardAdapter.setOnItemClickListener(mSpringBoard,
			new SimpleArrayAdapter.OnItemClickListener<Module>() {

				@Override
				public void onItemSelected(Module item) {
					Intent intent = new Intent(ctx, item.getModuleHomeActivity());
					startActivity(intent);
				}
			}
		);
		
		mSpringBoard.setAdapter(springBoardAdapter);	
		
		mBannerView = (RemoteImageView) findViewById(R.id.homeSpringBoardBanner);
	}
	
	private Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == MobileWebApi.SUCCESS) {
				updateBanner();
			} else {
				String photoUrl = mHomePreferences.getString(PHOTO_URL_KEY, "");
				if (photoUrl.length() > 0) {
					Banner banner = new Banner();
					banner.photoUrl = photoUrl;
					banner.height = mHomePreferences.getInt(HEIGHT_KEY, 0);
					banner.width = mHomePreferences.getInt(WIDTH_KEY, 0);
					String actionUrl = mHomePreferences.getString(ACTION_URL_KEY, "");
					if (actionUrl.length() > 0) {
						banner.actionUrl = actionUrl;
					}
					mBanner = banner;
					updateBanner();
				}
			}
		}
	};
	
	private void updateBanner() {
		if (mBanner != null) {
			float width = mSpringBoard.getWidth();
			int height = (int) (((float)mBanner.height) / ((float)mBanner.width) * width);
			mBannerView.setURL(mBanner.photoUrl);
			mBannerView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height));
			
			if (mBanner.actionUrl != null) {
				mBannerView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CommonActions.doAction(HomeScreenActivity.this, mBanner.actionUrl);								
					}
				});
			}
			mBannerView.setVisibility(View.VISIBLE);
		}		
	}
	
	private void getBannerData() {
		MobileWebApi webApi = new MobileWebApi(false, false, "Banner", this, uiHandler);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("module", "features");
		params.put("command", "banner");
		
		webApi.requestJSONObject(params, 
			new JSONObjectResponseListener(new DefaultErrorListener(uiHandler), null) {
			
				@Override
				public void onResponse(JSONObject object) throws ServerResponseException, JSONException {
					if(object.getBoolean("showBanner")) {
						Banner banner = new Banner();
						banner.photoUrl = object.getString("photo-url");
						banner.height = object.getJSONObject("dimensions").getInt("height");
						banner.width = object.getJSONObject("dimensions").getInt("width");
						banner.actionUrl = object.getString("url");
						mBanner = banner;

						// save banner data in persistently
						Editor editor = mHomePreferences.edit();
						editor.putString(PHOTO_URL_KEY, banner.photoUrl);
						editor.putInt(HEIGHT_KEY, banner.height);
						editor.putInt(WIDTH_KEY, banner.width);
						editor.putString(ACTION_URL_KEY, banner.actionUrl);
						editor.commit();
					} else {
						Editor editor = mHomePreferences.edit();
						editor.putString(PHOTO_URL_KEY, null);
						editor.commit();
					}
					MobileWebApi.sendSuccessMessage(uiHandler);
				}		
			}
		);
	}
	
	private static class Banner {
		int height;
		int width;
		String photoUrl;
		String actionUrl;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0, SETTINGS_MENU_ID, 0, "Settings")
		.setIcon(R.drawable.menu_settings);

		menu.add(0, ABOUT_MENU_ID     , 0, "About")
			.setIcon(R.drawable.menu_about);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		Intent intent;
		
		switch (item.getItemId()) {
			case ABOUT_MENU_ID:
				intent = new Intent(ctx, AboutActivity.class);
				startActivity(intent);
				return true;

			case SETTINGS_MENU_ID:
				intent = new Intent(ctx, MITSettingsActivity.class);
				startActivity(intent);
				return true;
		}

		return false;
	}
	
	public static void goHome(Context context) {
		Intent i = new Intent(context, HomeScreenActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(i);
	}
	
	@Override
	public synchronized void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		Log.d(TAG, "Preference changed: " + key);
		Context mContext = this;
		Handler uiHandler = new Handler();
		if (key.equalsIgnoreCase(Global.MIT_MOBILE_SERVER_KEY)) {
			Global.setMobileWebDomain(prefs.getString(Global.MIT_MOBILE_SERVER_KEY, null));

			// Update the version map any time the Mobile server is changed
			Global.getVersionInfo(mContext, uiHandler);
			Toast.makeText(this, "Mobile Web Domain set to " + Global.getMobileWebDomain(), Toast.LENGTH_SHORT).show();
		}
	}

}
