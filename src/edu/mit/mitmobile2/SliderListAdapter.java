package edu.mit.mitmobile2;

import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.mitmobile2.SliderView.ScreenPosition;

import android.view.View;

/*
 * This class caches all the views for each screen (this is not ideal for memory performance)
 * This caching can be turned off, but may break semantics (so be careful when turning off caching)
 */
public class SliderListAdapter implements SliderView.Adapter {

	public static interface OnPositionChangedListener {
		void onPositionChanged(int newPosition, int oldPosition);
	}
	
	private OnPositionChangedListener mPositionChangedListener;
	
	
	int mPosition = -1;
	boolean mCacheScreens = true;
	
	ArrayList<SliderInterface> mSliderInterfaces = new ArrayList<SliderInterface>();
	HashMap<Integer, View> mScreens = new HashMap<Integer, View>();
	
	public void addScreen(SliderInterface sliderInterface) {
		mSliderInterfaces.add(sliderInterface);
		if (mPosition == -1) {
			mPosition = 0;
			if (mPositionChangedListener != null) {
				mPositionChangedListener.onPositionChanged(mPosition, -1);
			}
		}
	}
	
	public void setPosition(int position) {
		if (mPosition != position) {
			int oldPosition = mPosition;
			mPosition = position;
			mPositionChangedListener.onPositionChanged(position, oldPosition);
		}
	}
	
	public void setCacheScreens(boolean cacheScreens) {
		mCacheScreens = cacheScreens;
	}
	
	public void setOnPositionChangedListener(OnPositionChangedListener positionChangedListener) {
		mPositionChangedListener = positionChangedListener;
	}
	
	private Integer getScreenIndex(ScreenPosition position) {
		int index = -1;
		switch (position) {
			case Previous:
				index = mPosition-1;
				break;
			case Current:
				index = mPosition;
				break;
			case Next:
				index = mPosition+1;
				break;
		}
		if ((index >= 0) && (index < mSliderInterfaces.size())) {
			return index;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean hasScreen(ScreenPosition screenPosition) {
		return (getScreenIndex(screenPosition) != null);
	}

	public View getScreen(int position) {
		View view = mScreens.get(position);
		if (view == null) {
			view = mSliderInterfaces.get(position).getView();
			mScreens.put(position, view);
		}	
		return view;
	}
	
	@Override
	public View getScreen(ScreenPosition screenPosition) {
		Integer index = getScreenIndex(screenPosition);
		if (index == null) {
			return null;
		}
		
		View view;
		if (mCacheScreens) {
			view = mScreens.get(index);
			if (view == null) {
				view = mSliderInterfaces.get(index).getView();
				mScreens.put(index, view);
				mSliderInterfaces.get(index).updateView();
			}
		} else {
			view = mSliderInterfaces.get(index).getView();
			mSliderInterfaces.get(index).updateView();
		}
		return view;
	}

	@Override
	public void destroyScreen(ScreenPosition screenPosition) { 
		if (!mCacheScreens) {
			Integer index = getScreenIndex(screenPosition);
			if (index == null) {
				return;
			}
			mSliderInterfaces.get(index).onDestroy();
		}
	}
	
	@Override
	public void destroy() {
		if (mCacheScreens) {
			for(int key : mScreens.keySet()) {
				mSliderInterfaces.get(key).onDestroy();
			}
		}		
	}

	@Override
	public void seek(ScreenPosition screenPosition) {
		if (mSliderInterfaces.size() == 0) {
			// nothing to seek
			return;
		}
		
		int oldPosition = mPosition;
		if (screenPosition == ScreenPosition.Previous && mPosition > 0) {
			mPosition--;			
		} else if (screenPosition == ScreenPosition.Next && (mPosition+1) < mSliderInterfaces.size()) {
			mPosition++;
		} 
		
		if (oldPosition != mPosition || screenPosition == ScreenPosition.Current) {
			mSliderInterfaces.get(mPosition).onSelected();
		}
		if (oldPosition != mPosition) {
			if (mPositionChangedListener != null) {
				mPositionChangedListener.onPositionChanged(mPosition, oldPosition);
			}
		}
	}

	public void seekTo(int position) {
		mPosition = position;
	}
	
	public int getPosition() {
		return mPosition;
	}
		
	public int getScreenCount() {
		return mSliderInterfaces.size();
	}
}
