package edu.mit.mitmobile2.emergency.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.mit.mitmobile2.R;



/**
 * Created by grmartin on 4/16/15.
 */
public class EmergencyContactsFragment extends ListFragment {

    public EmergencyContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_emergency_contacts, container, false);
        return rootView;
    }
}

