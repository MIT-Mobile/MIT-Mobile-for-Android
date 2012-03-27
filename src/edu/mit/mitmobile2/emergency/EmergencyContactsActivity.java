package edu.mit.mitmobile2.emergency;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import edu.mit.mitmobile2.FullScreenLoader;
import edu.mit.mitmobile2.NewModule;
import edu.mit.mitmobile2.NewModuleActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.objs.EmergencyItem.Contact;

public class EmergencyContactsActivity extends NewModuleActivity {
	
	FullScreenLoader mLoadingView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.emergency);
		setTitle("Emergeny Contacts");
		mLoadingView = (FullScreenLoader) findViewById(R.id.emergencyListLoader);
		
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
		mLoadingView.setVisibility(View.GONE);
		
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
}
