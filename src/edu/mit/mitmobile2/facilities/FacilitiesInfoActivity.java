package edu.mit.mitmobile2.facilities;

import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.MITPlainSecondaryTitleBar;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;

//public class FacilitiesActivity extends ModuleActivity implements OnClickListener {
public class FacilitiesInfoActivity extends NewModuleActivity {

	SharedPreferences pref;
	
	public static final String TAG = "FacilitiesInfoActivity";
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate()");
		super.onCreate(savedInstanceState);
		createViews();
	}

	private void createViews() {
		setContentView(R.layout.facilities_info);
		addSecondaryTitle("Facilities");
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return new FacilitiesModule();
	}

	@Override
	protected boolean isScrollable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected List<MITMenuItem> getPrimaryMenuItems() {
		// TODO Auto-generated method stub
		return null;
	}
}
