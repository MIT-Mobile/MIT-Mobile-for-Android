package edu.mit.mitmobile2.tour.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.tour.activities.TourDetailActivity;
import edu.mit.mitmobile2.tour.adapters.TourStopAdapter;
import edu.mit.mitmobile2.tour.model.MITTour;
import edu.mit.mitmobile2.tour.model.MITTourStop;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class TourStopListFragment extends Fragment {

    StickyListHeadersListView listView;
    TourStopAdapter adapter;
    MITTour tour;

    public TourStopListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tour_stop_list, null);

        listView = (StickyListHeadersListView) view.findViewById(R.id.sticky_header_list_view);

        View headerView = View.inflate(getActivity(), R.layout.tour_list_header_top, null);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTourDetailActivity();
            }
        });

        listView.addHeaderView(headerView);

        adapter = new TourStopAdapter(getActivity(), new ArrayList<MITTourStop>());
        listView.setAdapter(adapter);

        return view;
    }

    private void showTourDetailActivity() {
        Intent intent = new Intent(getActivity(), TourDetailActivity.class);
        intent.putExtra(Constants.Tours.TOUR_DETAILS_KEY, tour.getDescriptionHtml());
        startActivity(intent);
    }

    @Subscribe
    public void mitTourLoadedEvent(OttoBusEvent.TourInfoLoadedEvent event) {
        //TODO: Change tour to parcelable so it can be saved in the frag bundle
        this.tour = event.getTour();
        adapter.updateItems(event.getTour().getStops());
    }

    @Override
    public void onResume() {
        super.onResume();
        MitMobileApplication.bus.register(this);
    }

    @Override
    public void onPause() {
        MitMobileApplication.bus.unregister(this);
        super.onPause();
    }
}
