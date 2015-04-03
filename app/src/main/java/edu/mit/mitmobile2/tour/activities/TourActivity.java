package edu.mit.mitmobile2.tour.activities;

import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TourActivity extends MITModuleActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setContentLayoutId(R.layout.content_tours);
		super.onCreate(savedInstanceState);
	}

}
