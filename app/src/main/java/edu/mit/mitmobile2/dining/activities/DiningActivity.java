package edu.mit.mitmobile2.dining.activities;

import edu.mit.mitmobile2.MITMainActivity;
import edu.mit.mitmobile2.R;

import android.os.Bundle;
import android.util.Log;

public class DiningActivity extends MITMainActivity {

    protected String mTitle = "Dining";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("ZZZ","DINING ACTIVITY");
		this.setContentLayoutId(R.layout.content_dining);
		super.onCreate(savedInstanceState);
	}

}
