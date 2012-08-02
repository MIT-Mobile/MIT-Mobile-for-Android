package edu.mit.mitmobile2.qrreader;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;

public class QRReaderHelpActivity  extends ModuleActivity{

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrreader_help);
		
		PackageManager pm = this.getPackageManager();
		if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {					// add BANG to test camera-less device
			View noCameraText = findViewById(R.id.qrreaderHelpNoCameraText);	
			noCameraText.setVisibility(View.GONE);
			
		}
	}
	
	
	@Override
	protected Module getModule() {
		return new QRReaderModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		
	}

}
