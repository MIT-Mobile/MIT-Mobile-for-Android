package edu.mit.mitmobile2.qrreader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.zxing.client.android.CaptureActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.mit.mitmobile2.DateStrings;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.qrreader.QRReaderModel.SuggestedUrl;

public class QRReaderMainActivity extends ModuleActivity {

	private static final String QRCODES_KEY = "qrcodes";
	private static final int MAXIMUM_SAVED_QR_CODES = 10;
	
	ArrayList<QRCode> mQRCodes;
	QRCodeArrayAdapter mListAdapter;
	QRCodeDB mQRCodeDB;
	
	ListView mHistoryListView;
	View mHelpView;
	FullScreenLoader mLoader;
	
	private Bitmap mBitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		mQRCodeDB = QRCodeDB.getInstance(getApplicationContext());
		
		List<QRCode> savedQRCodes = mQRCodeDB.getQRCodes();
		mQRCodes = new ArrayList<QRCode>(savedQRCodes);
		mListAdapter = new QRCodeArrayAdapter(this, mQRCodes);
		
		setContentView(R.layout.qrreader_main);
		mHelpView = findViewById(R.id.qrreaderHelpView);
		mHistoryListView = (ListView) findViewById(R.id.qrreaderMainHistoryLV);
		mHistoryListView.setAdapter(mListAdapter);
		mListAdapter.setOnItemClickListener(mHistoryListView,
			new SimpleArrayAdapter.OnItemClickListener<QRCode>() {
				@Override
				public void onItemSelected(QRCode item) {
					QRReaderDetailActivity.launch(QRReaderMainActivity.this, item);
				}
			}
		);
		
		mLoader = (FullScreenLoader) findViewById(R.id.qrreaderMainFullScreenLoader);
		
		updateView();		

		
		findViewById(R.id.qrreaderScanButton).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				launchScan();
			}
		});
	}
	
	private void updateView() {
		mListAdapter.notifyDataSetChanged();
		if(mQRCodes.size() > 0) {
			mHelpView.setVisibility(View.GONE);
			mHistoryListView.setVisibility(View.VISIBLE);
		}
	}
	
	private void launchScan() {
		Intent i = new Intent(this, com.google.zxing.client.android.CaptureActivity.class);
		i.setAction(com.google.zxing.client.android.Intents.Scan.ACTION);
		i.putExtra("SCAN_MODE", "ONE_D_QRCODE_MODE");
		startActivityForResult(i, 1);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(QRCODES_KEY, mQRCodes);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			
			byte[] bitmapBytes = extras.getByteArray(com.google.zxing.client.android.Intents.Scan.RESULT_BITMAP_BYTES);
			mBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
			String result = extras.getString(com.google.zxing.client.android.Intents.Scan.RESULT);
			
			reMapURL(result);
		}
	}
	
	private boolean isUrl(String result) {
		return result.matches("http:\\\\.*");
	}
	
	private void reMapURL(final String result) {
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				
				if (MobileWebApi.SUCCESS == msg.arg1) {
					SuggestedUrl suggested = (SuggestedUrl) msg.obj;
					String url = result;
					
					if (null == suggested) {
						return;
					}
					
					if (suggested.isSuccess && null != suggested.suggestedUrl) {
						url = suggested.suggestedUrl;
					}
					updateDBAndViews(url);
				} else {
					updateDBAndViews(result);
				}
			}
		};
		
		mLoader.setVisibility(View.VISIBLE);
		mLoader.showLoading();
		
		((QRReaderModule) getModule()).getModel().fetchSuggestedUrl(this, result, handler, isUrl(result));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	
		// always hide loading indicator when pausing
		// we don't care the loading indicator does not get reshown
		// when we resume
		mLoader.setVisibility(View.GONE);
		mLoader.stopLoading();
	}
	
	private void updateDBAndViews(String url) {
		QRCode qrcode = new QRCode(url, mBitmap, new Date(System.currentTimeMillis()));
		mQRCodes.add(0, qrcode);
		mQRCodeDB.insertQRCode(qrcode);
		if(mQRCodeDB.qrcodesCount() > MAXIMUM_SAVED_QR_CODES) {
			mQRCodeDB.removeOldestQRCode();
		}
		updateView();
			
		QRReaderDetailActivity.launch(QRReaderMainActivity.this, qrcode);
	}
	
	@Override
	protected Module getModule() {
		return new QRReaderModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return true;
	}

	static final int MENU_SCAN_QR = MENU_MODULE_HOME + 1;
	static final int MENU_QR_HELP = MENU_MODULE_HOME + 2;
	
	@Override
	protected void prepareActivityOptionsMenu(Menu menu) {
		menu.add(0, MENU_SCAN_QR, Menu.NONE, "Scan")
			.setIcon(R.drawable.menu_camera);
		menu.add(0, MENU_QR_HELP, Menu.NONE, "Help")
			.setIcon(R.drawable.menu_about);
	}
	
	/*****************************************************************************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_SCAN_QR:
				launchScan();
				return true;
				
			case MENU_QR_HELP:
				showDialog(DIALOG_QR_HELP);
				break;
		}
		return super.onOptionsItemSelected(item);
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
	private static class QRCodeArrayAdapter extends SimpleArrayAdapter<QRCode> {

		public QRCodeArrayAdapter(Context context, List<QRCode> items) {
			super(context, items, R.layout.qrreader_row);
		}

		@Override
		public void updateView(QRCode item, View view) {
			ImageView barcodeIV = (ImageView) view.findViewById(R.id.qrreaderRowIV);
			TextView barcodeTitle = (TextView) view.findViewById(R.id.qrreaderRowTitle);
			TextView barcodeSubtitle = (TextView) view.findViewById(R.id.qrreaderRowSubtitle);
			
			barcodeIV.setImageBitmap(item.getBitmap());
			barcodeTitle.setText(item.getUrl());
			barcodeSubtitle.setText(DateStrings.agoString(item.getDate()));
		}
	}
}
