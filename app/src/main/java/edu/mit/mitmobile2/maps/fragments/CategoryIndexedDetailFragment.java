package edu.mit.mitmobile2.maps.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

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

public class CategoryIndexedDetailFragment extends Fragment {

    private static final String KEY_MAP_CATEGORY = "key_map_category";
    private static final String KEY_MAP_SHOULD_SORT = "key_map_should_sort";

    private static final String KEY_STATE_PLACES = "key_state_places";

    private ListView listView;

    private CategoryIndexedAdapter adapter;

    private MITMapCategory category;
    private List<MITMapPlace> places;
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

        listView = (ListView) view.findViewById(R.id.list);

        adapter = new CategoryIndexedAdapter(category);
        listView.setAdapter(adapter);

        if (savedInstanceState == null) {
            places = new ArrayList<>();
            getPlaces();
        } else {
            if (savedInstanceState.containsKey(KEY_STATE_PLACES)) {
                places = savedInstanceState.getParcelableArrayList(KEY_STATE_PLACES);
            }
        }

        return view;
    }

    /* Network */

    private void getPlaces() {
        places.clear();

        for (MITMapCategory subCategory : category.getCategories()) {
            fetchCategoryPlaces(subCategory);
        }
    }

    private void fetchCategoryPlaces(MITMapCategory category) {
        MapManager.getMapPlaces(getActivity(), category, new Callback<ArrayList<MITMapPlace>>() {

            @Override
            public void success(ArrayList<MITMapPlace> mitMapPlaces, Response response) {
                onPlacesReceived(mitMapPlaces);
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }

    private synchronized void onPlacesReceived(ArrayList<MITMapPlace> mitMapPlaces) {
        places.addAll(mitMapPlaces);

        if (adapter != null) {
            adapter.updatePlaces(places);
        }
    }
}
