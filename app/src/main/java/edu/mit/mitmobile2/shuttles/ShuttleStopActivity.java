package edu.mit.mitmobile2.shuttles;

import android.os.Bundle;

import edu.mit.mitmobile2.MITActivity;
import edu.mit.mitmobile2.R;
import edu.mit.mitmobile2.shuttles.fragment.ShuttleStopFragment;

public class ShuttleStopActivity extends MITActivity implements MapFragmentCallback {

    ShuttleStopFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_frame);

        fragment = new ShuttleStopFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_frame, fragment).commit();
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

    @Override
    public void setActionBarSubtitle(String subtitle) {
        getSupportActionBar().setSubtitle(subtitle);
    }
}
