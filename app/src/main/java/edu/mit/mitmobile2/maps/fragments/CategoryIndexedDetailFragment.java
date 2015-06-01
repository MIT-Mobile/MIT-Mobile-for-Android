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
import java.util.List;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MapManager;
import edu.mit.mitmobile2.maps.adapter.CategoryIndexedAdapter;
import edu.mit.mitmobile2.maps.model.MITMapCategory;
import edu.mit.mitmobile2.maps.model.MITMapPlace;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class CategoryIndexedDetailFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String KEY_MAP_CATEGORY = "key_map_category";
    private static final String KEY_MAP_SHOULD_SORT = "key_map_should_sort";

    private static final String KEY_STATE_PLACES = "key_state_places";

    private StickyListHeadersListView listView;
    private SwipeRefreshLayout refreshLayout;

    private CategoryIndexedAdapter adapter;

    private MITMapCategory category;
    private ArrayList<MITMapPlace>[] sectionedPlaces;
    private boolean shouldSortCategory;

    public static CategoryIndexedDetailFragment newInstance(MITMapCategory category) {
        return newInstance(category, false);
    }

    public static CategoryIndexedDetailFragment newInstance(MITMapCategory category, boolean shouldSortCategory) {
        CategoryIndexedDetailFragment fragment = new CategoryIndexedDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_MAP_CATEGORY, category);
        bundle.putBoolean(KEY_MAP_SHOULD_SORT, shouldSortCategory);
        fragment.setArguments(bundle);

        return fragment;
    }

    public CategoryIndexedDetailFragment() {
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

            if (arguments.containsKey(KEY_MAP_SHOULD_SORT)) {
                shouldSortCategory = arguments.getBoolean(KEY_MAP_SHOULD_SORT);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_indexed_detail, container, false);

        listView = (StickyListHeadersListView) view.findViewById(R.id.list);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.category_indexed_refreshlayout);

        adapter = new CategoryIndexedAdapter(category);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        if (savedInstanceState == null) {
            sectionedPlaces = new ArrayList[category.getCategories().size()];
            getPlaces();
        } else {
            if (savedInstanceState.containsKey(KEY_STATE_PLACES)) {
                // TODO:
                // places = savedInstanceState.getParcelableArrayList(KEY_STATE_PLACES);
                // onPlacesReceived((ArrayList<MITMapPlace>) places);
            }
        }

        return view;
    }

    /* AdapterView.OnItemClickListener */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent result = new Intent();
        result.putExtra(Constants.PLACES_KEY, adapter.getItem(position));
        result.putExtra(Constants.Map.TAB_TYPE, 0);
        getActivity().setResult(Activity.RESULT_OK, result);
        getActivity().finish();
    }

    /* Network */

    private void getPlaces() {
        refreshLayout.setRefreshing(true);

        for (int i = 0; i < category.getCategories().size(); i++) {
            MITMapCategory subCategory = category.getCategories().get(i);
            fetchCategoryPlaces(subCategory, i);
        }
    }

    private void fetchCategoryPlaces(final MITMapCategory category, final int subcategoryIndex) {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("category", category.getIdentifier());

        MapManager.getMapPlaces(getActivity(), queryParams, new Callback<ArrayList<MITMapPlace>>() {

            @Override
            public void success(ArrayList<MITMapPlace> mitMapPlaces, Response response) {
                for (MITMapPlace place : mitMapPlaces) {
                    place.setMitCategory(category);
                }
                onPlacesReceived(mitMapPlaces, subcategoryIndex);
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

    private synchronized void onPlacesReceived(ArrayList<MITMapPlace> mitMapPlaces, int subcategoryIndex) {
        sectionedPlaces[subcategoryIndex] = mitMapPlaces;

        if (adapter != null) {
            adapter.updatePlaces(mitMapPlaces, subcategoryIndex);
        }

        refreshLayout.setRefreshing(false);
        refreshLayout.setEnabled(false);
    }
}
