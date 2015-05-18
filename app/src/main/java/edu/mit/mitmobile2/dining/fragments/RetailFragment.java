package edu.mit.mitmobile2.dining.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;

import java.util.ArrayList;
import java.util.Set;

import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.PreferenceUtils;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.activities.DiningRetailActivity;
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

public class RetailFragment extends Fragment implements Updateable, AdapterView.OnItemClickListener {

    private static final String KEY_STATE_DINING = "state_dining";

    private StickyListHeadersListView listView;

    private RetailAdapter adapter;
    private MITDiningDining mitDiningDining;
    private SwipeRefreshLayout refreshLayout;
    private List<MITMapPlace> mitMapPlaces;

    public RetailFragment() {
    }

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
        listView.setOnItemClickListener(this);

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.dining_refreshlayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDiningRetailVenues();
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_STATE_DINING)) {
                mitDiningDining = savedInstanceState.getParcelable(KEY_STATE_DINING);

                onDining(mitDiningDining);
            }
        }

        return view;
    }

    private void getDiningRetailVenues() {
        MITAPIClient mitApiClient = new MITAPIClient(getActivity());
        mitApiClient.get(Constants.DINING, Constants.Dining.DINING_RETAIL_PATH, null, null, new Callback<List<MITDiningRetailVenue>>() {
            @Override
            public void success(List<MITDiningRetailVenue> mitDiningRetailVenues, Response response) {
                for (MITDiningRetailVenue retailVenue : mitDiningRetailVenues) {
                    int i = mitDiningRetailVenues.indexOf(retailVenue);
                    String markerText = (i + 1) < 10 ? "   " + (i + 1) + "   " : "  " + (i + 1) + "  ";
                    retailVenue.setMarkerText(markerText);
                    retailVenue.setFavorite(isVenueFavorite(retailVenue));
                }
                mitDiningDining.getVenues().setRetail(mitDiningRetailVenues);
                adapter.setRetailVenues(mitDiningRetailVenues, mitMapPlaces);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private boolean isVenueFavorite(MITDiningRetailVenue venue) {
        if (!PreferenceUtils.getDefaultSharedPreferencesMultiProcess(getActivity()).contains(Constants.FAVORITE_VENUES_KEY)) {
            return false;
        } else {
            Set<String> stringSet = PreferenceUtils.getDefaultSharedPreferencesMultiProcess(getActivity()).getStringSet(Constants.FAVORITE_VENUES_KEY, null);
            return stringSet.contains(venue.getIdentifier());
        }
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
        Intent intent = new Intent(getActivity(), DiningRetailActivity.class);
        intent.putExtra(Constants.DINING_VENUE_KEY, selectedVenue);
        startActivity(intent);
    }

    /* Network */

    private void fetchMapPlaces() {
        MapManager.getMapPlaces(getActivity(), new Callback<ArrayList<MITMapPlace>>() {
            @Override
            public void success(ArrayList<MITMapPlace> mitMapPlaces, Response response) {
                if (adapter != null) {
                    RetailFragment.this.mitMapPlaces = mitMapPlaces;
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
