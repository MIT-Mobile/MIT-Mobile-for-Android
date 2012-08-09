package edu.mit.mitmobile2.qrreader;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.mit.mitmobile2.DateStrings;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;

public class QRReaderHistoryActivity extends ModuleActivity {

	private QRCodeDB mQRCodeDB;
	private ArrayList<QRCode> mQRCodes;
	private QRCodeArrayAdapter mListAdapter;
	private ListView mHistoryListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		mQRCodeDB = QRCodeDB.getInstance(getApplicationContext());
		
		List<QRCode> savedQRCodes = mQRCodeDB.getQRCodes();
		mQRCodes = new ArrayList<QRCode>(savedQRCodes);
		mListAdapter = new QRCodeArrayAdapter(this, mQRCodes);
		
		setContentView(R.layout.qrreader_history);
		View noHistory = findViewById(R.id.qrreaderNoHistory);
		mHistoryListView = (ListView) findViewById(R.id.qrreaderMainHistoryLV);
		mHistoryListView.setAdapter(mListAdapter);
		mListAdapter.setOnItemClickListener(mHistoryListView,
			new SimpleArrayAdapter.OnItemClickListener<QRCode>() {
				@Override
				public void onItemSelected(QRCode item) {
					QRReaderDetailActivity.launch(QRReaderHistoryActivity.this, item);
				}
			}
		);
		
		if(mQRCodes.size() > 0) {
			noHistory.setVisibility(View.GONE);
			mHistoryListView.setVisibility(View.VISIBLE);
		}
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
			barcodeTitle.setText(item.getId());
			barcodeSubtitle.setText("Scanned " + DateStrings.agoString(item.getDate()));
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
		// TODO Auto-generated method stub
		
	}
}
