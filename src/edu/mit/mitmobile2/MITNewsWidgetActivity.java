package edu.mit.mitmobile2;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import edu.mit.mitmobile2.qrreader.QRReaderModule;
import edu.mit.mitmobile2.about.AboutActivity;
import edu.mit.mitmobile2.alerts.NotificationsHelper;
import edu.mit.mitmobile2.classes.ClassesModule;
import edu.mit.mitmobile2.emergency.EmergencyModule;
import edu.mit.mitmobile2.events.EventsModule;
import edu.mit.mitmobile2.facilities.FacilitiesModule;
import edu.mit.mitmobile2.maps.MapsModule;
import edu.mit.mitmobile2.mit150.MIT150Module;
import edu.mit.mitmobile2.news.NewsDetailsActivity;
import edu.mit.mitmobile2.news.NewsHomeItem;
import edu.mit.mitmobile2.news.NewsListSliderActivity;
import edu.mit.mitmobile2.news.NewsModel;
import edu.mit.mitmobile2.news.NewsModule;
import edu.mit.mitmobile2.objs.NewsItem;
import edu.mit.mitmobile2.people.PeopleModule;
import edu.mit.mitmobile2.shuttles.ShuttlesModule;
import edu.mit.mitmobile2.tour.TourModule;

public class MITNewsWidgetActivity extends Activity implements OnSharedPreferenceChangeListener {
	
	private static final long TIMER_DELAY = 5000;
	
	private static final int ABOUT_MENU_ID = 0;
	private static final int MOBILE_WEB_MENU_ID = 1;

	Context ctx;
	
	private GridView mSpringBoard;
	private View mNewsWidget;
	private View mNewsWidgetPlaceHolder;
	private View mNewsWidgetFailed;
	private ImageView mNewsWidgetLoadingImage;
	private boolean mNewsLoadingFailed;
	
	Timer slideTimer = null;
	
	private static int SLIDE_RIGHT = 1;
	private static int SLIDE_LEFT = -1;
	private int direction = SLIDE_RIGHT;
	
	List<NewsItem> news;

	private SliderView mNewsSlider;

	public static final String TAG = "MITNewsWidgetActivity";
	private Global app;
	private SharedPreferences prefs;

	
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

		
		getData();
		
