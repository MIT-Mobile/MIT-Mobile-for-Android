package edu.mit.mitmobile2.libraries;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TabConfigurator;

public class LibraryYourAccount extends ActivityGroup {

	protected TabHost tabHost;	
	protected Activity mActivity;
	
		
	private int ADD_NEW_TAB = Menu.FIRST;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mActivity = this;
		setContentView(R.layout.library_your_account);

		TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
		tabHost.setup(this.getLocalActivityManager());  // NEEDED!!!
				
//				TabConfigurator tabConfigurator = new TabConfigurator(mActivity, tabHost);
//				tabConfigurator.addTab("Loans", R.id.tabLoans,LibraryLoans.class);
//				tabConfigurator.addTab("Holds", R.id.tabHolds,LibraryHolds.class);
//				tabConfigurator.addTab("Fines", R.id.tabFines,LibraryFines.class);
//				
//				tabConfigurator.configureTabs();
		
		Resources res = getResources(); // Resource object to get Drawables
		///TabHost tabHost1=(TabHost)findViewById(R.id.tabHost);
		tabHost.setup(this.getLocalActivityManager());
		TabHost.TabSpec spec = tabHost.newTabSpec("");  // Resusable TabSpec for each tab
		
		
		Intent intent;  // Reusable Intent for each tab
		ColorStateList colors = mActivity.getResources().getColorStateList(R.color.tab_text_color);
		
		// Tab Loans
		intent = new Intent().setClass(this, LibraryLoans.class);
		spec = tabHost.newTabSpec("Loans").setIndicator("Loans",res.getDrawable(R.drawable.ic_loans)).setContent(intent);
		tabHost.addTab(spec);

		//Tab Fines
		intent = new Intent().setClass(this, LibraryFines.class);
		spec = tabHost.newTabSpec("Fines").setIndicator("Fines",res.getDrawable(R.drawable.ic_fines)).setContent(intent);
		tabHost.addTab(spec);
				
		//Tab Holds
		intent = new Intent().setClass(this, LibraryHolds.class);
		spec = tabHost.newTabSpec("Holds").setIndicator("Holds",res.getDrawable(R.drawable.ic_holds)).setContent(intent);
		tabHost.addTab(spec);
		
	}

}