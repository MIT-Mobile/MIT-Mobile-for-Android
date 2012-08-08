package edu.mit.mitmobile2.qrreader;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.DateStrings;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.SimpleArrayAdapter.OnItemClickListener;
import edu.mit.mitmobile2.SpecialActions;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.qrreader.QRReaderModel.QRAction;
import edu.mit.mitmobile2.qrreader.QRReaderModel.SuggestedUrl;

public class QRReaderDetailActivity extends ModuleActivity {

	public static void launch(Context context, QRCode qrcode) {
		Intent intent = new Intent(context, QRReaderDetailActivity.class);
		intent.putExtra(QRReaderDetailActivity.QRCODE_KEY, qrcode);
		context.startActivity(intent);
	}
	
	private static final String QRCODE_KEY = "qrcode";
	FullScreenLoader mLoader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		final QRCode qrcode = getIntent().getParcelableExtra(QRCODE_KEY);
		
		setContentView(R.layout.qrreader_detail);
		ImageView qrcodeIV = (ImageView) findViewById(R.id.qrreaderDetailIV);
		qrcodeIV.setImageBitmap(qrcode.getBitmap());
		
		TextView qrcodeTV = (TextView) findViewById(R.id.qrreaderDetailContent);
		String urlTitle = SpecialActions.actionTitle(qrcode.getId());
		if(urlTitle != null) {
			qrcodeTV.setText(urlTitle);
		} else {
			qrcodeTV.setText(qrcode.getId());
		}
		
		TextView qrcodeDate = (TextView) findViewById(R.id.qrreaderDetailScanDate);
		qrcodeDate.setText("Scanned " + DateStrings.agoString(qrcode.getDate()));
		
		
		mLoader = (FullScreenLoader) findViewById(R.id.qrDetailLoader);
		mLoader.setVisibility(View.VISIBLE);
		mLoader.showLoading();
		
		final Context context = this;
		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				
				if (MobileWebApi.SUCCESS == msg.arg1) {
					SuggestedUrl qrItem = (SuggestedUrl) msg.obj;
					if (null == qrItem) {
						return;
					}
					
					if (qrItem.isSuccess) {
						
						TextView itemType = (TextView) findViewById(R.id.qrreaderDetailType);
						itemType.setText(qrItem.displayType);
						
						ListView actionList = (ListView) findViewById(R.id.qrreaderActionLV);
						final ArrayList<QRAction> actions = new ArrayList<QRAction>();
						
						if (qrItem.shareAction != null) {
							actions.add(0,qrItem.shareAction);
							if (isUrl(qrItem.shareAction.payload) && qrItem.actions == null) {
								// if share action payload happens to be a url and there are no actions in list, create new action to open URL
								QRAction item = new QRAction();
								item.title = "Open URL";
								item.payload = qrItem.shareAction.payload;
								actions.add(item);
							}
						}
						if (qrItem.actions != null) {
							actions.addAll(qrItem.actions);
						}
						
						SimpleArrayAdapter<QRAction> adapter = new SimpleArrayAdapter<QRAction>(context, actions, R.layout.boring_action_row){
							@Override
							public void updateView(QRAction item, View view) {
								TwoLineActionRow row = (TwoLineActionRow) view;
								row.setTitle(item.title);
								if (item.title.equals("Share URL")) {
									row.setActionIconResource(R.drawable.action_email);
								} else {
									row.setActionIconResource(R.drawable.action_external);
								}
							}
						};
						
						adapter.setOnItemClickListener(actionList, new OnItemClickListener<QRAction>(){

							@Override
							public void onItemSelected(QRAction item) {
								// TODO Auto-generated method stub
								int position = actions.indexOf(item);
								if (position == 0) {
									// share is always at position 0
									// handle share action
									CommonActions.shareContent(QRReaderDetailActivity.this, "", "", item.payload);
								} else {
									// handle regular action
									CommonActions.doAction(QRReaderDetailActivity.this, item.payload);
								}
							}
						});
						
						actionList.setAdapter(adapter);
					}
					mLoader.setVisibility(View.GONE);
					
				} // if SUCCESS
			}
		}; // Handler
		
		((QRReaderModule) getModule()).getModel().fetchSuggestedUrl(this, qrcode.getId(), handler);
		
	}
	
	private boolean isUrl(String result) {
		return result.matches("http:\\/\\/.*");
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
