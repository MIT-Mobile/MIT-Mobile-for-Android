package edu.mit.mitmobile2;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import edu.mit.mitmobile2.MITSliderTitleBar.OnPreviousNextListener;
import edu.mit.mitmobile2.SliderView.Adapter;
import edu.mit.mitmobile2.SliderView.OnSeekListener;

public abstract class SliderNewModuleActivity extends NewModuleActivity {	
	protected Context ctx;
	
	private MITSliderTitleBar mSliderTitleBar;
	private SliderView mSliderView;
	
	abstract protected SliderView.Adapter getSliderAdapter();
	
	protected abstract String getCurrentHeaderTitle();
	
	protected void onSliderSeek() {
	    // default implementation does nothing
	}
	
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {

    		super.onCreate(savedInstanceState);
    		setContentView(R.layout.new_slider);
    		mSliderTitleBar = new MITSliderTitleBar(this);
    		getTitleBar().addSliderBar(mSliderTitleBar);
        
    		mSliderView = (SliderView) findViewById(R.id.newSliderMainContent);
        
    		ctx = this;
		
    		mSliderView.setOnSeekListener(new OnSeekListener() {
    		    @Override
    		    public void onSeek(SliderView view, Adapter adapter) {
    			mSliderTitleBar.enablePreviousButton(!view.isAtBeginning());
    			mSliderTitleBar.enableNextButton(!view.isAtEnd());
    			
    			if (!view.isAtEnd() || !view.isAtBeginning()) {
    			    mSliderTitleBar.showPreviousNext();
    			}
    			
    			mSliderTitleBar.setTitle(getCurrentHeaderTitle());
			
    			onSliderSeek();
    		    }
    		});
    		
    		mSliderView.setAdapter(getSliderAdapter());

    		mSliderTitleBar.setPreviousNextListener(new OnPreviousNextListener() {
			@Override
			public void onPreviousClicked() {
				mSliderView.slideLeft();
			}
			
			@Override
			public void onNextClicked() {
				mSliderView.slideRight();
			}
		});  
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
    
//    protected void useSubtitles(String mainTitle) {
//    	mSliderTitleBar.useSubtitleBar();
//    	mSliderTitleBar.setTitle(mainTitle);
//    }
	
	@Override
	protected void onResume() {
		super.onResume();
		
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
	
	protected void showLoading(String title) {
		FullScreenLoader loader = (FullScreenLoader) findViewById(R.id.newSliderActivityLoader);
		loader.setVisibility(View.VISIBLE);
		loader.showLoading();
		mSliderView.setVisibility(View.GONE);
//		mSliderTitleBar.setTitle(title);
	}
	
	protected void showLoadingError() {
		FullScreenLoader loader = (FullScreenLoader) findViewById(R.id.newSliderActivityLoader);
		loader.setVisibility(View.VISIBLE);
		loader.showError();
		mSliderView.setVisibility(View.GONE);
	}
	
	protected void showLoadingCompleted() {
		FullScreenLoader loader = (FullScreenLoader) findViewById(R.id.newSliderActivityLoader);
		loader.setVisibility(View.GONE);
		mSliderView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public final boolean isScrollable() {
	    return false;
	}
	
	public void refreshScreens() {
	    mSliderView.refreshScreens();
	}
}
