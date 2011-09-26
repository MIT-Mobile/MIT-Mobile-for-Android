package edu.mit.mitmobile2.libraries;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import edu.mit.mitmobile2.R;

public class LibraryYourAccount extends ActivityGroup {

		
	protected Activity mActivity;
	
		
	private int ADD_NEW_TAB = Menu.FIRST;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.library_your_account);
		
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
		tabHost.setup(this.getLocalActivityManager());
		TabHost.TabSpec spec = tabHost.newTabSpec("");  // Resusable TabSpec for each tab
		
		
		Intent intent;  // Reusable Intent for each tab
		
		
		// Tab Loans
		intent = new Intent().setClass(this, LibraryLoans.class);
		spec = tabHost.newTabSpec("Loans").setIndicator("Loans",res.getDrawable(R.drawable.ic_loans)).setContent(intent);
		spec.setContent(intent);
		tabHost.addTab(spec);
		
		//Tab Holds
		intent = new Intent().setClass(this, LibraryHolds.class);
		spec = tabHost.newTabSpec("Holds").setIndicator("Holds",res.getDrawable(R.drawable.ic_holds)).setContent(intent);
		spec.setContent(intent);
		tabHost.addTab(spec);
		
		//Tab Fines
		intent = new Intent().setClass(this, LibraryFines.class);
		spec = tabHost.newTabSpec("Fines").setIndicator("Fines",res.getDrawable(R.drawable.ic_fines)).setContent(intent);
		spec.setContent(intent);
		tabHost.addTab(spec);
	}

}