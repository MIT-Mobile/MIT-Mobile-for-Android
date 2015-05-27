package edu.mit.mitmobile2.maps.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.MapManager;
import edu.mit.mitmobile2.maps.adapter.CategoriesAdapter;
import edu.mit.mitmobile2.maps.model.MITMapCategory;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CategoriesFragment extends Fragment {

    public interface OnCategoriesFragmentInteractionListener {

    }

    private ListView listView;

    private CategoriesAdapter adapter;

    private OnCategoriesFragmentInteractionListener mListener;

    public static CategoriesFragment newInstance() {
        CategoriesFragment fragment = new CategoriesFragment();
        return fragment;
    }

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCategoriesFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        listView = (ListView) view.findViewById(R.id.list);

        adapter = new CategoriesAdapter();
        listView.setAdapter(adapter);

        if (savedInstanceState == null) {
            fetchCategories();
        }

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /* Network */

    private void fetchCategories() {
        MapManager.getMapPlaceCategories(getActivity(), new Callback<ArrayList<MITMapCategory>>() {
            @Override
            public void success(ArrayList<MITMapCategory> mitMapCategories, Response response) {
                if (adapter != null) {
                    adapter.refreshCategories(mitMapCategories);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                MitMobileApplication.bus.post(new OttoBusEvent.RetrofitFailureEvent(error));
            }
        });
    }
}
