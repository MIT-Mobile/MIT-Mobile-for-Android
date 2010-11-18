package edu.mit.mitmobile.emergency;

import edu.mit.mitmobile.FullScreenLoader;
import edu.mit.mitmobile.Module;
import edu.mit.mitmobile.ModuleActivity;
import edu.mit.mitmobile.R;
import edu.mit.mitmobile.objs.EmergencyItem.Contact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class EmergencyContactsActivity extends ModuleActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.emergency);
		setTitle("Emergeny Contacts");
		
		Handler uiHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				updateView();
			}
		};
		
		EmergencyParser.fetchContacts(this, uiHandler);
	}
	
	private void updateView() {
		FullScreenLoader loadingView = (FullScreenLoader) findViewById(R.id.emergencyListLoader);		
		loadingView.setVisibility(View.GONE);
		
		ListView listView = (ListView) findViewById(R.id.emergencyListView);
		listView.setVisibility(View.VISIBLE);
		final EmergencyDB db = EmergencyDB.getInstance(this);
		EmergencyContactsAdapter adapter = new EmergencyContactsAdapter(this, db.getContactsCursor());

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Contact c = db.getContact(position);
				String numericPhone = PhoneNumberUtils.convertKeypadLettersToDigits(c.phone);
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + numericPhone));
				startActivity(intent);
			}
		});
		
	}

	@Override
	protected Module getModule() {
		return new EmergencyModule();
	}

	@Override
	public boolean isModuleHomeActivity() {
		return false;
	}

	@Override
	protected void prepareActivityOptionsMenu(Menu menu) { }
}
