package edu.mit.mitmobile2.dining.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.adapters.HouseDiningAdapter;

/**
 * Created by serg on 5/8/15.
 */
public class HouseDiningFragment extends Fragment {

    private ListView listView;

    private HouseDiningAdapter adapter;

    public static HouseDiningFragment newInstance() {
        HouseDiningFragment fragment = new HouseDiningFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dining_house, null);

        listView = (ListView) view.findViewById(R.id.list_dining_house);

        return view;
    }
}
