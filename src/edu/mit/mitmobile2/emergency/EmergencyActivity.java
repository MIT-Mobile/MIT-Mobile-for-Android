package edu.mit.mitmobile2.emergency;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.OnRefreshListener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.LoadingUIHelper;
import edu.mit.mitmobile2.MITMenuItem;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.alerts.C2DMReceiver;
import edu.mit.mitmobile2.objs.EmergencyItem;
import edu.mit.mitmobile2.objs.EmergencyItem.Contact;


public class EmergencyActivity extends NewModuleActivity implements OnRefreshListener {

	private ListView mListView;

	private Context mContext;
	private WebView mEmergencyMsgTV = null;
	private ImageView mEmergencyMsgLoader;
	private ImageView mEmergencyListLoader;
	private ScrollView mEmergencyScrollView;
	
	TextView emergencyContactsTV;

	SharedPreferences pref;

	private PullToRefreshAttacher mPullToRefreshAttacher;

	
	static EmergencyItem emergencyItem;
	
	static String PREF_KEY_EMERGENCY_TEXT = "emergency_text";
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		
		setContentView(R.layout.emergency);
		
		mEmergencyMsgLoader = (ImageView) findViewById(R.id.emergencyMsgLoader);
		mEmergencyListLoader = (ImageView) findViewById(R.id.emergencyListLoader);
		mEmergencyScrollView = (ScrollView) findViewById(R.id.emergencyMainScrollView);
		
		pref = this.getSharedPreferences(Global.PREFS,MODE_PRIVATE);  // FIXME
		
		mPullToRefreshAttacher = createPullToRefreshAttacher();
	    mPullToRefreshAttacher.setRefreshableView(mEmergencyScrollView, this);
	    
		getData();
		
	}

	private static String noticeTemplate = "<html><body style=\"padding: 7px;\">%s\n<p>Posted %s</p></body></html>";
	/****************************************************/
	private void updateEmergencyText() {
		emergencyItem = EmergencyParser.getStatus();

		if (emergencyItem==null) {
			emergencyItem = new EmergencyItem();
			Toast.makeText(mContext, "Sorry, showing cached info", Toast.LENGTH_LONG).show();
			emergencyItem.text = pref.getString(PREF_KEY_EMERGENCY_TEXT, "");
		} else {
			// Did version change?
			SharedPreferences.Editor editor = pref.edit();
			editor.putInt(Global.PREF_KEY_EMERGENCY_VERSION, Integer.valueOf(emergencyItem.version));
			editor.putString(PREF_KEY_EMERGENCY_TEXT, emergencyItem.text);
			editor.commit();
		}
		
		setTitle(emergencyItem.title);
		String html;
		
		mEmergencyMsgTV = (WebView) findViewById(R.id.emergencyMsgTV);
		mEmergencyMsgTV.setVisibility(View.VISIBLE);
		mEmergencyMsgLoader.setVisibility(View.GONE);
		
		if (emergencyItem.unixtime > 0) {
			Date postDate = new Date(emergencyItem.unixtime * 1000);
			SimpleDateFormat format = new SimpleDateFormat("EEE d, MMM yyyy");
			String dateStr = format.format(postDate);
			html = String.format(noticeTemplate, emergencyItem.text, dateStr);
		} else {
			html = emergencyItem.text;
		}
		
		mEmergencyMsgTV.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
	}
	
	private void updateView() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mListView = (ListView) findViewById(R.id.emergencyListView);
		mListView.setVisibility(View.VISIBLE);
		mEmergencyListLoader.setVisibility(View.GONE);
		//ImageView backgroundView = (ImageView) listHeader.findViewById(R.id.graySectionHeaderBackground);
		//int height = backgroundView.getDrawable().getIntrinsicHeight();
		//FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, height);
		//FrameLayout wrapper = (FrameLayout) listHeader.findViewById(R.id.graySectionHeaderWrapper);
		//wrapper.setLayoutParams(params);
		final EmergencyDB db = EmergencyDB.getInstance(this);
		
		EmergencyContactsAdapter adapter = null;
		adapter = new EmergencyContactsAdapter(this, db.getLimitedContactsCursor());

		final TwoLineActionRow moreContactsRow = (TwoLineActionRow) inflater.inflate(R.layout.boring_action_row, null);;
		moreContactsRow.setTitle("More Contacts");
		mListView.addFooterView(moreContactsRow);
		
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				if(view == moreContactsRow) {
					Intent intent = new Intent(mContext, EmergencyContactsActivity.class);
					startActivity(intent);
					return;
				}
				
				// not sure why position isn't the same row in this activity vs. emergencycontactsactivity
				Contact c = db.getContact(position - 1);
				String numericPhone = PhoneNumberUtils.convertKeypadLettersToDigits(c.phone);
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + numericPhone));
				startActivity(intent);
			}
		});
	}
	
	/****************************************************/
	void getData() {
		
		// TODO combine into single handler/runnable
		
		// this Handler will run on this thread (UI)
		final Handler uiHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				updateView();
			}
		};
		
		// TODO: don't wait for both parts to load before showing stuff to the user
		final Handler nextHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// mark emergency as read
				updateEmergencyText();
				C2DMReceiver.markNotificationAsRead(mContext, "emergencyinfo:");
			}
		};
		
		mEmergencyMsgLoader.setVisibility(View.VISIBLE);
		mEmergencyListLoader.setVisibility(View.VISIBLE);
		LoadingUIHelper.startLoadingImage(new Handler(), mEmergencyMsgLoader);
		LoadingUIHelper.startLoadingImage(new Handler(), mEmergencyListLoader);
		
		EmergencyParser.fetchStatus(this, nextHandler);
		EmergencyParser.fetchContacts(mContext, uiHandler);
	}
	
	void refresh() {
		
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				updateEmergencyText();
				mPullToRefreshAttacher.setRefreshComplete();
			}
		};

		EmergencyParser.refreshStatus(this, handler);
	}
	
	// default implementation for primary, and secondary menu items.
	@Override
	protected List<MITMenuItem> getPrimaryMenuItems() {
		return null;
	}
	
	@Override
	public boolean isModuleHomeActivity() {
		return true;
	}

	@Override
	protected NewModule getNewModule() {
		// TODO Auto-generated method stub
		return new EmergencyModule();
	}

	@Override
	protected boolean isScrollable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onOptionSelected(String optionId) { }

	@Override
	public void onRefreshStarted(View view) {
		refresh();		
	}
}
