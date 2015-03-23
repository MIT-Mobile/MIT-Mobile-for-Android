package edu.mit.mitmobile2;

import android.view.MotionEvent;
import android.view.View;

public class HighlightEffects {
	
	/**
	 * The whole purpose of this method is to override the behavior of list views
	 * it's a hack to get around the fact that the ListView do not treat
	 * transparent backgrounds properly
	 * 
	 * @param view the view which is not show highlighting effects when touched
	 */
	public static void turnOffHighlightingEffects(final View view) {
		
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
	}
	
	
	public static void restoreDefaultHighlightingEffects(final View view) {
		
		view.setOnTouchListener(null);
	}
}
