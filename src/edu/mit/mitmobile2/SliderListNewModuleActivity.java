package edu.mit.mitmobile2;

import java.util.ArrayList;

import edu.mit.mitmobile2.SliderListAdapter.OnPositionChangedListener;
import edu.mit.mitmobile2.SliderView.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public abstract class SliderListNewModuleActivity extends
		SliderNewModuleActivity implements OnPositionChangedListener {
	protected Context ctx;

	private ArrayList<String> mHeaderTitles = new ArrayList<String>();
	private SliderListAdapter mSliderListAdapter = new SliderListAdapter();
	private OnPositionChangedListener mOnPositionChangedListener;

	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null
				&& savedInstanceState.containsKey(KEY_POSITION_SAVED)) {
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
			return "" + (position + 1) + " of " + mHeaderTitles.size();
		} else {
			return "";
		}
	}

	protected void addScreen(SliderInterface sliderInterface, 
			String headerTitle) {
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

	protected View getScreen(int position) {
		Log.d("ZZZ", "screen position = " + position);
		return mSliderListAdapter.getScreen(position);
	}

	@Override
	protected void onNewIntent(Intent intent) {

		super.onNewIntent(intent);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			setPosition(extras.getInt(KEY_POSITION));
		}

	}

	@Override
	public void onPositionChanged(int newPosition, int oldPosition) {
		if (mOnPositionChangedListener != null) {
			mOnPositionChangedListener.onPositionChanged(newPosition,
					oldPosition);
		}
	}

	protected void setOnPositionChangedListener(
			SliderListAdapter.OnPositionChangedListener positionChangedListener) {
		mOnPositionChangedListener = positionChangedListener;
	}

	@Override
	protected Adapter getSliderAdapter() {
		return mSliderListAdapter;
	}

	@Override
	protected String getCurrentHeaderTitle() {
		return getHeaderTitle(getPosition());
	}

	protected void setScreenCaching(boolean cacheScreens) {
		mSliderListAdapter.setCacheScreens(cacheScreens);
	}
}
