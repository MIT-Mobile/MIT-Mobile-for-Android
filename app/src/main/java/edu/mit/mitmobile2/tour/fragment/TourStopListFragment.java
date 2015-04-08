package edu.mit.mitmobile2.tour.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.otto.Subscribe;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.tour.adapters.TourStopAdapter;
import edu.mit.mitmobile2.tour.callbacks.TourStopCallback;
import edu.mit.mitmobile2.tour.model.MITTour;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class TourStopListFragment extends Fragment implements Animation.AnimationListener {

    private static final int DURATION_OUTGOING = 300;
    private static final int DURATION_INCOMING = 301;

    TourStopAdapter adapter;
    MITTour tour;
    TourStopCallback callback;
    private FloatingActionButton floatingActionButton;

    public TourStopListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tour_stop_list, null);

        callback = (TourStopCallback) getActivity();

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

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.list_button);
        floatingActionButton.setColorNormalResId(R.color.mit_red);
        floatingActionButton.setColorPressedResId(R.color.mit_red_dark);
        floatingActionButton.setSize(FloatingActionButton.SIZE_NORMAL);
        floatingActionButton.setIcon(R.drawable.ic_map);
        floatingActionButton.setVisibility(View.VISIBLE);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        startAnimation(0, 0, displayMetrics.heightPixels, floatingActionButton.getY(), DURATION_INCOMING);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                startAnimation(0, 0, 0, displayMetrics.heightPixels, DURATION_OUTGOING);
            }
        });

        return view;
    }

    private void startAnimation(float fromXDelt, float toXDelt, float fromYDelt, float toYDelt, int duration) {
        TranslateAnimation translateAnimation = new TranslateAnimation(fromXDelt, toXDelt, fromYDelt, toYDelt);
        translateAnimation.setDuration(duration);
        translateAnimation.setAnimationListener(TourStopListFragment.this);
        floatingActionButton.startAnimation(translateAnimation);
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

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        int duration = (int) animation.getDuration();
        switch (duration) {
            case DURATION_OUTGOING:
                floatingActionButton.setVisibility(View.INVISIBLE);
                callback.switchViews(false);
                break;
            case DURATION_INCOMING:
                floatingActionButton.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
