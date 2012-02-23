package edu.mit.mitmobile2.emergency;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.Global;
import edu.mit.mitmobile2.Module;
import edu.mit.mitmobile2.ModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.TwoLineActionRow;
import edu.mit.mitmobile2.alerts.C2DMReceiver;
import edu.mit.mitmobile2.objs.EmergencyItem;
import edu.mit.mitmobile2.objs.EmergencyItem.Contact;


public class EmergencyActivity extends ModuleActivity {

	private ListView mListView;

	private Context mContext;
	private WebView mEmergencyMsgTV = null;

	TextView emergencyContactsTV;

	SharedPreferences pref;
	
	static EmergencyItem emergencyItem;
	
	static String PREF_KEY_EMERGENCY_TEXT = "emergency_text";
	
	static final int MENU_REFRESH = MENU_SEARCH + 1;
	
	/****************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		mContext = this;
		
		setContentView(R.layout.emergency);
		
		pref = this.getSharedPreferences(Global.PREFS,MODE_PRIVATE);  // FIXME
		
		FullScreenLoader loader = (FullScreenLoader) findViewById(R.id.emergencyListLoader);
		loader.showLoading();
		getData();
		
	}

	/****************************************************/
	private void updateEmergencyText() {
		String html;
		if (emergencyItem.unixtime > 0) {
			Date postDate = new Date(emergencyItem.unixtime * 1000);
			SimpleDateFormat format = new SimpleDateFormat("EEE d, MMM yyyy");
			String dateStr = format.format(postDate);
			html = String.format("%s\n<p>Posted %s</p>",
				emergencyItem.text,
				dateStr);
		} else {
			html = emergencyItem.text;
		}
		
		mEmergencyMsgTV.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
	}
	
	private void updateView() {
		
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
		
		FullScreenLoader loadingView = (FullScreenLoader) findViewById(R.id.emergencyListLoader);		
		loadingView.setVisibility(View.GONE);
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mListView = (ListView) findViewById(R.id.emergencyListView);
		mListView.setVisibility(View.VISIBLE);
		LinearLayout listHeader = (LinearLayout) inflater.inflate(R.layout.emergency_header, null);
		//ImageView backgroundView = (ImageView) listHeader.findViewById(R.id.graySectionHeaderBackground);
		//int height = backgroundView.getDrawable().getIntrinsicHeight();
		//FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, height);
		//FrameLayout wrapper = (FrameLayout) listHeader.findViewById(R.id.graySectionHeaderWrapper);
		//wrapper.setLayoutParams(params);
		mListView.addHeaderView(listHeader);
		mEmergencyMsgTV = (WebView) findViewById(R.id.emergencyMsgTV);
		
		updateEmergencyText();
		
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
				super.handleMessage(msg);
				updateView();
			}
		};
		
		// TODO: don't wait for both parts to load before showing stuff to the user
		final Handler nextHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				EmergencyParser.fetchContacts(mContext, uiHandler);
				
				// mark emergency as read
				C2DMReceiver.markNotificationAsRead(mContext, "emergencyinfo:");
			}
		};
		
		EmergencyParser.fetchStatus(this, nextHandler);
		
	}
	
	void refresh() {
		
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				updateEmergencyText();
			}
		};

		EmergencyParser.refreshStatus(this, handler);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_REFRESH:
			refresh();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected Module getModule() {
		return new EmergencyModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return true;
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) { 
		menu.add(0, MENU_REFRESH, Menu.NONE, "Refresh")
		  .setIcon(R.drawable.menu_refresh);
	}
}
