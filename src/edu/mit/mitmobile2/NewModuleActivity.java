package edu.mit.mitmobile2;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import edu.mit.mitmobile2.MITTitleBar.OnMITTitleBarListener;

public abstract class NewModuleActivity extends Activity {

	private MITTitleBar mTitleBar;
	private LinearLayout mMainLayout;
	private View mContentView;
	
	protected Global app;
	
	protected abstract NewModule getNewModule();
	protected abstract boolean isScrollable();
	protected abstract void onOptionSelected(String optionId);
	protected abstract boolean isModuleHomeActivity();
	protected abstract void prepareActivityOptionsMenu(Menu menu);
	
	protected static final int MENU_MAIN_GROUP = 0;
	protected static final int MENU_HOME = Menu.FIRST;
	protected static final int MENU_MODULE_HOME = MENU_HOME + 1;
	protected static final int MENU_SEARCH = MENU_MODULE_HOME + 1;
	protected static final String MENU_SEARCH_TITLE = "Search";
	
	
	
	public String getMobileWebDomain() {
		return app.getMobileWebDomain();
	}
	
	protected MITTitleBar getTitleBar() {
		return mTitleBar;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		app = (Global)getApplication();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case MENU_HOME: 
				MITNewsWidgetActivity.goHome(this);
				return true;
		
			case MENU_MODULE_HOME:
				Intent intent = new Intent(this, getNewModule().getModuleHomeActivity());
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
				
			case MENU_SEARCH:
				onSearchRequested();
				return true;
		}
			
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		menu.clear();
		
		menu.add(MENU_MAIN_GROUP, MENU_HOME, Menu.NONE, "Home")
			.setIcon(R.drawable.menu_home);
		if(!isModuleHomeActivity()) {
			menu.add(MENU_MAIN_GROUP, MENU_MODULE_HOME, Menu.NONE, getNewModule().getMenuOptionTitle())
				.setIcon(getNewModule().getMenuIconResourceId());
		}
		
		
		prepareActivityOptionsMenu(menu);
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	/**
	 * Use it to add TitleBar items by use {@link NewModuleActivity#addPrimaryMenuItem(List)
	 * , NewModuleActivity#addSecondaryMenuItem(List)}}
	 */
	protected void initTitleBar() {
		List<MITMenuItem> primaryItems = getPrimaryMenuItems();
		if (primaryItems != null) {
			for (MITMenuItem item : primaryItems) {
				mTitleBar.addPrimaryItem(item);
			}
		}
		List<MITMenuItem> secondaryItems = getSecondaryMenuItems();
		if (secondaryItems != null) {
			for (MITMenuItem item : secondaryItems) {
				mTitleBar.addPrimaryItem(item);
			}
		}
	}

	// default implementation for primary, and secondary menu items.
	protected List<MITMenuItem> getPrimaryMenuItems() {
		return getNewModule().getPrimaryOptions();
	}
	
	protected List<MITMenuItem> getSecondaryMenuItems() {
		return null;
	}

	
	protected void initContentView() {
		super.setContentView(R.layout.new_module_main);
		mMainLayout = (LinearLayout) findViewById(R.id.newModuleMain);
		mTitleBar = (MITTitleBar) findViewById(R.id.mitTitleBar);
		initViews();
		initTitleBar();
	}
	
	@Override
	public void setContentView(int layoutResID) {
		initContentView();
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContentView = inflater.inflate(layoutResID, null);
		mMainLayout.addView(mContentView);
	}
	
	protected void setContentView(View view, boolean fullRefresh) {
		if (fullRefresh || (mMainLayout == null)) {
			initContentView();			
		} 
		if (mContentView != null) {
			mMainLayout.removeViewAt(mMainLayout.getChildCount()-1);
		}
		mContentView = view;
		mMainLayout.addView(view);
	}
	
	private void initViews() {
		mTitleBar.setOnTitleBarListener(new OnMITTitleBarListener() {
			@Override
			public void onOptionItemSelected(String optionId) {
				onOptionSelected(optionId);
			}

			@Override
			public void onHomeSelected() {
				MITNewsWidgetActivity.goHome(NewModuleActivity.this);				
			}

			@Override
			public void onModuleHomeSelected() {
				Intent intent = new Intent(NewModuleActivity.this, getNewModule().getModuleHomeActivity());
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		mTitleBar.setTextForModuleBtn(getNewModule().getShortName());
		mTitleBar.setClickableForModuleBtn(isModuleHomeActivity());
	}
}
