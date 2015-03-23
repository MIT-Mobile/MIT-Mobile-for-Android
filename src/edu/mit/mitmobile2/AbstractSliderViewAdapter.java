package edu.mit.mitmobile2;

import android.view.View;
import edu.mit.mitmobile2.SliderView.ScreenPosition;

public abstract class AbstractSliderViewAdapter implements SliderView.Adapter{

	private SliderInterface mPreviousInterface;
	private SliderInterface mCurrentInterface;
	private SliderInterface mNextInterface;

	private View mPreviousView;
	private View mCurrentView;
	private View mNextView;
	
	@Override
	public View getScreen(ScreenPosition screenPosition) {
		if (screenPosition == ScreenPosition.Next) {
			if (mNextView == null) {
				mNextInterface = getSliderInterface(screenPosition);
				mNextView = mNextInterface.getView();
			}
			return mNextView;
		} else if (screenPosition == ScreenPosition.Current) {
			if (mCurrentView == null) {
				mCurrentInterface = getSliderInterface(screenPosition);
				mCurrentView = mCurrentInterface.getView();
			}
			return mCurrentView;
		} else if (screenPosition == ScreenPosition.Previous) {
			if (mPreviousView == null) {
				mPreviousInterface = getSliderInterface(screenPosition);
				mPreviousView = mPreviousInterface.getView();
			}
			return mPreviousView;
		}
		throw new RuntimeException("Invalid screen position passed into adapter");
	}

	abstract public SliderInterface getSliderInterface(ScreenPosition screenPosition);
	
	@Override
	public void destroyScreen(ScreenPosition screenPosition) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void seek(ScreenPosition screenPosition) {
		if (screenPosition == ScreenPosition.Next) {
			mPreviousInterface = mCurrentInterface;
			mCurrentInterface = mNextInterface;
			mNextInterface = null;

			mPreviousView = mCurrentView;
			mCurrentView = mNextView;
			mNextView = null;			
			
		} else if (screenPosition == ScreenPosition.Previous) {
			mNextInterface = mCurrentInterface;
			mCurrentInterface = mPreviousInterface;
			mPreviousInterface = null;

			mNextView = mCurrentView;
			mCurrentView = mPreviousView;
			mPreviousView = null;	
		}
		
		mCurrentInterface.onSelected();
				
	}	

	@Override
	public void destroy() {
		
	}

}
