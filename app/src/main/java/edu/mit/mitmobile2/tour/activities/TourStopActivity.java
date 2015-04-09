package edu.mit.mitmobile2.tour.activities;

import android.os.Bundle;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.tour.callbacks.TourStopCallback;
import edu.mit.mitmobile2.tour.fragment.TourStopViewPagerFragment;
import edu.mit.mitmobile2.tour.fragment.TourStopsFragment;
import edu.mit.mitmobile2.tour.model.MITTour;
import edu.mit.mitmobile2.tour.model.MITTourStop;

public class TourStopActivity extends MITActivity implements TourStopCallback {

    private TourStopViewPagerFragment tourStopViewPagerFragment;
    private TourStopsFragment tourStopsFragment;
    private MITTour tour;
    private MITTourStop tourStop;
    private String tourStopType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_stop);

        tour = getIntent().getExtras().getParcelable(Constants.TOURS);
        tourStopType = getIntent().getExtras().getString(Constants.TOUR_STOP_TYPE);

        if (tourStopType == Constants.MAIN_LOOP) {
            int currentStopNum = getIntent().getExtras().getInt(Constants.CURRENT_MAIN_LOOP_STOP);

            tourStopsFragment = new TourStopsFragment();
            Bundle args = new Bundle();
            args.putParcelable(Constants.TOURS, tour);
            args.putInt(Constants.CURRENT_MAIN_LOOP_STOP, currentStopNum);
            tourStopsFragment.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.tour_frame, tourStopsFragment).commit();
        } else {
            tourStop = getIntent().getExtras().getParcelable(Constants.TOUR_STOP);

            tourStopViewPagerFragment = TourStopViewPagerFragment.newInstance(tourStop, tour);
            Bundle args = new Bundle();
            args.putParcelable(Constants.TOUR_STOP, tourStop);
            args.putParcelable(Constants.TOURS, tour);
            tourStopViewPagerFragment.setArguments(args);
            getFragmentManager().beginTransaction().replace(R.id.container, tourStopViewPagerFragment).commit();
        }
    }

    @Override
    public void setMainLoopActionBarTitle(int mainLoopStopNum, int mainLoopStopsSize) {
        setTitle(String.format(getResources().getString(R.string.stop_nav_main_loop, mainLoopStopNum, mainLoopStopsSize)));
    }

    @Override
    public void setSideTripActionBarTitle() {
        setTitle(getResources().getString(R.string.stop_nav_side_trip));
    }
}
