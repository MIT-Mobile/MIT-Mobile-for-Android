package edu.mit.mitmobile2.tour;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_self_guided);

        mapFragment = new TourStopMapFragment();
        listFragment = new TourStopListFragment();

        getFragmentManager().beginTransaction().replace(R.id.tour_frame, mapFragment).commit();
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
