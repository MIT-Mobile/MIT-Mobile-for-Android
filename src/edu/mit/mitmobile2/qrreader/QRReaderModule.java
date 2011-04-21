package edu.mit.mitmobile2.qrreader;

import android.app.Activity;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;

public class QRReaderModule extends Module {

	@Override
	public String getShortName() {
		return "QR";
	}
	
	@Override
	public String getLongName() {
		return "QR Reader";
	}

	@Override
	public Class<? extends Activity> getModuleHomeActivity() {
		return QRReaderMainActivity.class;
	}

	@Override
	public int getMenuIconResourceId() {
		return R.drawable.menu_events;
	}

	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_events;
	}
}