		NotificationsHelper.setupAlarmData(getApplicationContext());
	}

	/****************************************************/
	@Override
	protected void onResume() {
		super.onResume();
		scheduleSlideShow();
		
		boolean topTenStillFresh = new NewsModel(this).isTopTenFresh();
		if(mNewsLoadingFailed || !topTenStillFresh) {
			getData();
		}
		
		Log.d(TAG,"onResume()");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		cancelSlideShow();
		Log.d(TAG,"onPause()");
	}
	
	/****************************************************/
	void createView() {
	
		setContentView(R.layout.home);


		final View leftArrow = findViewById(R.id.topPortalShowingLeftIV);
		final View rightArrow = findViewById(R.id.topPortalShowingRightIV);
		
		
		mNewsSlider = (SliderView) findViewById(R.id.newsWidgetSlider);
		Display display = getWindowManager().getDefaultDisplay(); 
        mNewsSlider.setWidth(display.getWidth());
        mNewsSlider.setOnPositionChangedListener(
        	new SliderView.OnPositionChangedListener() {				
				@Override
				public void onPositionChanged(int newPosition, int oldPosition) {
					leftArrow.setEnabled(!mNewsSlider.isAtBeginning());
					
					rightArrow.setEnabled(!mNewsSlider.isAtEnd());
					
					scheduleSlideShow();
				}
			}
        );
        
        mNewsSlider.setOnTouchListener(
        	new View.OnTouchListener() {		
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(event.getAction() == MotionEvent.ACTION_UP) {
						scheduleSlideShow();
					} else {
						cancelSlideShow();
					}
					return false;
				}
			}
        );
        
        mNewsSlider.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ctx, NewsDetailsActivity.class);
				i.putExtra(NewsDetailsActivity.KEY_POSITION, mNewsSlider.getPosition());
				i.putExtra(NewsDetailsActivity.CATEGORY_ID_KEY, NewsModel.TOP_NEWS);
				startActivity(i);
			}
		});
		
        mNewsWidget = findViewById(R.id.newsWidget);
        mNewsWidgetPlaceHolder = findViewById(R.id.newsWidgetPlaceHolder);
        mNewsWidgetLoadingImage = (ImageView) findViewById(R.id.newsWidgetLoadingIndicator);
        mNewsWidgetFailed = findViewById(R.id.newsWidgetFailedToLoad);
        LoadingUIHelper.startLoadingImage(new Handler(), mNewsWidgetLoadingImage);
		
        
		// News Slideshow controls
		leftArrow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mNewsSlider.slideLeft();  
			}
		});

		rightArrow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mNewsSlider.slideRight();
			}
		});

		// more news button
		View.OnClickListener moreTopNewsClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ctx, NewsListSliderActivity.class);
				ctx.startActivity(intent);				
			}
		};
		findViewById(R.id.moreTopNewsButton).setOnClickListener(moreTopNewsClickListener);
		findViewById(R.id.homeTopNewsTV).setOnClickListener(moreTopNewsClickListener);
		
		Module[] modules = new Module[] {
			new NewsModule(),
			new ShuttlesModule(),
			new MapsModule(),
			new EventsModule(),
			new ClassesModule(),
			new PeopleModule(),
			new TourModule(),
			new EmergencyModule(),
			new FacilitiesModule(),
			new QRReaderModule(),
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
	}

	/****************************************************/

		
		
	final Runnable mUpdateSlideShow = new Runnable() {
		public void run() {
			if(mNewsSlider.isAtEnd() || mNewsSlider.isAtBeginning()) {
				// we need to switch directions
				if(direction == SLIDE_RIGHT) {
					direction = SLIDE_LEFT;
				} else if(direction == SLIDE_LEFT) {
					direction = SLIDE_RIGHT;
				}
			}
				
			if(direction == SLIDE_RIGHT) {
				mNewsSlider.slideRight();
			} else if(direction == SLIDE_LEFT) {
				mNewsSlider.slideLeft();
			}
				
			scheduleSlideShow();
		}
	};
		
	private TimerTask slideShowTimerTask() {
		// this Handler will run on this thread (UI)
		final Handler myHandler = new Handler();
		
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				if (news==null) return;
				myHandler.post(mUpdateSlideShow);
			}
		};		
		return timerTask;
	}
	
	
	void cancelSlideShow() {
		if(slideTimer != null) {
			slideTimer.cancel();
		}
		slideTimer = null;
	}
	/*
	 * This is called to reset the timer to 0
	 * or to initialize the timer first type
	 */
	void scheduleSlideShow() {
		cancelSlideShow();
		slideTimer = new Timer();
		slideTimer.schedule(slideShowTimerTask(), TIMER_DELAY);
	}
	
	/****************************************************/
	void getData() {
		
		final NewsModel newsModel = new NewsModel(ctx);

		final Runnable updateResultsUI = new Runnable() {
			public void run() {
				news = newsModel.getTopTen();
				mNewsSlider.clear();
				for(NewsItem newsItem : news) {
					NewsHomeItem newsHomeItem = new NewsHomeItem(newsItem, newsModel, MITNewsWidgetActivity.this);
					mNewsSlider.addScreen(newsHomeItem);					
				}
				mNewsSlider.setPosition(0);
				mNewsWidgetPlaceHolder.setVisibility(View.GONE);
				LoadingUIHelper.stopLoadingImage(new Handler(), mNewsWidgetLoadingImage);
				mNewsWidget.setVisibility(View.VISIBLE);
				scheduleSlideShow();
			}
		};
		
		// this Handler will run on this thread (UI)
		final Handler myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.arg1 == NewsModel.FETCH_SUCCESSFUL) {
					news = newsModel.getTopTen();
					mNewsLoadingFailed = false;
					post(updateResultsUI);
				} else {
					mNewsLoadingFailed = true;
					mNewsWidgetPlaceHolder.setVisibility(View.GONE);
					mNewsWidgetFailed.setVisibility(View.VISIBLE);
				}
			}
		};
		
		// show that we are loading
		mNewsWidget.setVisibility(View.GONE);
		mNewsWidgetPlaceHolder.setVisibility(View.VISIBLE);
		mNewsWidgetFailed.setVisibility(View.GONE);
		
		if(newsModel.isTopTenFresh()) {
			updateResultsUI.run();
		} else {
			newsModel.fetchCategory(NewsModel.TOP_NEWS, null, true, myHandler);
		}
	}

	/****************************************************/
	
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0, ABOUT_MENU_ID     , 0, "About")
			.setIcon(R.drawable.menu_about);
		
		menu.add(0, MOBILE_WEB_MENU_ID, 0, "Mobile Web")
			.setIcon(R.drawable.menu_mobile_web);
		
		return true;
	}
	
	public boolean onOptionsItemSelected (MenuItem item) {
		Intent intent;
		
		switch (item.getItemId()) {
			case ABOUT_MENU_ID:
				intent = new Intent(ctx, AboutActivity.class);
				startActivity(intent);
				return true;
			case MOBILE_WEB_MENU_ID:
				CommonActions.viewURL(ctx, "http://" + Global.getMobileWebDomain() + "/");
				return true;
		}

		return false;
	}
	
	public static void goHome(Context context) {
		Intent i = new Intent(context, MITNewsWidgetActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(i);
	}
	
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
