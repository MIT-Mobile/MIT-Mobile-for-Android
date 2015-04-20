package edu.mit.mitmobile2.people.activity;

import android.os.Bundle;

import edu.mit.mitmobile2.MITModuleActivity;
import edu.mit.mitmobile2.R;

public class PeopleActivity extends MITModuleActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentLayoutId(R.layout.fragment_people);
	}
}


