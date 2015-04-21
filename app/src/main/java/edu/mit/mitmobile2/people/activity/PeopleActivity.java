package edu.mit.mitmobile2.people.activity;

import android.os.Bundle;

import edu.mit.mitmobile2.MITMainActivity;
import edu.mit.mitmobile2.R;

public class PeopleActivity extends MITMainActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentLayoutId(R.layout.fragment_people);
	}
}


