package edu.mit.mitmobile2.tour.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.otto.Subscribe;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.tour.adapters.TourStopAdapter;
import edu.mit.mitmobile2.tour.callbacks.TourSelfGuidedCallback;
import edu.mit.mitmobile2.tour.model.MITTour;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class TourStopListFragment extends Fragment {

    TourStopAdapter adapter;
    MITTour tour;
    TourSelfGuidedCallback callback;

    public TourStopListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tour_stop_list, null);

        callback = (TourSelfGuidedCallback) getActivity();

        StickyListHeadersListView listView = (StickyListHeadersListView) view.findViewById(R.id.sticky_header_list_view);

        View headerView = View.inflate(getActivity(), R.layout.tour_list_header_top, null);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.showTourDetailActivity(tour.getDescriptionHtml());
            }
        });

        listView.addHeaderView(headerView);

        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.Tours.TOUR_KEY)) {
            tour = savedInstanceState.getParcelable(Constants.Tours.TOUR_KEY);
        } else {
            tour = callback.getTour();
        }

        adapter = new TourStopAdapter(getActivity(), tour.getStops(), callback);

        listView.setAdapter(adapter);

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.list_button);
        floatingActionButton.setColorNormalResId(R.color.mit_red);
        floatingActionButton.setColorPressedResId(R.color.mit_red_dark);
        floatingActionButton.setSize(FloatingActionButton.SIZE_NORMAL);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.switchViews(false);
            }
        });

        return view;
    }

    @Subscribe
    public void mitTourLoadedEvent(OttoBusEvent.TourInfoLoadedEvent event) {
        tour = event.getTour();
        adapter.updateItems(tour.getStops());
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.Tours.TOUR_KEY, tour);
    }
}
