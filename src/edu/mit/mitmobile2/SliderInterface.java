package edu.mit.mitmobile2;

import android.view.View;

public interface SliderInterface {

	public void updateView();

	public View getView();	

	public void onSelected();
	
	public LockingScrollView getVerticalScrollView();
	
	public void onDestroy();
	
	//public void setPosition(int pos, int count);  // last should display "load more"
	
	// TODO make interface instead?  need "add more" method
	//public void setCallback(SliderActivity sa);
	
}
