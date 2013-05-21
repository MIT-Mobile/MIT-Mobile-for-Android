package edu.mit.mitmobile2.dining;

import java.util.Calendar;

import android.content.Context;
import android.view.View;

public abstract class DiningScheduleScreen {

	public abstract View initializeView(Context context);
	
	public abstract Calendar getSelectedDate();
	
	public boolean titleBarHidden() {
		return false;
	}
}
