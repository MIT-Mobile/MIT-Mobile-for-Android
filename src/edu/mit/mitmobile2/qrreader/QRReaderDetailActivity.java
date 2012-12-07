package edu.mit.mitmobile2.qrreader;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.mit.mitmobile2.CommonActions;
import edu.mit.mitmobile2.DateStrings;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.MobileWebApi;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.SimpleArrayAdapter;
import edu.mit.mitmobile2.SimpleArrayAdapter.OnItemClickListener;
import edu.mit.mitmobile2.SpecialActions;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.qrreader.QRReaderModel.QRAction;
import edu.mit.mitmobile2.qrreader.QRReaderModel.SuggestedUrl;

public class QRReaderDetailActivity extends NewModuleActivity {

	public static void launch(Context context, QRCode qrcode) {
		Intent intent = new Intent(context, QRReaderDetailActivity.class);
		intent.putExtra(QRReaderDetailActivity.QRCODE_KEY, qrcode);
		context.startActivity(intent);
	}
	
	private static final String QRCODE_KEY = "qrcode";
	FullScreenLoader mLoader;
	SuggestedUrl mQRItem;
	Context mContext;
	
	boolean mShouldTimeout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mShouldTimeout = true;
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
		
		mContext = this;
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (MobileWebApi.SUCCESS == msg.arg1) {
					mShouldTimeout = false;
					mQRItem = (SuggestedUrl) msg.obj;
					if (null == mQRItem) {
						return;
					}
					
					if (mQRItem.isSuccess) {
						layoutDetailView();
						
					}
					
				} else {
					handleError(qrcode);
					layoutDetailView();
					
				}
			}
		}; // Handler
		
		new Handler().postDelayed(
				new Runnable() {
					@Override
					public void run() {
						if (mShouldTimeout) {
							handleError(qrcode);
							layoutDetailView();
						}
					}
				}, 
				15000
			);
		
		((QRReaderModule) getNewModule()).getModel().fetchSuggestedUrl(this, qrcode.getId(), handler);
		
	}
	
	private boolean isUrl(String result) {
		return result.matches("http:\\/\\/.*") || result.matches("https:\\/\\/.*") ;
	}
	
	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return new QRReaderModule();
	}

	@Override
	protected boolean isScrollable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
		
	}
	
	private void handleError(QRCode qrcode) {
		mQRItem = new SuggestedUrl();
		
		String urlString = qrcode.getId();
		if (isUrl(urlString)) {
			QRAction shareAction = new QRAction();
			shareAction.title = "Share URL";
			shareAction.payload = urlString;
			
			QRAction action = new QRAction();
			action.title = "Open URL";
			action.payload = urlString;
			
			mQRItem.type = "url";
			mQRItem.displayType = "URL";
			mQRItem.shareAction = shareAction;
			mQRItem.actions.add(action);
		} else {
			QRAction shareAction = new QRAction();
			shareAction.title = "Share data";
			shareAction.payload = urlString;
			
			mQRItem.type = "other";
			mQRItem.displayType = "Other";
			mQRItem.shareAction = shareAction;
			
		}
		
		
	}
	
	private void layoutDetailView() {
		TextView itemType = (TextView) findViewById(R.id.qrreaderDetailType);
		itemType.setText(mQRItem.displayType);
		
		ListView actionList = (ListView) findViewById(R.id.qrreaderActionLV);
		final ArrayList<QRAction> actions = new ArrayList<QRAction>();
		actions.addAll(mQRItem.actions);
		if (mQRItem.shareAction != null) {
			if (isUrl(mQRItem.shareAction.payload) && mQRItem.actions.size() == 0) {
				// if share action payload happens to be a url and there are no actions in list, create new action to open URL
				QRAction item = new QRAction();
				item.title = "Open URL";
				item.payload = mQRItem.shareAction.payload;
				actions.add(item);
			}
			actions.add(mQRItem.shareAction);
		}
		
		
		
		SimpleArrayAdapter<QRAction> adapter = new SimpleArrayAdapter<QRAction>(mContext, actions, R.layout.boring_action_row){
			@Override
			public void updateView(QRAction item, View view) {
				TwoLineActionRow row = (TwoLineActionRow) view;
				row.setTitle(item.title);
				if (item.equals(mQRItem.shareAction)) {
					row.setActionIconResource(R.drawable.action_share);
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
				if (position == actions.size() - 1) {
					// share is always last index
					// handle share action
					CommonActions.shareContent(QRReaderDetailActivity.this, "", "", item.payload);
				} else {
					// handle regular action
					CommonActions.doAction(QRReaderDetailActivity.this, item.payload);
				}
			}
		});
		
		actionList.setAdapter(adapter);
		
		mLoader.setVisibility(View.GONE);
	}

}
