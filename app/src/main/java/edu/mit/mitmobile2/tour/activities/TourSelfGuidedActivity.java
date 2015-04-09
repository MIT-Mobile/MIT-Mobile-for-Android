package edu.mit.mitmobile2.tour.activities;

import android.content.Intent;
import android.os.Bundle;

import java.util.HashMap;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.tour.callbacks.TourSelfGuidedCallback;
import edu.mit.mitmobile2.tour.fragment.TourStopListFragment;
import edu.mit.mitmobile2.tour.fragment.TourStopMapFragment;
import edu.mit.mitmobile2.tour.model.MITTour;
import edu.mit.mitmobile2.tour.model.MITTourStop;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class TourSelfGuidedActivity extends MITActivity implements TourSelfGuidedCallback {

    /**
     * The plan:
     * This activity houses 2 fragments, one for the map and one for the list.
     * The activity holds the stop data, since it is common between the two fragments
     * When we want to toggle, we just animate the fragments in or out
     * <p/>
     * Make HTTP calls here in the activity, not in the fragments
     */

    private TourStopMapFragment mapFragment;
    private TourStopListFragment listFragment;

    private MITTour tour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_self_guided);

        mapFragment = new TourStopMapFragment();
        listFragment = new TourStopListFragment();

        MITAPIClient mitApiClient = new MITAPIClient(this);

        HashMap<String, String> pathParams = new HashMap<>();
        pathParams.put("tour", "self_guided");
        mitApiClient.get(Constants.TOURS, Constants.Tours.ALL_TOUR_STOPS_PATH, pathParams, null, new Callback<MITTour>() {
            @Override
            public void success(MITTour mitTour, Response response) {
                Timber.d("Success!");
                tour = mitTour;
                for (int i = 0; i < tour.getStops().size(); i++) {
                    // Spacing helps with centering of text in marker icon
                    String markerText = (i + 1) < 10 ? "   " + (i + 1) + "   " : "  " + (i + 1) + "  ";
                    tour.getStops().get(i).setMarkerText(markerText);
                }
                MitMobileApplication.bus.post(new OttoBusEvent.TourInfoLoadedEvent(tour));
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error, "Oh no!");
            }
        });

        getFragmentManager().beginTransaction().replace(R.id.tour_frame, mapFragment).commit();
    }

    @Override
    public void switchViews(boolean toList) {
        if (toList) {
            getFragmentManager().beginTransaction().replace(R.id.tour_frame, listFragment).commit();
        } else {
            getFragmentManager().beginTransaction().replace(R.id.tour_frame, mapFragment).commit();

        }
    }

    @Override
    public MITTour getTour() {
        return tour;
    }

    @Override
    public void showTourDetailActivity(String description) {
        Intent intent = new Intent(this, TourDetailActivity.class);
        intent.putExtra(Constants.Tours.TOUR_DETAILS_KEY, description);
        startActivity(intent);
    }

    @Override
    public void showMainLoopFragment(int currentStopNum) {
        Intent intent = new Intent(this, TourStopActivity.class);
        intent.putExtra(Constants.TOUR_STOP_TYPE, Constants.MAIN_LOOP);
        intent.putExtra(Constants.TOURS, tour);
        intent.putExtra(Constants.CURRENT_MAIN_LOOP_STOP, currentStopNum);
        startActivity(intent);
    }

    @Override
    public void showSideTripFragment(MITTourStop mitTourStop) {
        Intent intent = new Intent(this, TourStopActivity.class);
        intent.putExtra(Constants.TOUR_STOP, mitTourStop);
        intent.putExtra(Constants.TOUR_STOP_TYPE, Constants.SIDE_TRIP);
        startActivity(intent);
    }
}
