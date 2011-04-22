package edu.mit.mitmobile2.qrreader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SpecialActions;
import edu.mit.mitmobile2.TwoLineActionRow;

public class QRReaderDetailActivity extends ModuleActivity {

	public static void launch(Context context, QRCode qrcode) {
		Intent intent = new Intent(context, QRReaderDetailActivity.class);
		intent.putExtra(QRReaderDetailActivity.QRCODE_KEY, qrcode);
		context.startActivity(intent);
	}
	
	private static final String QRCODE_KEY = "qrcode";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		final QRCode qrcode = getIntent().getParcelableExtra(QRCODE_KEY);
		
		setContentView(R.layout.qrreader_detail);
		ImageView qrcodeIV = (ImageView) findViewById(R.id.qrreaderDetailIV);
		qrcodeIV.setImageBitmap(qrcode.getBitmap());
		
		TextView qrcodeTV = (TextView) findViewById(R.id.qrreaderDetailTV);
		String urlTitle = SpecialActions.actionTitle(qrcode.getUrl());
		if(urlTitle != null) {
			qrcodeTV.setText(urlTitle);
		} else {
			qrcodeTV.setText(qrcode.getUrl());
		}
		
		TwoLineActionRow openURLAction = (TwoLineActionRow) findViewById(R.id.qrreaderDetailOpenURL);
		openURLAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CommonActions.doAction(QRReaderDetailActivity.this, qrcode.getUrl());
			}
		});
		if(SpecialActions.actionSummary(qrcode.getUrl()) != null) {
			openURLAction.setTitle(SpecialActions.actionSummary(qrcode.getUrl()));
		}
		
		TwoLineActionRow shareURLAction = (TwoLineActionRow) findViewById(R.id.qrreaderDetailShareURL);
		shareURLAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CommonActions.shareContent(QRReaderDetailActivity.this, "", "", qrcode.getUrl());
			}
		});
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
