package edu.mit.mitmobile2.qrreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.R;

public class QRReaderModule extends Module {

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
