package edu.mit.mitmobile2.tour.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;

import edu.mit.mitmobile2.Constants;
import edu.mit.mitmobile2.MITAPIClient;
import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.MitMobileApplication;
import edu.mit.mitmobile2.OttoBusEvent;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.tour.fragment.TourStopListFragment;
import edu.mit.mitmobile2.tour.fragment.TourStopMapFragment;
import edu.mit.mitmobile2.tour.model.MITTour;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class TourSelfGuidedActivity extends MITActivity {

    /**
     * The plan:
     * This activity houses 2 fragments, one for the map and one for the list.
     * The activity holds the stop data, since it is common between the two fragments
     * When we want to toggle, we just animate the fragments in or out
     * <p/>
     * Make HTTP calls here in the activity, not in the fragments
     */

    TourStopMapFragment mapFragment;
    TourStopListFragment listFragment;

    MITTour tour;

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
                MitMobileApplication.bus.post(new OttoBusEvent.TourInfoLoadedEvent(tour));
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e(error, "Oh no!");
            }
        });

        getFragmentManager().beginTransaction().replace(R.id.tour_frame, listFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tour_self_guided, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
