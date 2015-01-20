package edu.mit.mitmobile2.shuttles;

import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ShuttlesActivity extends MITModuleActivity {
	
	int contentLayoutId = R.layout.content_shuttles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setContentLayoutId(R.layout.content_shuttles);
		super.onCreate(savedInstanceState);
	}

}
