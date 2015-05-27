package edu.mit.mitmobile2.maps.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.mit.mitmobile2.R;

public class CategoryDetailFragment extends Fragment {

    public static CategoryDetailFragment newInstance() {
        CategoryDetailFragment fragment = new CategoryDetailFragment();

        return fragment;
    }

    public CategoryDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_detail, container, false);
    }
}
