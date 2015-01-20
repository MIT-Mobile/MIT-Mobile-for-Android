package edu.mit.mitmobile2.emergency;

import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class EmergencyActivity extends MITModuleActivity {

	int contentLayoutId = R.layout.content_emergency;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setContentLayoutId(R.layout.content_emergency);
		super.onCreate(savedInstanceState);
	}

}
