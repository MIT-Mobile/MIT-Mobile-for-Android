package edu.mit.mitmobile2.maps.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.adapter.CategoriesAdapter;

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

        listView = (ListView) view.findViewById(R.id.map_list_view);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
