package edu.mit.mitmobile2.maps.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MapManager;
import edu.mit.mitmobile2.maps.adapters.CategoriesAdapter;
import edu.mit.mitmobile2.maps.model.MITMapCategory;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MapListCategoryFragment extends MapListFragment {

    private static final String KEY_STATE_CATEGORIES = "key_state_categories";

    private SwipeRefreshLayout refreshLayout;

    private CategoriesAdapter adapter;
    private ArrayList<MITMapCategory> mitMapCategories;

    public static MapListCategoryFragment newInstance() {
        MapListCategoryFragment fragment = new MapListCategoryFragment();
        return fragment;
    }

    public MapListCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        listView = (ListView) view.findViewById(R.id.list);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.category_refreshlayout);

        adapter = new CategoriesAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        if (savedInstanceState == null) {
            fetchCategories();
        } else {
            if (savedInstanceState.containsKey(KEY_STATE_CATEGORIES)) {
                mitMapCategories = savedInstanceState.getParcelableArrayList(KEY_STATE_CATEGORIES);
                adapter.refreshCategories(mitMapCategories);
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mitMapCategories != null) {
            outState.putParcelableArrayList(KEY_STATE_CATEGORIES, mitMapCategories);
        }
        super.onSaveInstanceState(outState);
    }

    /* AdapterView.OnItemClickListener */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MITMapCategory selectedCategory = adapter.getItem(position);

        if (selectedCategory.getCategories() != null && selectedCategory.getCategories().size() > 0) {
            boolean shouldSortCategory = selectedCategory.getIdentifier().equals("building_name");
            getActivity().getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_root, CategoryIndexedDetailFragment.newInstance(selectedCategory, shouldSortCategory))
                    .addToBackStack(null)
                    .commit();
        } else {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_root, CategoryDefaultDetailFragment.newInstance(selectedCategory))
                    .addToBackStack(null)
                    .commit();
        }
    }

    /* Network */

    private void fetchCategories() {
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        }, 200);

        MapManager.getMapPlaceCategories(getActivity(), new Callback<ArrayList<MITMapCategory>>() {
            @Override
            public void success(ArrayList<MITMapCategory> mitMapCategories, Response response) {
                MapListCategoryFragment.this.mitMapCategories = mitMapCategories;

                if (adapter != null) {
                    adapter.refreshCategories(mitMapCategories);
                }

                refreshLayout.setRefreshing(false);
                refreshLayout.setEnabled(false);
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }
}
