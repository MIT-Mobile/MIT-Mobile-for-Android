package edu.mit.mitmobile2.dining;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;

public class DiningMapActivity extends NewModuleActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dining_map);
		
	}
	
	@Override
	protected NewModule getNewModule() {
		return new DiningModule();
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}

	@Override
	protected List<String> getMenuItemBlackList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(DiningModule.FILTER_ITEM_ID);
		list.add(DiningModule.MAPVIEW_ITEM_ID);
		return list;
	}
	
	@Override
	protected void onOptionSelected(String optionId) {
		if (optionId.equals(DiningModule.LISTVIEW_ITEM_ID)) {
			Intent i = new Intent(this, DiningHomeActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		}
	}

	@Override
	protected boolean isModuleHomeActivity() {
		return false;
	}

}
