package edu.mit.mitmobile2.emergency.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.emergency.EmergencyManager;
import edu.mit.mitmobile2.emergency.adapter.MITEmergencyContactsInfoAdapter;
import edu.mit.mitmobile2.emergency.model.MITEmergencyInfoAnnouncement;
import edu.mit.mitmobile2.emergency.model.MITEmergencyInfoContact;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EmergencyFragment extends Fragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String KEY_STATE_ANNOUNCEMENT = "key_state_announcement";
    private static final String KEY_STATE_CONTACTS = "key_state_contacts";

    // contacts count to display
    private static final int MAX_CONTACTS_COUNT = 3;

    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private MITEmergencyContactsInfoAdapter adapter;

    private MITEmergencyInfoAnnouncement announcement;
    private List<MITEmergencyInfoContact> contacts;

    public EmergencyFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency, null);

        listView = (ListView) view.findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshlayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        adapter = new MITEmergencyContactsInfoAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        getActivity().setTitle(R.string.title_activity_emergency);

        if (savedInstanceState == null || savedInstanceState.containsKey(KEY_STATE_ANNOUNCEMENT)) {
            fetchAnnouncement();
        } else {
            announcement = savedInstanceState.getParcelable(KEY_STATE_ANNOUNCEMENT);

            adapter.updateAnnouncement(announcement);
        }

        if (savedInstanceState == null || savedInstanceState.containsKey(KEY_STATE_CONTACTS)) {
            fetchContacts();
        } else {
            contacts = savedInstanceState.getParcelableArrayList(KEY_STATE_CONTACTS);

            adapter.updateItems(contacts);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (announcement != null) {
            outState.putParcelable(KEY_STATE_ANNOUNCEMENT, announcement);
        }
        if (contacts != null) {
            outState.putParcelableArrayList(KEY_STATE_CONTACTS, (ArrayList<? extends Parcelable>) contacts);
        }
        super.onSaveInstanceState(outState);
    }

    /* AdapterView.OnItemClickListener */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int rowType = adapter.getItemViewType(position);
        if (rowType == MITEmergencyContactsInfoAdapter.ROW_TYPE_SHOW_MORE && contacts != null) {
            ((AppCompatActivity)getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, EmergencyContactsFragment.newInstance(contacts)).addToBackStack(null).commit();
        }
    }

    /* SwipeRefreshLayout.OnRefreshListener */

    @Override
    public void onRefresh() {
        fetchAnnouncement();
        fetchContacts();
    }

    /* Network */

    private void fetchAnnouncement() {
        swipeRefreshLayout.setRefreshing(true);

        EmergencyManager.getAnnouncement(getActivity(), new Callback<MITEmergencyInfoAnnouncement>() {
            @Override
            public void success(MITEmergencyInfoAnnouncement mitEmergencyInfoAnnouncement, Response response) {
                swipeRefreshLayout.setRefreshing(false);
                announcement = mitEmergencyInfoAnnouncement;

                if (adapter != null) {
                    adapter.updateAnnouncement(announcement);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                swipeRefreshLayout.setRefreshing(false);
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

    private void fetchContacts() {
        swipeRefreshLayout.setRefreshing(true);
        EmergencyManager.getContacts(getActivity(), new Callback<List<MITEmergencyInfoContact>>() {

            @Override
            public void success(List<MITEmergencyInfoContact> mitEmergencyInfoContacts, Response response) {
                swipeRefreshLayout.setRefreshing(false);
                contacts = mitEmergencyInfoContacts;

                onContactsReceived(contacts);
            }

            @Override
            public void failure(RetrofitError error) {
                swipeRefreshLayout.setRefreshing(false);
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

    private void onContactsReceived(List<MITEmergencyInfoContact> contacts) {
        if (contacts == null) {
            return;
        }

        int maxContactsCountToDisplay = Math.min(MAX_CONTACTS_COUNT, contacts.size());

        List<MITEmergencyInfoContact> contactsToDisplay = contacts.subList(0, maxContactsCountToDisplay);

        if (adapter != null) {
            adapter.updateItems(contactsToDisplay);
        }
    }
}
