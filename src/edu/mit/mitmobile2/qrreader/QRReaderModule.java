package edu.mit.mitmobile2.qrreader;

import java.util.ArrayList;
import java.util.List;

import com.google.zxing.client.android.CaptureActivity;

import android.app.Activity;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class QRReaderModule extends NewModule {

	private static final String MENU_QR_HELP = "about";
	
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
	public List<MITMenuItem> getPrimaryOptions() {
		List<MITMenuItem> menuItems = new ArrayList<MITMenuItem>();
		menuItems.add(new MITMenuItem(MENU_QR_HELP, "", R.drawable.menu_info));
		return menuItems;
	}

	@Override
	public List<MITMenuItem> getSecondaryOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onItemSelected(Activity activity, String id) {
		if (id.equals(MENU_QR_HELP)) {
			CaptureActivity.helpDialog(activity).show();
			return true;
		}
		return false;
	}
}
