package edu.mit.mitmobile2.qrreader;

import java.util.Date;

import com.google.zxing.client.android.CaptureActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TitleBar;
import edu.mit.mitmobile2.qrreader.QRReaderModel.SuggestedUrl;

public class QRReaderMainActivity extends ModuleActivity {

	private static final int MAXIMUM_SAVED_QR_CODES = 10;
	
	QRCodeDB mQRCodeDB;
	
	ListView mHistoryListView;
	View mHelpView;
	FullScreenLoader mLoader;
	
	private Bitmap mBitmap;
	
	private static final String LAUNCH_SCHEDULED_KEY = "launch_scheduled";
	private static final String FINISH_SCHEDULED_KEY = "finish_scheduled";
	
	private boolean mLaunchScanScheduled;
	private boolean mFinishScheduled;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.boring_activity_layout);
		LinearLayout root = (LinearLayout) findViewById(R.id.boringLayoutRoot);
		TitleBar titleBar = (TitleBar) findViewById(R.id.boringLayoutTitleBar);
		titleBar.setTitle("Scanner");
			
		mLoader = new FullScreenLoader(this, null);	
		root.addView(mLoader, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		mQRCodeDB = QRCodeDB.getInstance(getApplicationContext());	
		
		mLaunchScanScheduled = true;
		mFinishScheduled = false;
	}
	
	@Override
	protected void onRestoreInstanceState (Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mLaunchScanScheduled = savedInstanceState.getBoolean(LAUNCH_SCHEDULED_KEY, true);
		mFinishScheduled = savedInstanceState.getBoolean(FINISH_SCHEDULED_KEY, false);		
	}
	
	@Override
	protected void onSaveInstanceState (Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(LAUNCH_SCHEDULED_KEY, mLaunchScanScheduled);
		outState.putBoolean(FINISH_SCHEDULED_KEY, mFinishScheduled);
	}
	
	private void launchScan() {
		Intent i = new Intent(this, com.google.zxing.client.android.CaptureActivity.class);
		i.setAction(com.google.zxing.client.android.Intents.Scan.ACTION);
		i.putExtra("SCAN_MODE", "ONE_D_QRCODE_MODE");
		i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivityForResult(i, 1);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			
			byte[] bitmapBytes = extras.getByteArray(com.google.zxing.client.android.Intents.Scan.RESULT_BITMAP_BYTES);
			mBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
			String result = extras.getString(com.google.zxing.client.android.Intents.Scan.RESULT);
			
			QRCode qrcode = updateDB(result);
			QRReaderDetailActivity.launch(QRReaderMainActivity.this, qrcode);
			mLaunchScanScheduled = false;
			mFinishScheduled = false;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (mFinishScheduled) {
			finish();
			return;
		}
		
		if (mLaunchScanScheduled) {
			mLaunchScanScheduled = false;
			mFinishScheduled = true;
			launchScan();
		}
		mLaunchScanScheduled = true;
	}
	
	protected void onBackPresed() {
		mFinishScheduled = true;
		super.onBackPressed();
	}
	
	private QRCode updateDB(String url) {
		QRCode qrcode = new QRCode(url, mBitmap, new Date(System.currentTimeMillis()));
		mQRCodeDB.insertQRCode(qrcode);
		if(mQRCodeDB.qrcodesCount() > MAXIMUM_SAVED_QR_CODES) {
			mQRCodeDB.removeOldestQRCode();
		}
		return qrcode;
	}
	
	@Override
	protected Module getModule() {
		return new QRReaderModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return true;
	}
	
	private final static int DIALOG_QR_HELP = 1;
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_QR_HELP:
			return CaptureActivity.helpDialog(this);
		}
		return null;
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) { } // TODO Auto-generated method stub
}
