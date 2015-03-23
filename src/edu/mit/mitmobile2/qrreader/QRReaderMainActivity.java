package edu.mit.mitmobile2.qrreader;

import java.util.Date;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.R;


public class QRReaderMainActivity extends NewModuleActivity {

	private static final int MAXIMUM_SAVED_QR_CODES = 10;
	
	QRCodeDB mQRCodeDB;
	
	ListView mHistoryListView;
	View mHelpView;
	
	private Bitmap mBitmap;
	
	 //private static final int MENU_QR_HELP = Menu.FIRST;
	
	private static final String LAUNCH_SCHEDULED_KEY = "launch_scheduled";
	private static final String FINISH_SCHEDULED_KEY = "finish_scheduled";
	
	private boolean mLaunchScanScheduled;
	private boolean mFinishScheduled;

	@SuppressWarnings("unused")
	private FullScreenLoader mLoader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mLoader = new FullScreenLoader(this, null);	
		setContentView(R.layout.camera_not_found);
		
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
	public boolean isModuleHomeActivity() {
		return true;
	}
	

	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return new QRReaderModule();
	}

	@Override
	protected boolean isScrollable() {
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) { }

	@Override
	protected void onResume() {
		super.onResume();
		PackageManager pm = this.getPackageManager();
		if (!(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))) {			// remove BANG to test camera-less device
			Log.d("camera","System does NOT have a camera!");
			mLaunchScanScheduled = false;
		} else {
			mQRCodeDB = QRCodeDB.getInstance(getApplicationContext());
			
			View noCamera = findViewById(R.id.NoCameraLayout);
			noCamera.setVisibility(View.GONE);
		}
		
		
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
}
