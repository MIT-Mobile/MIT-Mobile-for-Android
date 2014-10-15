package edu.mit.mitmobile2.emergency;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import android.widget.Toast;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.alerts.C2DMReceiver;
import edu.mit.mitmobile2.objs.EmergencyItem;
import edu.mit.mitmobile2.objs.EmergencyItem.Contact;


public class EmergencyActivity extends NewModuleActivity implements OnRefreshListener {
	
	FullScreenLoader mLoadingView;
	static EmergencyItem emergencyItem;
	private WebView mEmergencyMsgTV = null;
	SharedPreferences pref;
	private Context mContext;
	private PullToRefreshAttacher msgPullToRefreshAttacher;
	private PullToRefreshAttacher contactsPullToRefreshAttacher;
	
	private ListView contactsListView;
	private ImageView emergencyMsgLoader;
	private ImageView emergencyListLoader;
	
	static String PREF_KEY_EMERGENCY_TEXT = "emergency_text";
	private static String noticeTemplate = "<html><body style=\"padding: 7px;\">%s\n<p>Posted %s</p></body></html>";
	private Handler uiContactsHandler;
	private Handler uiStatusHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.emergency);		
		setTitle("Emergeny Contacts");
		
		emergencyMsgLoader = (ImageView)findViewById(R.id.emergencyMsgLoader);
		emergencyListLoader = (ImageView)findViewById(R.id.emergencyListLoader);
		mEmergencyMsgTV = (WebView) findViewById(R.id.emergencyMsgTV);
		contactsListView = (ListView) findViewById(R.id.emergencyContactListView);

		pref = this.getSharedPreferences(Global.PREFS,MODE_PRIVATE);  

		uiContactsHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				updateContacts();
			}
		};

		uiStatusHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				updateEmergencyText();
				C2DMReceiver.markNotificationAsRead(mContext, "emergencyinfo:");
			}
		};

		msgPullToRefreshAttacher = createPullToRefreshAttacher();
		contactsPullToRefreshAttacher = createPullToRefreshAttacher();

	    msgPullToRefreshAttacher.setRefreshableView(mEmergencyMsgTV, this);
	    contactsPullToRefreshAttacher.setRefreshableView(contactsListView, this);
	    
		msgPullToRefreshAttacher.setEnabled(false);
	    contactsPullToRefreshAttacher.setEnabled(false);

		emergencyMsgLoader.setVisibility(View.VISIBLE);
		emergencyListLoader.setVisibility(View.VISIBLE);
	    
		EmergencyParser.fetchStatus(this, uiStatusHandler);
		EmergencyParser.fetchContacts(this, uiContactsHandler);
	}
	
	private void updateContacts() {	
		emergencyListLoader.setVisibility(View.GONE);
		
		contactsListView.setVisibility(View.VISIBLE);
		final EmergencyDB db = EmergencyDB.getInstance(this);
		EmergencyContactsAdapter adapter = new EmergencyContactsAdapter(this, db.getLimitedContactsCursor());

		contactsListView.setAdapter(adapter);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final TwoLineActionRow moreContactsRow = (TwoLineActionRow) inflater.inflate(R.layout.boring_action_row, null);;
		moreContactsRow.setTitle("More Contacts");
		contactsListView.addFooterView(moreContactsRow);

		contactsListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
									
					if(view == moreContactsRow) {
						Intent intent = new Intent(mContext, EmergencyContactsActivity.class);
						startActivity(intent);
						return;
					}
					
					Contact c = db.getContact(position);
					String numericPhone = PhoneNumberUtils.convertKeypadLettersToDigits(c.phone);
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + numericPhone));
					startActivity(intent);
				}
		});
		
	}

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
		
		mEmergencyMsgTV.setVisibility(View.VISIBLE);
		
		if (emergencyItem.unixtime > 0) {
			Date postDate = new Date(emergencyItem.unixtime * 1000);
			SimpleDateFormat format = new SimpleDateFormat("EEE d, MMM yyyy");
			String dateStr = format.format(postDate);
			html = String.format(noticeTemplate, emergencyItem.text, dateStr);
		} else {
			html = emergencyItem.text;
		}
		
		mEmergencyMsgTV.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
		
	    msgPullToRefreshAttacher.setEnabled(true);
	    msgPullToRefreshAttacher.setRefreshing(false);
	    contactsPullToRefreshAttacher.setEnabled(true);
	    contactsPullToRefreshAttacher.setRefreshing(false);

	    emergencyMsgLoader.setVisibility(View.GONE);
	}
	
	@Override
	public boolean isModuleHomeActivity() {
		return false;
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
	protected void onOptionSelected(String optionId) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onRefreshStarted(View view) {
		EmergencyParser.fetchStatus(this, uiStatusHandler);
	}
}
