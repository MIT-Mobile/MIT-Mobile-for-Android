package edu.mit.mitmobile2.qrreader;

import java.util.ArrayList;
import java.util.List;

import com.google.zxing.client.android.CaptureActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.R;

public class QRReaderModule extends NewModule {

	private static final String MENU_QR_HELP = "about";
	private static final String MENU_HISTORY = "history";
	
	@Override
	public String getShortName() {
		return "Scanner";
	}
	
	@Override
	public String getLongName() {
		return "Scanner";
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
		menuItems.add(new MITMenuItem(MENU_HISTORY, "history", R.drawable.action_history));
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
		} else if (id.equals(MENU_HISTORY)) {
			Intent intent = new Intent(activity, QRReaderHistoryActivity.class);
			activity.startActivity(intent);
			return true;
		}
		return false;
	}
	
	private QRReaderModel mModel;
	
	public QRReaderModel getModel() {
		if (null == mModel) {
			mModel = new QRReaderModel();
		}
		return mModel;
	}
	
	public void handleUrl(Context context, String url) {
		if (url.startsWith("mitmobile://qrreader/")) {
			Intent i = new Intent(context, QRReaderMainActivity.class);
			context.startActivity(i);
		}
		
	}
	
	
}
