package edu.mit.mitmobile2.tour.activities;

import android.content.Intent;
import android.os.Bundle;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.tour.callbacks.TourSelfGuidedCallback;
import edu.mit.mitmobile2.tour.callbacks.TourStopCallback;
import edu.mit.mitmobile2.tour.fragment.TourStopFragment;
import edu.mit.mitmobile2.tour.fragment.TourStopViewPagerFragment;
import edu.mit.mitmobile2.tour.model.MITTour;
import edu.mit.mitmobile2.tour.model.MITTourStop;

public class TourStopActivity extends MITActivity implements TourStopCallback, TourSelfGuidedCallback {

    private TourStopViewPagerFragment tourStopViewPagerFragment;
    private TourStopFragment tourStopsFragment;
    private MITTour tour;
    private MITTourStop tourStop;
    private String tourStopType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_stop);

        Bundle extras = getIntent().getExtras();
        tourStopType = extras.getString(Constants.Tours.TOUR_STOP_TYPE);
        tour = extras.getParcelable(Constants.Tours.TOUR_KEY);

        if (tourStopType.equals(Constants.Tours.MAIN_LOOP)) {
            int currentStopNum = extras.getInt(Constants.Tours.CURRENT_MAIN_LOOP_STOP);
            tourStopsFragment = TourStopFragment.newInstance(tour, currentStopNum);
            getFragmentManager().beginTransaction().replace(R.id.container, tourStopsFragment).commit();
        } else {
            tourStop = extras.getParcelable(Constants.Tours.TOUR_STOP);
            tourStopViewPagerFragment = TourStopViewPagerFragment.newInstance(tourStop, tour);
            getFragmentManager().beginTransaction().replace(R.id.container, tourStopViewPagerFragment).commit();
        }
    }

    @Override
    public void setMainLoopActionBarTitle(int mainLoopStopNum, int mainLoopStopsSize) {
        setTitle(String.format(getResources().getString(R.string.stop_nav_main_loop), mainLoopStopNum, mainLoopStopsSize));
    }

    @Override
    public void setSideTripActionBarTitle() {
        setTitle(getResources().getString(R.string.stop_nav_side_trip));
    }

    @Override
    public void setTourStopActionbarTitle(MITTourStop mitTourStop) {
        if (mitTourStop.getType().equals(Constants.Tours.MAIN_LOOP)) {
            setTitle(mitTourStop.getTitle());
        } else {
            setTitle(getResources().getString(R.string.stop_nav_side_trip) + " - " + mitTourStop.getTitle());
        }
    }

    @Override
    public void showMainLoopFragment(int currentStopNum) {
        Intent intent = new Intent(this, TourStopActivity.class);
        intent.putExtra(Constants.Tours.TOUR_STOP_TYPE, Constants.Tours.MAIN_LOOP);
        intent.putExtra(Constants.Tours.TOUR_KEY, tour);
        intent.putExtra(Constants.Tours.CURRENT_MAIN_LOOP_STOP, currentStopNum);
        startActivity(intent);
    }

    @Override
    public void showSideTripFragment(MITTourStop mitTourStop) {
        Intent intent = new Intent(this, TourStopActivity.class);
        intent.putExtra(Constants.Tours.TOUR_STOP, mitTourStop);
        intent.putExtra(Constants.Tours.TOUR_KEY, tour);
        intent.putExtra(Constants.Tours.TOUR_STOP_TYPE, Constants.Tours.SIDE_TRIP);
        startActivity(intent);
    }

    @Override
    public void switchViews(boolean toList) {

    }

    @Override
    public MITTour getTour() {
        return null;
    }

    @Override
    public void showTourDetailActivity(String description) {

    }
}