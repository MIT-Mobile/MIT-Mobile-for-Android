package edu.mit.mitmobile2.dining;

import java.util.Calendar;

import edu.mit.mitmobile2.dining.DiningModel.HouseDiningHall;

import android.content.Context;
import android.view.View;

public abstract class DiningScheduleScreen {

	public abstract View initializeView(Context context);
	
	public abstract Calendar getSelectedDate(HouseDiningHall house);
	
	public boolean titleBarHidden() {
		return false;
	}
	
	public void refreshScreen() { } // default implementation is empty
}
