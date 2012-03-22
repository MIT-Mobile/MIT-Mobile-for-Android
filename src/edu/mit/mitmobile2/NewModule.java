package edu.mit.mitmobile2;

import java.util.List;

import android.app.Activity;


public abstract class NewModule extends Module {
	protected abstract List<MITMenuItem> getPrimaryOptions();
	
	protected abstract List<MITMenuItem> getSecondaryOptions();
	
	protected abstract boolean onItemSelected(Activity activity, String id);
}
