package edu.mit.mitmobile2.qrreader;

import java.util.List;

import android.app.Activity;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class QRReaderModule extends NewModule {

	@Override
	public String getShortName() {
		return "QR Reader";
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
		return R.drawable.menu_qr;
	}

	@Override
	public int getHomeIconResourceId() {
		return R.drawable.home_qr;
	}

	@Override
	protected List<MITMenuItem> getPrimaryOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<MITMenuItem> getSecondaryOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean onItemSelected(Activity activity, String id) {
		// TODO Auto-generated method stub
		return false;
	}
}
