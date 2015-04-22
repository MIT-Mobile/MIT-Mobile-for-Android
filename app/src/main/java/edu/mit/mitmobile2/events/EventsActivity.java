package edu.mit.mitmobile2.events;

import edu.mit.mitmobile2.MITMainActivity;
import edu.mit.mitmobile2.R;

import android.os.Bundle;

public class EventsActivity extends MITMainActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setContentLayoutId(R.layout.content_events);
		super.onCreate(savedInstanceState);
	}

}
