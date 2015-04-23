package edu.mit.mitmobile2.emergency.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.emergency.EmergencyManager;
import edu.mit.mitmobile2.emergency.EmergencyManager.EmergencyManagerCall;
import edu.mit.mitmobile2.emergency.adapter.MITEmergencyContactInfoAdapter;
import edu.mit.mitmobile2.emergency.model.MITEmergencyInfoContact;
import edu.mit.mitmobile2.shared.SharedIntentManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;


/**
 * Created by grmartin on 4/16/15.
 */
public class EmergencyContactsFragment extends ListFragment {
    private MITEmergencyContactInfoAdapter adapter;
    private EmergencyManagerCall requestRunning;

    public EmergencyContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_emergency_contacts, container, false);

        this.setListAdapter(adapter = new MITEmergencyContactInfoAdapter());

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (this.requestRunning == null) {
            fetchContacts(this);
        }
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        this.startActivity(SharedIntentManager.createTelephoneCallIntent(((MITEmergencyInfoContact)adapter.getItem(position)).getPhone()));
    }

    private boolean fetchContacts(Object sender) {
        if (this.requestRunning != null && !this.requestRunning.isComplete()) {
            Timber.d("abend, request in process.");
            return true;
        }

        this.requestRunning = EmergencyManager.getContacts(getActivity(), new Callback<List<MITEmergencyInfoContact>>() {
            @Override
            public void success(List<MITEmergencyInfoContact> list, Response response) {
                Timber.d("Success!");
                adapter.updateItems(list);
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error, "Failed");
            }
        });

        return true;
    }
}

