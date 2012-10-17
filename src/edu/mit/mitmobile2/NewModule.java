package edu.mit.mitmobile2;

import java.util.List;

import android.app.Activity;


public abstract class NewModule extends Module {
	public abstract List<MITMenuItem> getPrimaryOptions();
	
	public abstract List<MITMenuItem> getSecondaryOptions();
	
	public abstract boolean onItemSelected(Activity activity, String id);
}
