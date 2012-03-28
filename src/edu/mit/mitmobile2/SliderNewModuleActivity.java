package edu.mit.mitmobile2;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import edu.mit.mitmobile2.MITSliderTitleBar.OnSlideToListener;

public abstract class SliderNewModuleActivity extends NewModuleActivity {
	public static final String KEY_POSITION = "start_position";
	private static final String KEY_POSITION_SAVED = "saved_start_position";
	private int mLastSavedPosition = -1;
	public static String TAG = "SliderActivity";
	
	protected Context ctx;

	private ArrayList<String> headerTitles = new ArrayList<String>();
	
	protected GestureDetector mFlingDetector;

	protected ImageView overlayLeftIV,overlayRightIV;
	protected TextView overlayTitleTV;
	
	private MITSliderTitleBar mSliderTitleBar;
	protected SliderView mSliderView;
	
	public Boolean mWasRotated;
	
	
	private SliderView.OnPositionChangedListener mSliderActivityPositionChangedListener = null;
	
	
	/****************************************************/
    @Override
	protected void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
        setContentView(R.layout.new_slider);
        mSliderTitleBar = new MITSliderTitleBar(this);
        getTitleBar().addSliderBar(mSliderTitleBar);
        
        mSliderView = (SliderView) findViewById(R.id.newsliderMainContent);
        
		ctx = this;
		
		mWasRotated = (Boolean) getLastNonConfigurationInstance();
        
        Display display = getWindowManager().getDefaultDisplay(); 
        mSliderView.setWidth(display.getWidth());

        mSliderView.setOnPositionChangedListener(new SliderView.OnPositionChangedListener() {
			@Override
			public void onPositionChanged(int newPosition, int oldPosition) {
				mSliderTitleBar.enablePreviousButton(!mSliderView.isAtBeginning());
				mSliderTitleBar.enableNextButton(!mSliderView.isAtEnd());
				mSliderTitleBar.setTitle(headerTitles.get(newPosition));
				
				if(mSliderActivityPositionChangedListener != null) {
					mSliderActivityPositionChangedListener.onPositionChanged(newPosition, oldPosition);
				}
			}       	
        });
        
        mSliderTitleBar.setSlideListener(new OnSlideToListener() {
			@Override
			public void onSlideToPrevious() {
				// TODO Auto-generated method stub
				mSliderView.slideLeft();
			}
			
			@Override
			public void onSlideToNext() {
				// TODO Auto-generated method stub
				mSliderView.slideRight();
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
    
//    protected void useSubtitles(String mainTitle) {
//    	mSliderTitleBar.useSubtitleBar();
//    	mSliderTitleBar.setTitle(mainTitle);
//    }
    
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
		headerTitles.add(headerTitle);
		mSliderView.addScreen(sliderInterface);
		
		if(mSliderView.getScreenCount() > 1) {
			mSliderTitleBar.showPreviousNext();
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
		FullScreenLoader loader = (FullScreenLoader) findViewById(R.id.newsliderActivityLoader);
		loader.setVisibility(View.VISIBLE);
		loader.showLoading();
		mSliderView.setVisibility(View.GONE);
//		mSliderTitleBar.setTitle(title);
	}
	
	protected void showLoadingError() {
		FullScreenLoader loader = (FullScreenLoader) findViewById(R.id.newsliderActivityLoader);
		loader.setVisibility(View.VISIBLE);
		loader.showError();
		mSliderView.setVisibility(View.GONE);
	}
	
	protected void showLoadingCompleted() {
		FullScreenLoader loader = (FullScreenLoader) findViewById(R.id.newsliderActivityLoader);
		loader.setVisibility(View.GONE);
		mSliderView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public final boolean isScrollable() {
	    return false;
	}
}
