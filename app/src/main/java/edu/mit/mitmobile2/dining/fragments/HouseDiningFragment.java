package edu.mit.mitmobile2.dining.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.dining.adapters.HouseDiningAdapter;
import edu.mit.mitmobile2.dining.interfaces.Updateable;
import edu.mit.mitmobile2.dining.model.MITDiningDining;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by serg on 5/8/15.
 */
public class HouseDiningFragment extends Fragment implements Updateable {

    private StickyListHeadersListView listView;

    private HouseDiningAdapter adapter;
    private MITDiningDining mitDiningDining;

    public static HouseDiningFragment newInstance() {
        HouseDiningFragment fragment = new HouseDiningFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dining_house, null);

        listView = (StickyListHeadersListView) view.findViewById(R.id.list_dining_house);
        adapter = new HouseDiningAdapter(getActivity(), null);
        listView.setAdapter(adapter);

        return view;
    }

    /* Updateable */

    @Override
    public void onDining(MITDiningDining mitDiningDining) {
        this.mitDiningDining = mitDiningDining;
        adapter.setMitDiningDining(mitDiningDining);
    }

}
