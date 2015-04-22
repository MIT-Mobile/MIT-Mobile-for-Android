package edu.mit.mitmobile2.qrreader;

import edu.mit.mitmobile2.MITMainActivity;
import edu.mit.mitmobile2.R;

import android.os.Bundle;

public class QrreaderActivity extends MITMainActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setContentLayoutId(R.layout.content_qrreader);
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_qrreader);
	}

}
