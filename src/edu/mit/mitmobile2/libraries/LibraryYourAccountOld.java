package edu.mit.mitmobile2.libraries;

import android.app.Activity;
import android.app.ActivityGroup;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TabActivityConfigurator;

public class LibraryYourAccountOld extends ActivityGroup {

	private static final String TAG = "LibraryYourAccount"; 
	protected TabHost tabHost;	
	protected Activity mActivity;		
	protected int ADD_NEW_TAB = Menu.FIRST;
	protected TabHost.TabSpec spec;
	protected int displayWidth;
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		mActivity = this;
		setContentView(R.layout.your_account_tab_layout);


		tabHost = (TabHost)findViewById(R.id.tabHost);  
		tabHost.setup(this.getLocalActivityManager());  // NEEDED!!!
		
		TabActivityConfigurator tabConfigurator = new TabActivityConfigurator(mActivity, tabHost);
		tabConfigurator.addTab("Loans", LibraryLoans.class);
		tabConfigurator.addTab("Fines", LibraryFines.class);
		tabConfigurator.addTab("Holds", LibraryHolds.class);
		
		tabConfigurator.configureTabs();

	}

	 public void myClickHandler(View v) 
	    {
	        Log.v(TAG, "insideMyHandler" );
	    }

	private void addTab(String label, int drawableId, Class className) {
		Intent intent = new Intent(this, className);
		
		LinearLayout indicatorView = new LinearLayout(mActivity);
		indicatorView.setLayoutParams(new LayoutParams((int)(displayWidth / 3), 72));
		indicatorView.setBackgroundResource(R.drawable.tab_background);
		indicatorView.setGravity(Gravity.CENTER);
		TextView textView = new TextView(mActivity);
		textView.setLayoutParams(new LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)));
		textView.setText(label);
		ColorStateList colors = mActivity.getResources().getColorStateList(R.color.tab_text_color);
		textView.setTextColor(colors);
		textView.setTextSize(mActivity.getResources().getDimensionPixelSize(R.dimen.tabTextSize));
		textView.setTypeface(Typeface.SANS_SERIF);
		indicatorView.addView(textView);		 
		spec.setIndicator(indicatorView);
		spec.setContent(intent);
		tabHost.addTab(spec);
		}
	
	
//	private static View createTabView(final Context context, final String text) {
//		View view = LayoutInflater.from(context).inflate(R.layout.tab_loans, null);
//		//TextView tv = (TextView) view.findViewById(R.id.tabsText);
//		//tv.setText(text);
//		return view;
//	}
}