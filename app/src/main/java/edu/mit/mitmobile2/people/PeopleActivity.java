package edu.mit.mitmobile2.people;

import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class PeopleActivity extends MITModuleActivity {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setContentLayoutId(R.layout.content_people);
		super.onCreate(savedInstanceState);
	}

}
