package edu.mit.mitmobile2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public abstract class ModuleActivity extends Activity {
	
	protected static final int MENU_MAIN_GROUP = 0;
	protected static final int MENU_HOME = Menu.FIRST;
	protected static final int MENU_MODULE_HOME = MENU_HOME + 1;
	protected static final int MENU_SEARCH = MENU_MODULE_HOME + 1;
	protected static final String MENU_SEARCH_TITLE = "Search";
	protected Global app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		app = (Global)getApplication();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case MENU_HOME: 
				HomeScreenActivity.goHome(this);
				return true;
		
			case MENU_MODULE_HOME:
				Intent intent = new Intent(this, getModule().getModuleHomeActivity());
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
			menu.add(MENU_MAIN_GROUP, MENU_MODULE_HOME, Menu.NONE, getModule().getMenuOptionTitle())
				.setIcon(getModule().getMenuIconResourceId());
		}
		
		
		prepareActivityOptionsMenu(menu);
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	abstract protected Module getModule();
	
	abstract public boolean isModuleHomeActivity();
	
	abstract protected void prepareActivityOptionsMenu(Menu menu);
	
	@SuppressWarnings("static-access")
	public String getMobileWebDomain() {
		return app.getMobileWebDomain();
	}
}
