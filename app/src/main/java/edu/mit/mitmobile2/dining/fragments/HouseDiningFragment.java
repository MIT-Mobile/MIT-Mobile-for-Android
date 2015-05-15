package edu.mit.mitmobile2.dining.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.activities.AnnouncementsActivity;
import edu.mit.mitmobile2.dining.activities.DiningHouseActivity;
import edu.mit.mitmobile2.dining.adapters.HouseDiningAdapter;
import edu.mit.mitmobile2.dining.callback.DiningHouseCallback;
import edu.mit.mitmobile2.dining.interfaces.Updateable;
import edu.mit.mitmobile2.dining.model.MITDiningDining;
import edu.mit.mitmobile2.dining.model.MITDiningHouseVenue;
import edu.mit.mitmobile2.dining.model.MITDiningLinks;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by serg on 5/8/15.
 */
public class HouseDiningFragment extends Fragment implements Updateable, AdapterView.OnItemClickListener, DiningHouseCallback {

    private static final String KEY_STATE_DINING = "state_dining";

    private StickyListHeadersListView listView;

    private HouseDiningAdapter adapter;
    private MITDiningDining mitDiningDining;
    private DiningHouseCallback callback;

    public static HouseDiningFragment newInstance() {
        HouseDiningFragment fragment = new HouseDiningFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dining_house, null);

        callback = (DiningHouseCallback) this;

        adapter = new HouseDiningAdapter(getActivity(), null, callback);

        listView = (StickyListHeadersListView) view.findViewById(R.id.list_dining_house);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_STATE_DINING)) {
                mitDiningDining = savedInstanceState.getParcelable(KEY_STATE_DINING);

                onDining(mitDiningDining);
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mitDiningDining != null) {
            outState.putParcelable(KEY_STATE_DINING, mitDiningDining);
        }

        super.onSaveInstanceState(outState);
    }

    /* Updateable */

    @Override
    public void onDining(MITDiningDining mitDiningDining) {
        this.mitDiningDining = mitDiningDining;
        if (adapter != null) {
            adapter.setMitDiningDining(mitDiningDining);
        }
    }

    /* AdapterView.OnItemClickListener */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object selectedItem = adapter.getItem(position);

        if (selectedItem instanceof MITDiningDining) {                              // announcement
            MITDiningDining dining = (MITDiningDining) selectedItem;
            Intent intent = new Intent(getActivity(), AnnouncementsActivity.class);
            intent.putExtra(AnnouncementsActivity.ANNOUNCEMENTS_EXTRA, dining.getAnnouncementsHTML());
            startActivity(intent);
        } else if (selectedItem instanceof MITDiningHouseVenue) {                   // venue
            MITDiningHouseVenue houseVenue = (MITDiningHouseVenue) selectedItem;
            // TODO: add logic here
        } else if (selectedItem instanceof MITDiningLinks) {                        // resource
            MITDiningLinks link = (MITDiningLinks) selectedItem;

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link.getUrl()));
                startActivity(intent);
            } catch (NullPointerException e) {
                e.printStackTrace();
                // TODO: show toast here or so
            }
        }
    }

    @Override
    public void dinningHouseVenueCallback(MITDiningHouseVenue venue) {
        Intent intent = new Intent(getActivity(), DiningHouseActivity.class);
        intent.putExtra(Constants.Dining.DINING_HOUSE, venue);
        startActivity(intent);
    }
}
