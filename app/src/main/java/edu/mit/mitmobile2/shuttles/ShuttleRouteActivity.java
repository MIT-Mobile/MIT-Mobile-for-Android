package edu.mit.mitmobile2.shuttles;

import android.os.Bundle;

import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.fragment.ShuttleRouteFragment;

public class ShuttleRouteActivity extends MITActivity implements MapFragmentCallback {

    ShuttleRouteFragment fragment = new ShuttleRouteFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shuttle_route);

        getFragmentManager().beginTransaction().replace(R.id.route_screen_frame, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (!fragment.isMapViewExpanded()) {
            super.onBackPressed();
        } else {
            fragment.showListView();
        }
    }

    @Override
    public void setActionBarTitle(String title) {
        setTitle(title);
    }
}
