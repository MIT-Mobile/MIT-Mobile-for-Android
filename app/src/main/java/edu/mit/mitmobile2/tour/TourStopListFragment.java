package edu.mit.mitmobile2.tour;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.mit.mitmobile2.R;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class TourStopListFragment extends Fragment {

    StickyListHeadersListView listView;

    public TourStopListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tour_stop_list, container);

        listView = (StickyListHeadersListView) view.findViewById(R.id.sticky_header_list_view);

        return view;
    }
}
