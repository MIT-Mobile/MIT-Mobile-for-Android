package edu.mit.mitmobile2;

import android.view.View;

public interface ScreenInterface {

	public void updateView();

	public View getView();	

	public void onSelected();
	
	
	public void onDestroy();
	
}
