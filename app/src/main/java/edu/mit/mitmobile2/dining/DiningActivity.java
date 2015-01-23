package edu.mit.mitmobile2.dining;

import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class DiningActivity extends MITModuleActivity {

    protected String mTitle = "Dining";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("ZZZ","DINING ACTIVITY");
		this.setContentLayoutId(R.layout.content_dining);
		super.onCreate(savedInstanceState);
	}

}
