package edu.mit.mitmobile2;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

abstract public class SliderActivity extends ModuleActivity {
	public static final String KEY_POSITION = "start_position";
	private static final String KEY_POSITION_SAVED = "saved_start_position";
	private int mLastSavedPosition = -1;
	
	protected Context ctx;

	private ArrayList<String> jumpTitles = new ArrayList<String>();
	private ArrayList<String> headerTitles = new ArrayList<String>();
	
	protected GestureDetector mFlingDetector;

	protected ImageView overlayLeftIV,overlayRightIV;
	protected TextView overlayTitleTV;
	
	protected SliderView mSliderView;
	protected TitleBar mSliderTitleBar;
	
	public Boolean mWasRotated;
	
	private String mJumpTitle = null;
	private int mJumpMenuIconId = -1;
	
	static final int MENU_JUMP = MENU_SEARCH+1;
	protected final static int MENU_LAST = MENU_JUMP+1;
	
	static final int SCROLL_DURATION_PER_SCREEN = 250;
	static final int SCROLL_MAX_DURATION = 2500;
	
	private SliderView.OnPositionChangedListener mSliderActivityPositionChangedListener = null;
	
	public void setJumpTitle(String jumpTitle, int jumpMenuIconId) {
		mJumpTitle = jumpTitle;
		mJumpMenuIconId = jumpMenuIconId;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case MENU_JUMP: 
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				
				if(!mSliderView.isAnimatingScroll()) {
					
					builder.setTitle(mJumpTitle);
					
					String[] titlesArray = new String[jumpTitles.size()];
					jumpTitles.toArray(titlesArray);
					
					builder.setItems(titlesArray, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							mSliderView.snapToPosition(item);
						}
					});
					
