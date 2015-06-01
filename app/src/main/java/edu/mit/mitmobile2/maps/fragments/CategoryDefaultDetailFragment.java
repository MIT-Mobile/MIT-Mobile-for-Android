package edu.mit.mitmobile2.maps.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MapManager;
import edu.mit.mitmobile2.maps.adapter.CategoryDefaultAdapter;
import edu.mit.mitmobile2.maps.model.MITMapCategory;
import edu.mit.mitmobile2.maps.model.MITMapPlace;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CategoryDefaultDetailFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String KEY_MAP_CATEGORY = "key_map_category";

    private static final String KEY_STATE_PLACES = "key_state_places";

    private ListView listView;
    private SwipeRefreshLayout refreshLayout;

    private CategoryDefaultAdapter adapter;

    private MITMapCategory category;
    private ArrayList<MITMapPlace> places;

    public static CategoryDefaultDetailFragment newInstance(MITMapCategory category) {
        CategoryDefaultDetailFragment fragment = new CategoryDefaultDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_MAP_CATEGORY, category);
        fragment.setArguments(bundle);

        return fragment;
    }

    public CategoryDefaultDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            if (arguments.containsKey(KEY_MAP_CATEGORY)) {
                category = arguments.getParcelable(KEY_MAP_CATEGORY);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_default_detail, container, false);

        listView = (ListView) view.findViewById(R.id.list);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.category_default_refreshlayout);

        View headerViewAllOnMap = getActivity().getLayoutInflater().inflate(R.layout.header_map_category_default_detail, null);

        adapter = new CategoryDefaultAdapter(category);
        listView.addHeaderView(headerViewAllOnMap);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        if (savedInstanceState == null) {
            places = new ArrayList<>();
            getPlaces();
        } else {
            if (savedInstanceState.containsKey(KEY_STATE_PLACES)) {
                places = savedInstanceState.getParcelableArrayList(KEY_STATE_PLACES);
                onPlacesReceived(places);
            }
        }

        return view;
    }

    /* AdapterView.OnItemClickListener */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (places == null || places.size() == 0) {
            return;
        }

        Intent result = new Intent();

        if (position == 0) {
            // add all is selected
            result.putExtra(Constants.PLACES_KEY, places);
        } else {
            result.putExtra(Constants.PLACES_KEY, places.get(position - 1));
        }

        result.putExtra(Constants.Map.TAB_TYPE, 0);
        getActivity().setResult(Activity.RESULT_OK, result);
        getActivity().finish();
    }

    /* Network */

    private void getPlaces() {
        refreshLayout.setRefreshing(true);
        fetchCategoryPlaces(category);
    }

    private void fetchCategoryPlaces(final MITMapCategory category) {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("category", category.getIdentifier());

        MapManager.getMapPlaces(getActivity(), queryParams, new Callback<ArrayList<MITMapPlace>>() {

            @Override
            public void success(ArrayList<MITMapPlace> mitMapPlaces, Response response) {
                for (MITMapPlace place : mitMapPlaces) {
                    place.setMitCategory(category);
                }
                onPlacesReceived(mitMapPlaces);
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

    private void onPlacesReceived(ArrayList<MITMapPlace> mitMapPlaces) {
        this.places.clear();
        this.places.addAll(mitMapPlaces);

        if (adapter != null) {
            adapter.updatePlaces(mitMapPlaces);
        }

        refreshLayout.setRefreshing(false);
        refreshLayout.setEnabled(false);
    }
}
