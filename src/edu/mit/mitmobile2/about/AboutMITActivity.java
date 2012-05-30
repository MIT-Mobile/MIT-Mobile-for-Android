package edu.mit.mitmobile2.about;

import edu.mit.mitmobile2.HomeScreenActivity;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AboutMITActivity extends NewModuleActivity {
	
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
			i = new Intent(this,HomeScreenActivity.class);  
			startActivity(i);
			finish();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected NewModule getNewModule() {
		return new AboutModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected boolean isScrollable() {
		return true;
	}

	@Override
	protected void onOptionSelected(String optionId) { }
}