					AlertDialog alert = builder.create();
					alert.show();
					alert.getListView().setSelection(mSliderView.getPosition());
					
				} else {
					builder.setTitle("Too Fast!");
					builder.setMessage("Please wait for quick jump to complete first.");
					builder.setNeutralButton("OK", null);
					builder.create().show();
				}
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	protected void prepareJumpOptionsMenu(Menu menu) {
		if(mJumpTitle != null && mSliderView.getScreenCount() > 1) {
			menu.add(MENU_MAIN_GROUP, MENU_JUMP, Menu.NONE, mJumpTitle)
				.setIcon(mJumpMenuIconId);
		}
	}
	
	/****************************************************/
    @Override
	protected void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);

        setContentView(R.layout.slider);
        
        mSliderView = (SliderView) findViewById(R.id.sliderMainContent);
        
        mSliderTitleBar = (TitleBar) findViewById(R.id.sliderActivityTitleBar);
    	
		ctx = this;
		
		mWasRotated = (Boolean) getLastNonConfigurationInstance();
        
        Display display = getWindowManager().getDefaultDisplay(); 
        mSliderView.setWidth(display.getWidth());

        mSliderView.setOnPositionChangedListener(new SliderView.OnPositionChangedListener() {
			@Override
			public void onPositionChanged(int newPosition, int oldPosition) {
				mSliderTitleBar.setLeftArrowEnabled(!mSliderView.isAtBeginning());
				
				mSliderTitleBar.setRightArrowEnable(!mSliderView.isAtEnd());
				
				if(!mSliderTitleBar.getHasSubtitle()) {
					mSliderTitleBar.setTitle(headerTitles.get(newPosition));
				} else {
					mSliderTitleBar.setSubtitle(headerTitles.get(newPosition));
				}
				
				if(mSliderActivityPositionChangedListener != null) {
					mSliderActivityPositionChangedListener.onPositionChanged(newPosition, oldPosition);
				}
			}       	
        });
        
        mSliderTitleBar.setOnArrowListener(new TitleBar.OnArrowListener() {
			@Override
			public void onRightArrow() {
				mSliderView.slideRight();				
			}
			
			@Override
			public void onLeftArrow() {
				mSliderView.slideLeft();
			}
		});  
        
        if(savedInstanceState != null && savedInstanceState.containsKey(KEY_POSITION_SAVED)) {
        	mLastSavedPosition = savedInstanceState.getInt(KEY_POSITION_SAVED);
        }
    }
	
    @Override 
    public boolean dispatchKeyEvent(KeyEvent event) {
    	if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
    		mSliderView.slideLeft();
    		return true;
    	}
    	
    	if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
    		mSliderView.slideRight();
    		return true;
    	}
    	
    	return super.dispatchKeyEvent(event);
    }
    
    @Override 
    protected void onDestroy() {
    	mSliderView.destroy();
    	System.gc();
    	super.onDestroy();
    }
    
    @Override 
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	outState.putInt(KEY_POSITION_SAVED, mSliderView.getPosition());
    	
    }
    
    protected void useSubtitles(String mainTitle) {
    	mSliderTitleBar.useSubtitleBar();
    	mSliderTitleBar.setTitle(mainTitle);
    }
    
	@Override
	public Object onRetainNonConfigurationInstance() {
	    final Boolean rotated = new Boolean(true);  // TODO may need to confirm
	    return rotated;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
        Display display = getWindowManager().getDefaultDisplay(); 
        mSliderView.onWidthChanged(display.getWidth());		
	}
	
	protected void addScreen(SliderInterface sliderInterface, String jumpTitle, String headerTitle) {
		jumpTitles.add(jumpTitle);
		headerTitles.add(headerTitle);
		mSliderView.addScreen(sliderInterface);
		
		if(mSliderView.getScreenCount() > 1) {
			mSliderTitleBar.showArrows();
		}
	}
	
	public void freezeScroll() {
		mSliderView.freezeScroll();
	}
	
	public void unfreezeScroll() {
		mSliderView.unfreezeScroll();		
	}

	protected void setPosition(int position) {
		mSliderView.setPosition(position);
	}
	
	protected int getPosition() {
		return mSliderView.getPosition();
	}
	
	protected int getPositionValue() {
		if(mLastSavedPosition > 0) {
			return mLastSavedPosition;
		}
		
		Bundle extras = getIntent().getExtras();
		
		if (extras != null){
			return extras.getInt(KEY_POSITION);
		} else {
			return 0;
		}
	}
	
	protected View getScreen(int position) {
		return mSliderView.getScreen(position);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		
		super.onNewIntent(intent);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			setPosition(extras.getInt(KEY_POSITION));
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		mSliderView.start();
		
		// not sure why, but sometimes when activities
		// are resumed it scrolls to the previous FrameLayout
		// this is a fix for that
		final int scrollX = mSliderView.getScrollX();
		final int scrollY = mSliderView.getScrollY();
		
		new Handler().postAtFrontOfQueue(new Runnable() {
			@Override
			public void run() {
				mSliderView.scrollTo(scrollX, scrollY);
			}
		});

		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		mSliderView.stop();
	}
	
	protected void setOnPositionChangedListener(SliderView.OnPositionChangedListener positionChangedListener) {
		mSliderActivityPositionChangedListener = positionChangedListener;
	}
	
	protected void showLoading(String title) {
		FullScreenLoader loader = (FullScreenLoader) findViewById(R.id.sliderActivityLoader);
		loader.setVisibility(View.VISIBLE);
		loader.showLoading();
		mSliderView.setVisibility(View.GONE);
		mSliderTitleBar.setTitle(title);
	}
	
	protected void showLoadingError() {
		FullScreenLoader loader = (FullScreenLoader) findViewById(R.id.sliderActivityLoader);
		loader.setVisibility(View.VISIBLE);
		loader.showError();
		mSliderView.setVisibility(View.GONE);
	}
	
	protected void showLoadingCompleted() {
		FullScreenLoader loader = (FullScreenLoader) findViewById(R.id.sliderActivityLoader);
		loader.setVisibility(View.GONE);
		mSliderView.setVisibility(View.VISIBLE);
	}
}
