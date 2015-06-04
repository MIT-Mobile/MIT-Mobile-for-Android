package edu.mit.mitmobile2.emergency.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.emergency.EmergencyManager;
import edu.mit.mitmobile2.emergency.EmergencyManager.EmergencyManagerCall;
import edu.mit.mitmobile2.emergency.adapter.MITEmergencyContactsAdapter;
import edu.mit.mitmobile2.emergency.model.MITEmergencyInfoContact;
import edu.mit.mitmobile2.shared.SharedIntentManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import edu.mit.mitmobile2.shared.logging.LoggingManager.Timber;


/**
 * Created by grmartin on 4/16/15.
 */
public class EmergencyContactsFragment extends ListFragment {

    private static final String KEY_EXTRA_CONTACTS = "key_extra_contacts";

    private MITEmergencyContactsAdapter adapter;
    private List<MITEmergencyInfoContact> contacts;

    public static EmergencyContactsFragment newInstance(List<MITEmergencyInfoContact> contacts) {
        EmergencyContactsFragment fragment = new EmergencyContactsFragment();

        Bundle extras = new Bundle();
        extras.putParcelableArrayList(KEY_EXTRA_CONTACTS, (ArrayList<? extends Parcelable>) contacts);
        fragment.setArguments(extras);

        return fragment;
    }

    public EmergencyContactsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arg = getArguments();
        if (arg != null && arg.containsKey(KEY_EXTRA_CONTACTS)) {
            contacts = arg.getParcelableArrayList(KEY_EXTRA_CONTACTS);
        } else{
            contacts = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_emergency_contacts, container, false);

        this.setListAdapter(adapter = new MITEmergencyContactsAdapter());
        adapter.updateItems(contacts);

        return rootView;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        this.startActivity(SharedIntentManager.createTelephoneCallIntent(((MITEmergencyInfoContact) adapter.getItem(position)).getPhone()));
    }
}

