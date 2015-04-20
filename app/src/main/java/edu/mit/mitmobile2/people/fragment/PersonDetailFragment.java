package edu.mit.mitmobile2.people.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.mit.mitmobile2.R;

/**
 * Created by grmartin on 4/17/15.
 */
public class PersonDetailFragment extends Fragment {

    public PersonDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_people_person_detail, container, false);
        return rootView;
    }
}