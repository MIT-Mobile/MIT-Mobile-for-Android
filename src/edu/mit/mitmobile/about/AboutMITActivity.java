package edu.mit.mitmobile.about;

import edu.mit.mitmobile.MITNewsWidgetActivity;
import edu.mit.mitmobile.Module;
import edu.mit.mitmobile.ModuleActivity;
import edu.mit.mitmobile.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AboutMITActivity extends ModuleActivity {
	
	static final int MENU_HOME = Menu.FIRST;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_mit);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		
		menu.add(0, MENU_HOME, Menu.NONE, "Home")
		  .setIcon(R.drawable.menu_home);
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		
		switch (item.getItemId()) {
		case MENU_HOME:
			i = new Intent(this,MITNewsWidgetActivity.class);  
			startActivity(i);
			finish();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected Module getModule() {
		return new AboutModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) { }

}
