package edu.mit.mitmobile2.dining.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.adapters.RetailAdapter;
import edu.mit.mitmobile2.dining.interfaces.Updateable;
import edu.mit.mitmobile2.dining.model.MITDiningDining;
import edu.mit.mitmobile2.dining.model.MITDiningRetailVenue;
import edu.mit.mitmobile2.maps.MapManager;
import edu.mit.mitmobile2.maps.model.MITMapPlace;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by serg on 5/8/15.
 */
public class RetailFragment extends Fragment implements Updateable, AdapterView.OnItemClickListener {

    private static final String KEY_STATE_DINING = "state_dining";

    private StickyListHeadersListView listView;

    private RetailAdapter adapter;
    private MITDiningDining mitDiningDining;

    public static RetailFragment newInstance() {
        RetailFragment fragment = new RetailFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dining_retail, null);

        adapter = new RetailAdapter(getActivity());

        listView = (StickyListHeadersListView) view.findViewById(R.id.list_retail);
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
        fetchMapPlaces();

    }

    /* AdapterView.OnItemClickListener */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MITDiningRetailVenue selectedVenue = adapter.getItem(position);
        // TODO: add logic here
    }

    /* Network */

    private void fetchMapPlaces() {
        MapManager.getMapPlaces(getActivity(), new Callback<ArrayList<MITMapPlace>>() {
            @Override
            public void success(ArrayList<MITMapPlace> mitMapPlaces, Response response) {
                if (adapter != null) {
                    adapter.setRetailVenues(mitDiningDining.getVenues().getRetail(), mitMapPlaces);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }
}
