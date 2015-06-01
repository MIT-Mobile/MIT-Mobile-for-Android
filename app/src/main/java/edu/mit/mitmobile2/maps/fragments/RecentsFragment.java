package edu.mit.mitmobile2.maps.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.maps.adapter.RecentsAdapter;

public class RecentsFragment extends Fragment {

    public interface OnRecentsFragmentInteractionListener {
    }

    private RecentsAdapter adapter;

    private OnRecentsFragmentInteractionListener mListener;

    public static RecentsFragment newInstance() {
        RecentsFragment fragment = new RecentsFragment();
        return fragment;
    }

    public RecentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnRecentsFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recents, container, false);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
