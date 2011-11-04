package edu.mit.mitmobile2;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class TabActivityConfigurator {
	private static String TAG = "TabActivityConfigurator"; 
	TabHost mTabHost;
	Activity mActivity;
	ArrayList<String> mTabNames;
	//ArrayList<Integer> mTabContentResourceIds;
	ArrayList<Class> mTabActivities;
	Intent mIntent;
	public TabActivityConfigurator(Activity activity, TabHost tabHost) {
		mTabHost = tabHost;
		mActivity = activity;
		mTabNames = new ArrayList<String>();
		mTabActivities = new ArrayList<Class>();
	}
	
	public void addTab(String tabName, Class className) {
		mTabNames.add(tabName);
		mTabActivities.add(className);
	}
	
	public void configureTabs() {
		int height = mActivity.getResources().getDimensionPixelSize(R.dimen.tabHeight);
		int displayWidth = mActivity.getWindowManager().getDefaultDisplay().getWidth();
		int width = displayWidth/mTabNames.size();
		
		int remainingWidth = displayWidth; // insure the whole width is used in cases where the division rounds off pixels
		
		for(int i = 0; i < mTabNames.size(); i++) {
			int tabWidth;
			if(i == mTabNames.size() - 1) {
				tabWidth = remainingWidth;
			} else {
				tabWidth = width;
				remainingWidth -= width;
			}
			String tabName = mTabNames.get(i);
			
			// create the content view for the tab
			// we use linearlayout as wrapper view (this is a work around for some strange bug
			// with using 9-patch backgrounds).  In turn we use 9-patch backgrounds because
			// could not get the builtin tab dividers to display
			LinearLayout indicatorView = new LinearLayout(mActivity);
			Log.d(TAG,"tabWidth = " + tabWidth + " height = " + height);
			indicatorView.setLayoutParams(new LayoutParams(tabWidth, height));
			indicatorView.setBackgroundResource(R.drawable.tab2_background);
			indicatorView.setGravity(Gravity.CENTER);
			TextView textView = new TextView(mActivity);
			textView.setLayoutParams(new LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)));
			textView.setText(tabName);
			ColorStateList colors = mActivity.getResources().getColorStateList(R.color.tab_text_color);
			textView.setTextColor(colors);
			textView.setTextSize(mActivity.getResources().getDimensionPixelSize(R.dimen.tabTextSize));
			textView.setTypeface(Typeface.SANS_SERIF);
			indicatorView.addView(textView);
			TabSpec tabSpec = mTabHost.newTabSpec(tabName);
			tabSpec.setIndicator(indicatorView);
			mIntent = new Intent().setClass(mActivity, mTabActivities.get(i));
			tabSpec.setContent(mIntent); // ERROR null until AFTER addTab?
			mTabHost.addTab(tabSpec);	
		}	
	}

}
