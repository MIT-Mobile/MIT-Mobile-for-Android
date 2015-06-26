package edu.mit.mitmobile2.emergency.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.emergency.EmergencyManager;
import edu.mit.mitmobile2.emergency.adapter.MITEmergencyContactsAdapter;
import edu.mit.mitmobile2.emergency.model.MITEmergencyInfoContact;
import edu.mit.mitmobile2.shared.SharedActivityManager;
import edu.mit.mitmobile2.shared.SharedIntentManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EmergencyContactsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private MITEmergencyContactsAdapter adapter;
    private ListView listView;
    private List<MITEmergencyInfoContact> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);

        listView = (ListView) findViewById(R.id.emergency_contacts_listview);

        List<MITEmergencyInfoContact> contacts = getIntent().getParcelableArrayListExtra(Constants.EMERGENCY_CONTACTS_KEY);
        adapter = new MITEmergencyContactsAdapter();

        if (contacts == null) {
            fetchContacts();
        } else {
            adapter.updateItems(contacts);
        }

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void fetchContacts() {
        EmergencyManager.getContacts(this, new Callback<List<MITEmergencyInfoContact>>() {
            @Override
            public void success(List<MITEmergencyInfoContact> mitEmergencyInfoContacts, Response response) {
                contacts = mitEmergencyInfoContacts;

                onContactsReceived(contacts);
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

    private void onContactsReceived(List<MITEmergencyInfoContact> contacts) {
        if (contacts == null) {
            return;
        }

        if (adapter != null) {
            adapter.updateItems(contacts);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.startActivity(SharedActivityManager.createHomeJumpActivity(this));
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(SharedIntentManager.createTelephoneDialIntent(((MITEmergencyInfoContact) adapter.getItem(position)).getPhone()));
    }
}
