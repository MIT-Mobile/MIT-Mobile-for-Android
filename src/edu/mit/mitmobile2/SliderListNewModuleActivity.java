package edu.mit.mitmobile2;

import java.util.ArrayList;

import edu.mit.mitmobile2.SliderListAdapter.OnPositionChangedListener;
import edu.mit.mitmobile2.SliderView.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public abstract class SliderListNewModuleActivity extends SliderNewModuleActivity implements OnPositionChangedListener {
	public static final String KEY_POSITION = "start_position";
	private static final String KEY_POSITION_SAVED = "saved_start_position";
	private int mLastSavedPosition = -1;
	
	protected Context ctx;

	private ArrayList<String> mHeaderTitles = new ArrayList<String>();
	private SliderListAdapter mSliderListAdapter = new SliderListAdapter();
	private OnPositionChangedListener mOnPositionChangedListener;
	
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
    		super.onCreate(savedInstanceState); 
    	    
    		if(savedInstanceState != null && savedInstanceState.containsKey(KEY_POSITION_SAVED)) {
    		    mLastSavedPosition = savedInstanceState.getInt(KEY_POSITION_SAVED);
    		}
    		
    		mSliderListAdapter.setOnPositionChangedListener(this);
    	}
    
    @Override 
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	outState.putInt(KEY_POSITION_SAVED, mSliderListAdapter.getPosition());
    	
    }
	
    protected String getHeaderTitle(int position) {
	if (mHeaderTitles.size() > 1) {
	    return "" + (position+1) + " of " + mHeaderTitles.size();
	} else {
	    return "";
	}
    }
    
	protected void addScreen(SliderInterface sliderInterface, String jumpTitle, String headerTitle) {
	    	mHeaderTitles.add(headerTitle);
	    	mSliderListAdapter.addScreen(sliderInterface);
	}

	protected void setPosition(int position) {
	    	mSliderListAdapter.seekTo(position);
		refreshScreens();
	}
	
	protected int getPosition() {
		return mSliderListAdapter.getPosition();
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
		return mSliderListAdapter.getScreen(position);
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
	public void onPositionChanged(int newPosition, int oldPosition) {
	    if (mOnPositionChangedListener != null) {
		mOnPositionChangedListener.onPositionChanged(newPosition, oldPosition);
	    }
	}
	
	protected void setOnPositionChangedListener(SliderListAdapter.OnPositionChangedListener positionChangedListener) {
	    mOnPositionChangedListener = positionChangedListener;
	}
	
	@Override
	protected Adapter getSliderAdapter() {
	    return mSliderListAdapter;
	}
	
	protected String getCurrentHeaderTitle() {
	    return getHeaderTitle(getPosition());
	}
}
